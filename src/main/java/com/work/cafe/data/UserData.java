package com.work.cafe.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserData implements Serializable {

    public String email;
    public String pwd;
    public String nickname;
    public String type;

    public String cafe_name = null;
    public String cafe_id;

    public UserData () {}

    public UserData(String email, String pwd, String nickname) {
        this.email = email;
        this.pwd = pwd;
        this.nickname = nickname;

        this.type = "user";
    }

    public UserData(String email, String pwd, String nickname, String cafe_name, String cafe_idx) {
        this.email = email;
        this.pwd = pwd;
        this.nickname = nickname;
        this.cafe_name = cafe_name;
        this.cafe_id = cafe_idx;

        this.type = "cafe";
    }

    public String getEmail() {
        return email;
    }


}
