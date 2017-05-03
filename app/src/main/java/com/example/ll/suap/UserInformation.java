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


    public UserInformation(String e, String p, String n, String ph, String m, String y, String c, String pe){
        email = e;
        password = p;
        name = n;
        phone = ph;
        makeModel = m;
        year = y;
        color = c;
        permit = pe;
    }
}
