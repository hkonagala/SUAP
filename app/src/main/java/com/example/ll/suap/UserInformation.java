package com.example.ll.suap;

public class UserInformation {
    public String email;
    public String password;
    public String name;
    public String phone;
    public String makeModel;
    public String year;
    public String color;
    public String permit;
    //for matching
    public Long timestamp;
    public Boolean matched;
    public String match;


    public UserInformation(String emai, String pass, String nam, String phon, String makeandmod,
                           String yea, String colo, String permi){
        email = emai;
        password = pass;
        name = nam;
        phone = phon;
        makeModel = makeandmod;
        year = yea;
        color = colo;
        permit = permi;

        //not active yet
        timestamp=null;

        matched=null;
        match=null;



    }
}
