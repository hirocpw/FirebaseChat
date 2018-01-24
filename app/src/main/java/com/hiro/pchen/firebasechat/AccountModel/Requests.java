package com.hiro.pchen.firebasechat.AccountModel;

/**
 * Created by ABB89 on 2017/10/3.
 */

public class Requests {

    public Requests(){

    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Requests(String request_type) {

        this.request_type = request_type;
    }

    private String request_type;

}
