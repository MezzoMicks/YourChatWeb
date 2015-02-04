package de.deyovi.chat.web.controllers;

import de.deyovi.aide.Notice;
import de.deyovi.chat.core.objects.Alert;
import de.deyovi.chat.core.objects.impl.DefaultAlert;
import de.deyovi.chat.web.form.RegisterData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Michi on 15.01.2015.
 */
@Controller
public class LoginController {

    @RequestMapping( value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("login");
        if (error != null) {
            List<Notice> notices = new LinkedList<>();
            if ("credentials".equals(error)) {
                notices.add(new DefaultAlert("alert.login.wrongcredentials", Notice.Level.ERROR, Alert.Lifespan.NORMAL));
            } else if ("internal".equals(error)) {
                notices.add(new DefaultAlert("alert.authentication.internal", Notice.Level.ERROR, Alert.Lifespan.NORMAL));
            }
            modelAndView.addObject("alerts", notices);
        }
        modelAndView.addObject("registerData", new RegisterData());
        return modelAndView;
    }

    @RequestMapping( value = "/register", method = RequestMethod.POST)
    public void register(@ModelAttribute RegisterData registerData ,BindingResult bindingResult, ModelAndView modelAndView) {
        modelAndView.addObject("registerData", registerData);
        modelAndView.setViewName("login");
    }

}
