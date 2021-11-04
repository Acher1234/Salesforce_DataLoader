package com.syntechpro.dataproject.JsonClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SalesforceLogin {
    public SalesforceLogin()//obligatoire d'avoir un no arg Constructor
    {}
    public String issued_at;
    public String token_type;
    public String id;
    public String instance_url;
    public String id_token;
    public String scope;
    public String signature;
    public String refresh_token ;
    public String access_token;
    public static SalesforceLogin deserialize(String json)
    {
        Gson g = new Gson();
        return g.fromJson(json, SalesforceLogin.class);
    }
    public String Json()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    public String stringJson()
    {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
