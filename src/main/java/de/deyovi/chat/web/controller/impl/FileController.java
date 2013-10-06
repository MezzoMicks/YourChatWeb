package de.deyovi.chat.web.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.dao.ImageDAO;
import de.deyovi.chat.core.dao.impl.DefaultImageDAO;
import de.deyovi.chat.core.entities.ImageEntity;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.FileStoreService;
import de.deyovi.chat.core.services.impl.DefaultFileStoreService;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerStatusOutput;
import de.deyovi.chat.web.controller.ControllerStreamOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
public class FileController extends AbstractFormController {

	private static final String DB_IMAGE_PREFIX = "db.image.";
	private static final Logger logger = LogManager.getLogger(FileController.class);
	private static final Mapping PATH_CONTENT= new DefaultMapping("content");
	private static final Mapping PATH_DATA= new DefaultMapping("data");
	private static final Mapping[] PATHES = new Mapping[] { PATH_CONTENT, PATH_DATA };

	private final ImageDAO imageDAO = DefaultImageDAO.getInstance();
	private final FileStoreService fileStore = DefaultFileStoreService.getInstance();
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		String payload = matchedPath.getPayload();
		if (path == PATH_CONTENT || path == PATH_DATA) {
			if (payload.startsWith(DB_IMAGE_PREFIX)) {
				int lastDot = payload.lastIndexOf('.');
				String sizeStr = payload.substring(DB_IMAGE_PREFIX.length(), lastDot);
				long id = Long.parseLong(payload.substring(lastDot));
				ImageSize size = ImageSize.getByPrefix(sizeStr);
				ImageEntity imageEntity = imageDAO.findByID(id);
				byte[] bytes;
				switch (size) {
				default:
				case ORIGINAL:
					bytes = imageEntity.getOriginal();		
					break;
				case PINKY:
					bytes = imageEntity.getPinkynail();		
					break;
				case PREVIEW:
					bytes = imageEntity.getPreview();		
					break;
				case THUMBNAIL:
					bytes = imageEntity.getThumbnail();					
					break;
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				return new ControllerStreamOutput("image/jpeg", bytes.length, bis);
			} else {
				return new ControllerStreamOutput("image/jpeg", -1, fileStore.load(payload));
			}
		} else  {
			ServletContext context = request.getServletContext();
			return getLocalFile(path.getPathAsString(), payload, context);
		}
	}
		
	private ControllerOutput getLocalFile(String path, String filename, ServletContext context) {
		logger.info("looking for resource " + path + "/" + filename);
		File file = new File(context.getRealPath(path), filename);
		logger.info("real path for resource " + context.getRealPath(path) + "/" + filename);
		ControllerOutput output = null;
		if (file.exists()) {
			try {
				String contentType = context.getMimeType(file.getName());
				long size = file.length();
			    String header = "Content-Disposition:inline; filename=\"" + file.getName() + "\"";
				InputStream input = new FileInputStream(file);
				output = new ControllerStreamOutput(contentType, size, input, header);
			} catch (FileNotFoundException e) {
				logger.warn("Requested File " + path + "/" + filename + " not found");
				output = new ControllerStatusOutput(404);
			}
		} else {
			logger.warn("Requested File " + path + "/" + filename + " not found");
			output = new ControllerStatusOutput(404);
		}
		return output;
	}
	
}