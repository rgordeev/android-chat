package ru.rgordeev.chat.data;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("login")
    private String login;

    @SerializedName("token")
    private String token;

    public UserData(String login, String token) {
        this.login = login;
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
