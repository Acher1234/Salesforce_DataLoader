package com.syntechpro.dataproject.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syntechpro.dataproject.JsonClass.SalesforceLogin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;

public class TokenAccessSauvegarde
{
    public static Boolean isConnected(){
        File loginFile = new File(".\\login.txt");
        if(loginFile.length() == 0)
        {
            return false;
        }
        try {
            Scanner scaner = new Scanner(loginFile);
            Gson gson = new GsonBuilder().create();
            SalesforceLogin login = gson.fromJson(scaner.nextLine(),SalesforceLogin.class);
            LocalDateTime lastDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(login.issued_at)), ZoneId.systemDefault());
            if(lastDate.compareTo(LocalDateTime.now().minusHours(8)) > 0)
            {
                return  true;
            }
            return false;
        }catch (Exception e)
        {
            return false;
        }

    }

    public static void writeOnFiles(String Json) {
        System.out.println(Json);
        System.out.println("Sauvegarde on file");
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(".\\login.txt");
            myWriter.write(Json);
            myWriter.flush();
        }catch (Exception e)
        {
            System.out.println("error");
        }
    }

    public static SalesforceLogin getConnection()
    {
        File loginFile = new File(".\\login.txt");
        if(loginFile.length() == 0)
        {
            return null;
        }
        try {
            Scanner scaner = new Scanner(loginFile);
            Gson gson = new GsonBuilder().create();
            SalesforceLogin login = gson.fromJson(scaner.nextLine(),SalesforceLogin.class);
            LocalDateTime lastDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(login.issued_at)), ZoneId.systemDefault());
            if(lastDate.compareTo(LocalDateTime.now().minusHours(8)) > 0)
            {
                return  login;
            }
            return null;
        }catch (Exception e)
        {
            return null;
        }
    }

    public static void logout()
    {
        File loginFile = new File(".\\login.txt");
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(".\\login.txt");
            myWriter.write("");
            myWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
