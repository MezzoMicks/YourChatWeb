package de.deyovi.chat.web.controller.impl;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.ProfileService;
import de.deyovi.chat.web.controller.*;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.controller.annotations.Controller;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Controller
public class ProfileController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(ProfileController.class);
	private static final Mapping PATH_ID = new DefaultMapping("id/");
	private static final Mapping PATH_ID_EDIT = new DefaultMapping("id-edit/");
	private static final Mapping PATH_DELETE = new DefaultMapping("id-edit/delete");
	private static final Mapping PATH_CHANGE = new DefaultMapping("id-edit/change");
	private static final Mapping PATH_ADDIMAGE = new DefaultMapping("id-edit/addimage");
	private static final Mapping PATH_SETAVATAR = new DefaultMapping("id-edit/addavatar");
	private static final Mapping[] PATHES = new Mapping[] { PATH_ID, PATH_ID_EDIT, PATH_DELETE, PATH_CHANGE, PATH_ADDIMAGE, PATH_SETAVATAR };
	
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

    @Inject
	private Instance<ProfileService> profileServiceInstance;
    @Inject
    private Instance<ChatUserService> chatUserServiceInstance;
	
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
			return new ControllerJSONOutput(addImage(user, request, response));
		} else if (path == PATH_SETAVATAR) {
			return new ControllerJSONOutput(setAvatar(user, request, response));
		} else if (path == PATH_ID) {
            String userName = matchedPath.getPayload();
            if (userName == null) {
                userName = "";
            } else {
                userName.trim();
            }
            ChatUser profileUser;
            ChatUserService chatUserService = chatUserServiceInstance.get();
            if (!userName.isEmpty()) {
                profileUser = chatUserService.getByName(userName);
            } else {
                profileUser = user;
            }
            return new ControllerViewOutput("profile", Collections.singletonMap("profileUser", profileUser));
        } else if (path == PATH_ID_EDIT) {
            Map<String, Object> parameters = new HashMap<String, Object>(2);
            parameters.put("profileUser", user);
            parameters.put("editMode", true);
            return new ControllerViewOutput("profile", parameters);
        } else {
			return null;
		}
	}

	private JSONObject addImage(ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> parameters = getParameters(request);
		FileUpload image = (FileUpload) parameters.get(PARAM_IMAGE);
		String title = (String) parameters.get(PARAM_IMAGE_TITLE);
		if (image != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(user + " adds image " + image.getFileName());
			}
            ProfileService profileService = profileServiceInstance.get();
			Long newID = profileService.addGalleryImage(user, image.getFileName(), image.getStream(), title);
			try {
				return new JSONObject().put("id", newID).put("url", response.encodeURL(request.getContextPath() + "/d/db_" + newID + ".jpg"));
			} catch (JSONException e) {
				logger.error(e);
				return null;
			}
		} else {
			return null;
		}
	}

	private JSONObject setAvatar(ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> parameters = getParameters(request);
		FileUpload image = (FileUpload) parameters.get(PARAM_IMAGE);
		String title = (String) parameters.get(PARAM_IMAGE_TITLE);
		if (image != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(user + " sets avatar " + image.getFileName());
			}
            ProfileService profileService = profileServiceInstance.get();
			Long newID = profileService.setAvatarImage(user, image.getFileName(), image.getStream(), title);
			try {
                return new JSONObject().put("id", newID).put("url", response.encodeURL(request.getContextPath() + "/d/db_" + newID + ".jpg"));
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

        ProfileService profileService = profileServiceInstance.get();
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
