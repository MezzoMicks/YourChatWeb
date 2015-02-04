package de.deyovi.chat.web;

import de.deyovi.aide.Outcome;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.MessageEventListener;
import de.deyovi.chat.core.services.impl.JSONMessageConsumer;
import de.deyovi.chat.facades.InputFacade;
import de.deyovi.chat.facades.OutputFacade;
import de.deyovi.chat.facades.SessionFacade;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;

public class ChatSocket extends WebSocketServlet {

	private final static Logger logger = Logger.getLogger(ChatSocket.class);

    @Inject
	private SessionFacade sessionFacade;
    @Inject
	private OutputFacade outputFacade;
    @Inject
    private InputFacade inputFacade;
	
	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		return new ChatUserInbound();
	}
	 
    protected class ChatUserInbound extends StreamInbound implements MessageEventListener {
    	
    	private ChatUser user = null;
    	private String privateSugar = null;
    	private String messageCache = null;
    	private String hashToAcknowledge = null;
    	private String listenId = null;
    	
    	@Override
    	protected void onOpen(WsOutbound outbound) {
    		logger.info("opened connection to " + outbound.toString());
    	}
    	
        @Override
        protected void onTextData(Reader r) throws IOException {
        	StringBuilder builder = new StringBuilder();
        	BufferedReader br = new BufferedReader(r);
        	String line;
        	while ((line = br.readLine()) != null) {
        		builder.append(line);
        	}
        	logger.info(builder.toString());
        	try {
				JSONObject json = new JSONObject(builder.toString());
				String context = json.getString("context");
				String messageid = json.getString("messageid");
				String action = json.getString("action");
				JSONObject requestBody = null;
				if (json.has("body")) {
					requestBody = json.getJSONObject("body");
				}
				JSONObject result = new JSONObject();
				result.put("messageid", messageid);
				if ("session".equals(context)) {
					if ("sugar".equals(action)) {
						String username = requestBody.getString("username");
						String sugar = sessionFacade.getSugar();
						privateSugar = sugar + username;
						result.put("success", true);
						result.put("sugar", sugar);
					} else if ("login".equals(action)) {
						String username = requestBody.getString("username");
						String password = requestBody.getString("password");
						Outcome<ChatUser> outcome = sessionFacade.login(username, password, privateSugar);
						user = outcome.getResult();
						if (user != null) {
							result.put("success", true);
							result.put("listenId", user.getListenId());
						} else {
							result.put("success", false);
						}
					} else if ("register".equals(action)) {
						String username = requestBody.getString("username");
						String password = requestBody.getString("password");
						String inviteKey = requestBody.getString("inviteKey");
						Outcome<ChatUser> outcome = sessionFacade.register(username, password, inviteKey, privateSugar);
						user = outcome.getResult();
						if (user != null) {
							result.put("success", true);
							result.put("listenId", user.getListenId());
						} else {
							result.put("success", false);
						}
					}
				} else if (user != null) {
					if ("listen".equals(context)) {
						if ("start".equals(action)) {
							user.addMessageEventListener(this);
							listenId = requestBody.getString("listenid");
							result.put("success", true);
						} else if ("acknowledge".equals(action)) {
							String hash = requestBody.getString("hash");
							if (hashToAcknowledge != null && hash.equals(hashToAcknowledge)) {
								synchronized (user) {
									hashToAcknowledge = null;
									messageCache = null;
								}
								// let's check if we missed something
								messageRecieved(user);
								result.put("success", true);
							} else {
								result.put("success", false);
							}
						} else if ("peek".equals(action)) {
							messageRecieved(user);
							sendCache();
                            result.put("success", true);
						} else if ("refresh".equals(action)) {
                            result.put("success", true);
                            result.put("data", outputFacade.refresh(user, Locale.GERMAN));
                            logger.debug(result.toString());
                        }
					}  else if ("input".equals(context)) {
                        if ("talk".equals(action)) {
                            result.put("success", true);
                            inputFacade.talk(user, requestBody.getString("message"), null, null);
                        }

                    }
                }  else {
					result.put("success", false);
					logger.error("Requested context != 'session' with no user ");
				}
				send(result);
			} catch (JSONException e) {
			}
        }

		@Override
		protected void onBinaryData(InputStream is) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
			IOUtils.copy(is, baos);
			getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(baos.toByteArray()));
		}

		private void send(JSONObject json) throws IOException {
			send(json.toString());
		}
		
		private void send(String data) throws IOException {
			getWsOutbound().writeTextMessage(CharBuffer.wrap(data));
		}
		
		@Override
		protected void onClose(int status) {
    		System.out.println("got Closed :(");
		}
        
		public void sendCache() {
			synchronized (user) {
				if (messageCache != null && !messageCache.isEmpty()) {
					JSONObject json = new JSONObject();
					try {
						hashToAcknowledge = Integer.toHexString(messageCache.hashCode());
						json.put("listenid", listenId);
						json.put("message", new JSONObject(messageCache));
                        json.put("hash", hashToAcknowledge);
						send(json);
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		@Override
		public void messageRecieved(ChatUser user) {
			boolean doSend = false;
			synchronized (user) {
				if (hashToAcknowledge == null) {
					JSONMessageConsumer consumer = new JSONMessageConsumer();
					outputFacade.listen(consumer, Locale.GERMAN, user, listenId);
					messageCache = consumer.getResult();
					doSend = true;
				}
			}
			if (doSend) {
				sendCache();
			}
		}
    }
    
}
