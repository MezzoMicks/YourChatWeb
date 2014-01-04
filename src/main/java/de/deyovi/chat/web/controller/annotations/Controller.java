package de.deyovi.chat.web.controller.annotations;


public @interface Controller {

	public boolean disabled() default false;
	
}
