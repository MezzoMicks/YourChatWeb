package de.deyovi.chat.web.controller.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Room.RoomInfo;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.RoomService;
import de.deyovi.chat.core.services.TranslatorService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.services.impl.DefaultRoomService;
import de.deyovi.chat.core.services.impl.ResourceTranslatorService;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.web.controller.Controller;
import de.deyovi.chat.web.controller.ControllerHTMLOutput;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerStatusOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.json.JSONObject;
import de.deyovi.json.impl.DefaultJSONObject;

public class OutputController implements Controller {

	private static final Logger logger = LogManager.getLogger(OutputController.class);
	
	private static final Mapping PATH_LISTEN = new DefaultMapping("listen");
	private static final Mapping PATH_REFRESH = new DefaultMapping("refresh");
	private static final Mapping[] PATHES = new Mapping[] { PATH_LISTEN, PATH_REFRESH };
	private static final Message[] NO_MESSAGES = new Message[0];
	private final static int ALIVE_CYCLES = 100;
	private static AtomicLong ANCHOR_COUNT = new AtomicLong();

	private static final String STOP_SCRIPT = 
			"<script type=\"text/javascript\">" + //
			"	stop();" + //
			"</script>";
	
	private static final String REFRESH_SCRIPT = 
			"<script type=\"text/javascript\">" + //
			"	refresh();" + //
			"</script>";
	
	private static final String PROFILE_SCRIPT = 
			"<script type=\"text/javascript\">" + //
			"	openProfile('%s');" + //
			"</script>";
	
	
	public static final String USER_TAG  = 
			"<span class=\"username\" style=\"color:#%s\">" + //
			"%s" + //
			"</span>";
	

	public static final String USER_ALIAS_TAG  = 
			"<span style=\"color:#%s\" data-animation=\"false\" data-title=\"%s\" data-placement=\"right\" onmouseover=\"toolify(this);\" class=\"useralias\">" + //
			"%s" + //
			"</span>";
	
	private static Map<String, Integer> senslessCycleMap = new ConcurrentHashMap<String, Integer>();
    
	private enum OutputType {

		SYNC("sync"), ASYNC("async"), REGISTER("register");

		private final String id;

		private OutputType(String id) {
			this.id = id;
		}

		private static OutputType getById(String id) {
			if (id != null) {
				id = id.trim().toLowerCase();
				for (OutputType value : values()) {
					if (value.id.equals(id)) {
						return value;
					}
				}
			}
			return SYNC;
		}
	}
	
	private final TranslatorService translatorService = ResourceTranslatorService.getInstance();
	private final RoomService roomServce = DefaultRoomService.getInstance();
	private final ChatUserService userService = DefaultChatUserService.getInstance();
	
	@Override
	public Mapping[] getMappings() {
		return PATHES;
	}

	@Override
	public ControllerOutput process(MatchedMapping path, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		String lang = request.getParameter("lang");
		if (lang == null) {
			lang = "de";
		}
		Locale locale = new Locale(lang);

		if (path.equals(PATH_LISTEN)) {
			return output(user, locale, request);
		} else if (path.equals(PATH_REFRESH)) {
			return new ControllerJSONOutput(refresh(user, locale));
		} else {
			return null;
		}
	}

	private JSONObject refresh(ChatUser user, Locale locale) {
		JSONObject result = new DefaultJSONObject();
		result.put("away", user.isAway());
		String font = user.getSettings().getFont();
		result.put("font", (font != null ? font.trim() : null));
		RoomInfo info = user.getCurrentRoom().getInfoForUser(user);
		result.put("room", ChatUtils.escape(info.getName()));
		result.put("background", info.getBgColor());
		result.put("foreground", info.getFgColor());
		result.put("backgroundimage", info.getBgImage());
		ChatUser[] myRoomMates = info.getUsers();
		for (ChatUser roomUser : myRoomMates) {
			result.push("users", jsonifyUser(roomUser));
		}
		for (Segment media : info.getMedia()) {
			JSONObject jsonMedia = new DefaultJSONObject();
			jsonMedia.put("link", media.getContent());
			String name = media.getAlternateName();
			if (name == null) {
				name = media.getContent();
			} else  {
				if (name.charAt(0) == '$') {
					name = translatorService.translate(name.substring(1), locale);
				}
				name = ChatUtils.escape(name);
			}
			jsonMedia.put("name", name);
			jsonMedia.put("preview", media.getPreview());
			jsonMedia.put("pinky", media.getPinky());
			jsonMedia.put("type", media.getType());
			jsonMedia.put("user", ChatUtils.escape(media.getUser()));
			result.push("medias", jsonMedia);
		}
		List<ChatUser> otherUsers = userService.getLoggedInUsers();
		for (ChatUser roomMate : myRoomMates) {
			otherUsers.remove(roomMate);
		}
		for (ChatUser otherUser : otherUsers) {
			result.push("others", jsonifyUser(otherUser));
		}
		List<RoomInfo> openRooms = roomServce.getOpenRooms();
		for (RoomInfo room : openRooms) {
			JSONObject jsonRoom = new DefaultJSONObject();
			jsonRoom.put("name", ChatUtils.escape(room.getName()));
			jsonRoom.put("color", room.getBgColor());
			ChatUser[] users = room.getUsers();
			jsonRoom.put("users", (users != null ? users.length : null));
			result.push("rooms", jsonRoom);
		}
		return result;
	}

	private JSONObject jsonifyUser(ChatUser user) {
		JSONObject json = new DefaultJSONObject();
		json.put("username", ChatUtils.escape(user.getUserName()));
		json.put("alias", ChatUtils.escape(user.getAlias()));
		json.put("color", user.getSettings().getColor());
		json.put("guest", user.isGuest());
		json.put("away", user.isAway());
		json.put("avatar", user.getProfile().getAvatar().getID());
		return json;
	}

	private Message[] listen(ChatUser user, String listenId) {
		logger.debug("Called listen() on user[" + user+ "]");
		if (user == null) {
			return null;
		} else if (listenId != null) {
			List<Message> result = new LinkedList<Message>();
			if (!listenId.equals(user.getListenId())) {
				SystemMessage message = new SystemMessage(null, 0l, MessagePreset.DUPLICATESESSION);
				result.add(message);
			} else {		
				Message message;
				while ((message = user.read()) != null) {
					result.add(message);
				}
			}
			logger.debug("returning " + result.size() + " messages");
			return result.toArray(new Message[result.size()]);
		} else {
			return NO_MESSAGES;
		}
	}
	
	private ControllerOutput output(ChatUser user, Locale locale, HttpServletRequest request) {
		ControllerOutput result = null;
		try {
			OutputType type = OutputType.getById(request.getParameter("output"));
			logger.debug(request.getRemoteAddr() + " does " + type);
			if (user != null) {
				final String listenid = request.getParameter("listenid");
				logger.debug(user + 	" starts to listen with id " + listenid);
				
				String htmlString = request.getParameter("html");
				boolean html = htmlString == null || Boolean.parseBoolean(htmlString);
				if (type == OutputType.ASYNC) {
					Message[] messages = listen(user, listenid);
					StringBuilder writer = new StringBuilder();
					Trigger trigger = decodeMessages(writer, messages, locale, html, true);
					if (html) {
						switch (trigger) {
						case REFRESH:
							writer.append(REFRESH_SCRIPT);
							break;
						case STOP:
							writer.append(STOP_SCRIPT);
							break;
						case EMPTY:
							Integer senselessCycles = senslessCycleMap.get(listenid);
							if (senselessCycles == null) {
								senselessCycles = 0;
							}
							if (++senselessCycles % ALIVE_CYCLES == 0) {
								senselessCycles = 0;
								writeDummy(writer, 16);
							}
							senslessCycleMap.put(listenid, senselessCycles);
							break;
						default:
							break;
						}
						result = new ControllerHTMLOutput(writer.toString());
					} else if (trigger == Trigger.STOP) {
						result = new ControllerStatusOutput(307);
					}
				} else if (type == OutputType.REGISTER) {
					logger.info(user + 	" registers to Listen");
					user.setListenerTime(System.currentTimeMillis());
					user.alive();
					user.getCurrentRoom().join(user);
					result = new ControllerJSONOutput(new DefaultJSONObject("listenId", user.getListenId()));
				}
			} else {
				result = new ControllerHTMLOutput("No User!");
			}
		} catch (IOException ioex) {
			logger.error("Error writing to HTTP", ioex);
		}
		return result;
	}

	public enum Trigger {
		NONE, REFRESH, STOP, EMPTY
	}

	public Trigger decodeMessages(Appendable appender, Message[] messages, Locale locale, boolean html, boolean live) throws IOException {
		boolean refresh = false;
		boolean stop = false;
		boolean empty = true;
		if (messages == null) {
			stop = true;
		} else {
			for (Message msg : messages) {
				empty = false;
				boolean profile = false;
				StringBuilder sb = new StringBuilder();
				StringBuilder lineBuilder = new StringBuilder();
				String profileScript = null;
				MessagePreset code = MessagePreset.getByCode(msg.getCode());
				switch (code) {
				case TIMEOUT:
				case DUPLICATESESSION:
					stop = true;
					break;
				case CHANNEL_BG_CHANGED:
				case CHANNEL_FG_CHANGED:
				case CHANNEL_NOTALLOWED:
				case CLOSE_CHANNEL:
				case CREATE_DONE:
				case INVITETO_USER:
				case INVITE_USER:
				case JOIN_CHANNEL:
				case LEFT_CHANNEL:
				case OPEN_CHANNEL:
				case OPEN_CHANNEL_ALREADY:
				case SWITCH_CHANNEL:
				case MESSAGE:
				case SETTINGS:
				case USER_AWAY:
				case USER_BACK:
				case USER_ALIAS_SET:
				case USER_ALIAS_CLEARED:
				case CLEAR_LOG:
				case CLEAR_MEDIA:
				case REFRESH:
					refresh = true;
					break;
				case PROFILE_OPEN:
					profile = true;
					break;
				case DEFAULT:
					lineBuilder.append("|ORIGIN|: ");
				}
				Segment[] segments = msg.getSegments();
				if (segments != null) {
					for (Segment seg : segments) {
						String content = seg.getContent();
						switch (seg.getType()) {
						case TEXT:
							if (content.charAt(0) == '$') {
								if (profile && html && live) {
									if (content.startsWith("$PROFILE_OPEN")) {
//										Map<String, String> map = translatorService.;
//										String profileUser = map.get("user");
//										if (profileUser != null) {
//											profileScript = String.format(PROFILE_SCRIPT, profileUser);
//										}
									}
								}
								content = translatorService.translate(content, locale);
							}
							if (html) {
								content = ChatUtils.escape(content);
							}
							break;
						case DOCUMENT:
						case UNKNOWN:
						case VIDEO:
						case IMAGE:
						case WEBSITE:
							refresh = true;
							if (html) {
								String text = seg.getAlternateName() != null ? seg.getAlternateName() : seg.getContent();
								String typeClass = "icon-";
								switch (seg.getType()) {
								case IMAGE:
									typeClass += "picture";
									break;
								case DOCUMENT:
									typeClass += "text";
									break;
								case VIDEO:
									typeClass += "film";
									break;
								case WEBSITE:
									typeClass += "globe";
									break;
								default:
								case UNKNOWN:
									typeClass += "question";
									break;
								}
								String anchorID = "linkNo" + ANCHOR_COUNT.getAndIncrement();
								if (live && seg.getPreview() != null) {
									text = StringEscapeUtils.escapeHtml4(text);
									content = String.format("<a id=" + anchorID + " target=\"_blank\" href=\"%1$s\" data-preview=\"%3$s\" data-pinky=\"%4$s\"><i class=\"" + typeClass + "\" />&nbsp;%2$s</a><script type=\"text/javascript\">tooltipify($('#" + anchorID + "'));</script>", content, text, seg.getPreview(), seg.getPinky()); 
								} else {
									content = String.format("<a id=" + anchorID + " target=\"_blank\" href=\"%1$s\"><i class=\"" + typeClass + "\" />&nbsp;%2$s</a>", content, text); 
								}
							} else {
								content = seg.getContent();
							}
							break;
						default:
							break;
						}
						lineBuilder.append(content);
						lineBuilder.append(' ');
					}
					String line;
					if (html) {
						sb.append("<p>");
					}
					ChatUser origin = msg.getOrigin();
					if (origin != null) {
						String name = origin.getUserName();
						if (html) {
							if (origin.getAlias() != null) {
								name = String.format(USER_ALIAS_TAG, origin.getSettings().getColor(), StringEscapeUtils.escapeHtml4(name), StringEscapeUtils.escapeHtml4(origin.getAlias()));
							} else {
								name = String.format(USER_TAG, origin.getSettings().getColor(), StringEscapeUtils.escapeHtml4(name));
							}
						}
						line = lineBuilder.toString();
						line = line.replace("|ORIGIN|", name);
					} else if (html) { 
						line = lineBuilder.toString();
					} else {
						line = lineBuilder.toString();
					}
					if (html) {
						line = makeCursive(line);
					}
					sb.append(line);
					sb.append('\n');
					if (html) {
						sb.append("</p>");
					}
					if (profileScript != null) {
						appender.append(profileScript);
					}
					appender.append(sb);
				}
			}
		}
		if (stop) {
			return Trigger.STOP;
		} else if (refresh) {
			return Trigger.REFRESH;
		} else if (empty) {
			return Trigger.EMPTY;
		} else {
			return Trigger.NONE;
		}
	}
	
	private static String makeCursive(String content) {
		int ixOfAst;
		boolean iOpen = false;
		int offset = 0;
		while ((ixOfAst = content.indexOf('*', offset)) >= 0) {
			offset = ixOfAst + 1;
			// if there's no open i-tag
			if (!iOpen) {
				// and the next char is 'the end'
				char next;
				if (content.length() <= (ixOfAst + 1)) {
					// skip this one
					continue;
				// or is a whitespace or another asterisk
				} else if ((next = content.charAt(ixOfAst + 1)) == '*' || Character.isWhitespace(next)) {
					// skip this one
					continue;
				}
			}
			String before = content.substring(0, ixOfAst);
			String after = content.substring(ixOfAst + 1);
			content = before;
			if (iOpen) {
				content += "</i>";
				iOpen = false;
				offset += 3;
			} else {
				content += "<i>";
				iOpen = true;
				offset += 2;
			}
			content += after;
		}
		if (iOpen) {
			content += "</i>";
		}
		return content;
	}
	
	private static void writeDummy(Appendable appender, int length) throws IOException {
		appender.append("<!-- ");
		Random rnd = new Random();
		while (length-- > 0) {
			appender.append(new Integer((rnd.nextInt(10)+48)).toString());
		}
		appender.append("-->");
	}
	
}
