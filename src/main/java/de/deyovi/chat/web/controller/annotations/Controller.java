package de.deyovi.chat.web.controller.annotations;


import javax.ejb.Singleton;

public @interface Controller {

	public boolean disabled() default false;
	
}
