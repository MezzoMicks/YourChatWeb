package de.deyovi.chat.web.controller.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.services.ProfileService;
import de.deyovi.chat.core.services.impl.DefaultProfileService;
import de.deyovi.chat.web.controller.ControllerHTMLOutput;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

public class ProfileController extends AbstractFormController {


	private static final Logger logger = LogManager.getLogger(ProfileController.class);
	private static final Mapping PATH_DELETE = new DefaultMapping("delete");
	private static final Mapping PATH_CHANGE = new DefaultMapping("change");
	private static final Mapping PATH_ADDIMAGE = new DefaultMapping("addimage");
	private static final Mapping PATH_SETAVATAR = new DefaultMapping("addavatar");
	private static final Mapping[] PATHES = new Mapping[] { PATH_DELETE, PATH_CHANGE, PATH_ADDIMAGE, PATH_SETAVATAR };
	
	private static final String PARAM_IMAGE = "image";
	private static final String PARAM_IMAGE_TITLE = "imageTitle";
	private static final String PARAM_IMAGE_ID = "imageId";
	private static final String PARAM_FIELD = "field";
	private static final String PARAM_VALUE = "value";
	private static final String FIELD_ABOUT = "about";
	private static final String FIELD_ADDITIONAL = "additional";
	private static final String FIELD_GENDER = "gender";
	private static final String FIELD_BIRTHDAY = "birthday";
	
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	
	private final ProfileService profileService = DefaultProfileService.getInstance();
	
	public Mapping[] getMappings() {
		return PATHES;
	}

	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_DELETE) {
			delete(user, request);
			return new ControllerHTMLOutput(null);
		} else if (path == PATH_CHANGE) {
			change(user, request);
			return new ControllerHTMLOutput(null);
		} else if (path == PATH_ADDIMAGE) {
			return new ControllerJSONOutput(addImage(user, request));
		} else if (path == PATH_SETAVATAR) {
			return new ControllerJSONOutput(setAvatar(user, request));
		} else {
			return null;
		}
	}
	
	private JSONObject addImage(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		FileUpload image = (FileUpload) parameters.get(PARAM_IMAGE);
		String title = (String) parameters.get(PARAM_IMAGE_TITLE);
		if (image != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(user + " adds image " + image.getFileName());
			}
			Long newID = profileService.addGalleryImage(user, image.getFileName(), image.getStream(), title);
			try {
				return new JSONObject().put("id", newID);
			} catch (JSONException e) {
				logger.error(e);
				return null;
			}
		} else {
			return null;
		}
	}

	private JSONObject setAvatar(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		FileUpload image = (FileUpload) parameters.get(PARAM_IMAGE);
		String title = (String) parameters.get(PARAM_IMAGE_TITLE);
		if (image != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(user + " sets avatar " + image.getFileName());
			}
			Long newID = profileService.setAvatarImage(user, image.getFileName(), image.getStream(), title);
			try {
				return new JSONObject().put("id", newID);
			} catch (JSONException e) {
				logger.error(e);
				return null;
			}
		} else {
			return null;
		}
	}
	
	
	private void delete(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		Long id = Long.parseLong((String) parameters.get(PARAM_IMAGE_ID));
		profileService.deleteImage(user, id);
	}
	
	private void change(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		Profile profile = user.getProfile();
		String field = (String) parameters.get(PARAM_FIELD);
		String value = (String) parameters.get(PARAM_VALUE);
		if (value != null) {
			if (FIELD_ABOUT.equals(field)) {
				if (logger.isDebugEnabled()) {
					logger.debug(user + " sets about to " + value);
				}
				profile.setAbout(value);
			} else if (FIELD_ADDITIONAL.equals(field)) {
				if (logger.isDebugEnabled()) {
					logger.debug(user + " sets additional to " + value);
				}
				profile.setAdditionalInfo(value);
			} else if (FIELD_BIRTHDAY.equals(field)) {
				if (logger.isDebugEnabled()) {
					logger.debug(user + " sets birthday to " + value);
				}
				Date birthday = null;
				if (value != null && !value.isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
					try {
						birthday = sdf.parse(value);
						profile.setDateOfBirth(birthday);
					} catch (ParseException pe) {
						logger.error("Couldn't parse birthday for " + user + " value = '" + value + "'");
					}
				} else {
					profile.setDateOfBirth(null);
				}
			} else if (FIELD_GENDER.equals(field)) {
				if (logger.isDebugEnabled()) {
					logger.debug(user + " sets gender to " + value);
				}
				int gender = 0;
				if (value != null && !value.isEmpty()) {
					try {
						gender = Integer.parseInt(value);
						profile.setGender(gender);
					} catch (NumberFormatException nfe) {
						logger.error("Couldn't parse gender for " + user + " value = '" + value + "'");
					}
				} else {
					profile.setGender(0);
				}
			}
		}
	}
	
}
