package com.unifei.stefano.lab_ead_app.operations;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class OperationLogin extends Operation {
    private String token;
    private String mName;
    private int timeoutLimit;

    private String reqEmail;
    private String reqPassword;
    private String accType;

    public OperationLogin(String email, String password, Activity sender) throws JSONException {
        super("/login", sender);

        JSONObject loginJSON = new JSONObject();

        loginJSON.put("email", email);
        loginJSON.put("password", password);

        this.setRequest(loginJSON);

        this.reqEmail = email;
        this.reqPassword = password;
    }

    @Override
    public void setResponse(String response) throws JSONException {
        super.setResponse(response);
        JSONObject json = new JSONObject(response);
        if(json.has("token")){
            this.token = json.getString("token");
        }
        if(json.has("name")){
            this.mName = json.getString("name");
        }
        if(json.has("accType")){
            this.accType = json.getString("accType");
        }
        if(json.has("timeout")){
            this.timeoutLimit = Integer.parseInt(json.getString("timeout"));
        }
    }

    @Override
    public void resetOperation(){
        super.resetOperation();
        this.token = null;
        this.timeoutLimit = -1;
        this.reqPassword = null;
        this.reqEmail = null;
        this.mName = null;
        // this.accType = null;
    }

    public String getAccType() {return accType;}
    public String getmName() {return mName;}
    public String getToken() { return token; }
    public int getTimeoutLimit() { return timeoutLimit; }
    public String getReqPassword() { return reqPassword; }
    public String getReqEmail() { return reqEmail; }
//    public String getAccType() { return accType; }

}
