package com.syntechpro.dataproject.Utils;

import java.util.HashMap;
import java.util.Map;

public class StaticVariable {
    public static  String clientID = "3MVG9SOw8KERNN08SR0JCYAKyjZrVrfYbMS0HSkX6jCXpCltTXw.4qOEXHuD2iRFy8xdSNOuhn7xY9BaG_a.g";
    public static String clientSecret = "427CE7BA42DA25F94DA397334D029351B9931114EFFEAB6FE5A35A6D00844A0F";
    public static String URLLogin = "https://login.salesforce.com/";
    public static String URLLoginTest = "https://test.salesforce.com/";
    public static Map<String,Class> salesforceType= new HashMap<String,Class>(){{
        put("reference", Reference.class);
        put("string",String.class );
        put("picklist",String.class );
        put("textarea",String.class );
        put("string",String.class );
        put("double",double.class );
        put("currency",double.class );
        put("int",Integer.class );
        put("address",String.class);
        put("phone",String.class);
        put("url",String.class);


    }};;
}
