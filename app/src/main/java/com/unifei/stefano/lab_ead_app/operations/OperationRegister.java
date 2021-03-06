package com.unifei.stefano.lab_ead_app.operations;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class OperationRegister extends Operation{
    private String reqEmail;
    private String reqPassword;
    private String reqName;

    public OperationRegister(String email, String password, String name, Activity sender) throws JSONException {
        super("/register", sender);

        JSONObject registerJSON = new JSONObject();

        registerJSON.put("email", email);
        registerJSON.put("password", password);
        registerJSON.put("name", name);

        this.setRequest(registerJSON);

        this.reqEmail = email;
        this.reqPassword = password;
        this.reqName = name;
    }

    @Override
    public void resetOperation(){
        super.resetOperation();
        this.reqPassword = null;
        this.reqEmail = null;
        this.reqName = null;
    }

    public String getReqPassword() { return reqPassword; }
    public String getReqEmail() { return reqEmail; }
    public String getReqName() { return reqName; }

}
