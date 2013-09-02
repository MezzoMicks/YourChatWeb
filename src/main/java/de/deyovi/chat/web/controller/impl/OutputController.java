package de.deyovi.chat.web.controller.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.services.TranslatorService;
import de.deyovi.chat.core.services.impl.ResourceTranslatorService;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.web.controller.Controller;
import de.deyovi.chat.web.controller.ControllerHTMLOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

public class OutputController implements Controller {

	private static final Logger logger = LogManager.getLogger(OutputController.class);
	
	private static final Mapping PATH_LISTEN = new DefaultMapping("listen");

	private static final Message[] NO_MESSAGES = new Message[0];
	private final static int ALIVE_CYCLES = 100;
	
	private static final String HEADER = 
			"<HTML><HEAD><TITLE>Chat-Output</TITLE>" + //
			"<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">" +//		
			"<style>" + //
			" body {background:transparent; line-height:1em; margin: 0 8px; color:#000; line-height:normal}" + //
			" .username {font-weight:bold; }" +//
			" .useralias {font-weight:bold; font-style:italic; }" +//
			"</style>" + //
			"<script type=\"text/javascript\">" +//
			"   function openProfile(usernam) {parent.openProfile(username);}" + //
			"   function refresh() {parent.refresh();}" + //
			"   function stop() {return false}" + //
			"   function toolify(element) {parent.toolify(element); return false;}" + //
			"	function preview(element, click) {parent.preview(element, click); return false;}"  +//
			"	function moves() {" +
			" 		if (typeof parent.scrolling == 'undefined' || parent.scrolling) window.scroll(1,5000000); window.setTimeout(\"moves()\", 100);} moves();" + //
			"</script>" + //
			"</HEAD>" + //
			"<BODY style=\"overflow-x: hidden;\">" 
			//+ //
			//"<div style=\"position:absolute; bottom:0; right:0;\"><input type=\"checkbox\" value=\"true\" onclick=\"toggleScrolling(this);\"/>&nbsp;auto-scroll</div>";
			;
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

	private static final Mapping[] PATHES = new Mapping[] { PATH_LISTEN };
	
	private final TranslatorService translatorService = ResourceTranslatorService.getInstance();
	
	@Override
	public Mapping[] getMappings() {
		return PATHES;
	}

	@Override
	public ControllerOutput process(MatchedMapping path, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		if (path.equals(PATH_LISTEN)) {
			output(user, request, response);
			return new ControllerHTMLOutput(null);
		} else {
			return null;
		}
	}

	private Message[] listen(ChatUser user, String listenId) {
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
			return result.toArray(new Message[result.size()]);
		} else {
			return NO_MESSAGES;
		}
	}
	
	private void output(ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		response.setStatus(200);
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private, max-stale=0, post-check=0, pre-check=0");
		response.setHeader("Connection", "close");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); // HTTP 1.1 
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			OutputType type = OutputType.getById(request.getParameter("output"));
			logger.debug(request.getRemoteAddr() + " does " + type);
			if (user != null) {
				final String listenid = request.getParameter("listenid");
				logger.debug(user + 	" starts to listen with id " + listenid);
				String lang = request.getParameter("lang");
				if (lang == null) {
					lang = "de";
				}
				Locale locale = new Locale(lang);
	
				String htmlString = request.getParameter("html");
				boolean html = htmlString == null || Boolean.parseBoolean(htmlString);
				if (type == OutputType.SYNC) {
					if(html) {
						writer.write(HEADER);
						// 5 KB initial load... 
						// TODO configurable BURST-Mode (some initial MB to trick Filters!)
						writeDummy(writer, 1024 * 5);
						writer.flush();
						response.flushBuffer();
					}
					try {
						boolean stop = false;
						int senselessCycles = 0;
						do {
							Thread.sleep(200l);
							Message[] listen = listen(user, listenid);
							switch (decodeMessages(writer, listen, locale, html, true)) {
							case STOP:
								stop = true;
								if (html) {
									writer.write(STOP_SCRIPT);
								}
								break;
							case REFRESH:
								if (html) {
									senselessCycles = 0;
									writer.write(REFRESH_SCRIPT);
								}
								break;
							case EMPTY:
								if (html) {
									if (++senselessCycles % ALIVE_CYCLES == 0) {
										senselessCycles = 0;
										writeDummy(writer, 16);
									}
								}
								break;
							case NONE:
							default:
								break;
							};
							writer.flush();
							response.flushBuffer();
						} while (!stop);
							
						if (stop) {
							logger.info("Obsolet stream for " + user.getUserName() + " interrupted!");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (type == OutputType.ASYNC) {
					Message[] messages = listen(user, listenid);
					Trigger trigger = decodeMessages(writer, messages, locale, html, true);
					if (html) {
						switch (trigger) {
						case REFRESH:
							writer.write(REFRESH_SCRIPT);
							break;
						case STOP:
							writer.write(STOP_SCRIPT);
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
					} else if (trigger == Trigger.STOP) {
						response.sendError(307);
					}
				} else if (type == OutputType.REGISTER) {
					logger.info(user + 	" registers to Listen");
					user.setListenerTime(System.currentTimeMillis());
					user.alive();
					user.getCurrentRoom().join(user);
					writer.write(user.getListenId());
				}
			} else {
				writer.write("No User!");
			}
		} catch (IOException ioex) {
			logger.error("Error writing to HTTP", ioex);
		}
	}

	public enum Trigger {
		NONE, REFRESH, STOP, EMPTY
	}

	public Trigger decodeMessages(Writer writer, Message[] messages, Locale locale, boolean html, boolean live) throws IOException {
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
						case VIDEO:
						case IMAGE:
						case WEBSITE:
							refresh = true;
							if (html) {
								String text = seg.getAlternateName() != null ? seg.getAlternateName() : seg.getContent();
								if (live && seg.getPreview() != null) {
									text = StringEscapeUtils.escapeHtml4(text);
									content = String.format("<a target=\"_blank\" onmouseover=\"preview(this, false);\" href=\"%1$s\" data-preview=\"%3$s\">%2$s</a>", content, text, seg.getPreview()); 
								} else {
									content = String.format("<a target=\"_blank\" href=\"%1$s\">%2$s</a>", content, text); 
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
						sb.append("<br />");
					}
					if (profileScript != null) {
						writer.write(profileScript);
					}
					writer.write(sb.toString());
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
	
	private static void writeDummy(Writer writer, int length) throws IOException {
		writer.write("<!-- ");
		Random rnd = new Random();
		while (length-- > 0) {
			writer.write((rnd.nextInt(10)+48));
		}
		writer.write("-->");
	}
	
}