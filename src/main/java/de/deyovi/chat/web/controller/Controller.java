package de.deyovi.chat.web.controller;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {

  public Mapping[] getMappings();

  public ControllerOutput process(MatchedMapping path, ChatUser user, HttpServletRequest request, HttpServletResponse response);

}