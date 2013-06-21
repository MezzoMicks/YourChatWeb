package de.deyovi.chat.web.controller;

public enum Method {
     GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
     
     private final String name;
     
     private Method(String name){
       this.name = name;
     }
      
     public static Method getByName(String name) {
       if (name != null) {
         for (Method value : values()) {
           if (value.name.equals(name)) {
             return value;
           }
         }
       }
       return null;
     } 
}
