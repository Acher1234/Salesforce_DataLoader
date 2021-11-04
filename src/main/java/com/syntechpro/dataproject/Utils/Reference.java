package com.syntechpro.dataproject.Utils;

import com.google.gson.JsonElement;

public class Reference
{
    @Override
    public String toString() {
        return "Reference{" +
                "objectName='" + objectName + '\'' +
                '}';
    }

    String objectName = "";
    public Reference(JsonElement json)
    {
        for(JsonElement temp:json.getAsJsonObject().get("referenceTo").getAsJsonArray())
        {
            objectName += temp.getAsString() + ";";
        }
    }
}