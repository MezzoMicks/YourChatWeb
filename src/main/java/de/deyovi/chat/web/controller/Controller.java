package de.deyovi.chat.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

public interface Controller {

  public Mapping[] getMappings();

  public ControllerOutput process(MatchedMapping path, ChatUser user, HttpServletRequest request, HttpServletResponse response);

}