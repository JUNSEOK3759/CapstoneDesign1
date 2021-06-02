package com.work.cafe.data;

public class AdminData {


    public String email;
    public String pwd;
    public String nickname;
    public String type;

    public String cafe_name = null;
    public String cafe_id;

    public AdminData () {}

    public AdminData(String email, String pwd, String nickname) {
        this.email = email;
        this.pwd = pwd;
        this.nickname = nickname;

        this.type = "user";
    }

    public AdminData(String email, String pwd, String nickname, String cafe_name, String cafe_id) {
        this.email = email;
        this.pwd = pwd;
        this.nickname = nickname;
        this.cafe_name = cafe_name;
        this.cafe_id = cafe_id;

        this.type = "cafe";
    }

    public String getEmail() {
        return email;
    }

}
