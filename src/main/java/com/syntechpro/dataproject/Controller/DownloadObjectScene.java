package com.syntechpro.dataproject.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.syntechpro.dataproject.JsonClass.SalesforceLogin;
import com.syntechpro.dataproject.Utils.Query;
import com.syntechpro.dataproject.Utils.Sobject;
import com.syntechpro.dataproject.Utils.SobjectRepresentation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public class DownloadObjectScene {
    public ListView listView;
    public String query = "select id";
    public String objQuery;
    public List<Sobject> sobjects;
    public String APIName;
    public int count;
    public String Date = "";
    public SalesforceLogin logInfos;
    public SobjectRepresentation downSobject;



    public void Start()
    {

        List<String> FieldsStr = downSobject.getAllValue();
        for (String f:FieldsStr)
        {
            CheckBox temp = new CheckBox();
            temp.setText(f);
            temp.setOnAction((e)->
            {
                String value = "," + downSobject.getFieldsAPI(((CheckBox) e.getSource()).getText());
                if (((CheckBox) e.getSource()).isSelected()) {
                    query += query.contains(value) ? "" : value;
                } else {
                    query = query.replace(value, "");
                }
                System.out.println(query + objQuery);
            });
            listView.getItems().add(temp);
            objQuery = " from " + downSobject.name;
        }
    }

    public void DownloadBatch() {
        String condition = ";";
        if(!Date.isEmpty())
            condition = " where createdDate <= "+Date.toString();
        String groupBy = " Order by createdDate DESC Limit 10000";
        if(!query.contains("createdDate"))
        {
            query +=",createdDate";
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String sobject = downSobject.name;
        String url = logInfos.instance_url + "/services/data/v52.0/jobs/query";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization","Bearer "+logInfos.access_token);
        Query queryJson = new Query();
        queryJson.query = this.query + objQuery + groupBy;
        System.out.println(queryJson.query);
        try {
            httpPost.setEntity(new StringEntity(new GsonBuilder().create().toJson(queryJson)));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpresponse = httpclient.execute(httpPost);
            String idRequest = new GsonBuilder().create().fromJson(EntityUtils.toString(httpresponse.getEntity()),JsonElement.class).getAsJsonObject().get("id").getAsString();
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n" + idRequest);
            Thread t1 = new Thread(()->ThreadMain(idRequest));
            t1.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ThreadMain(String id)
    {
        while(true)
        {
            if(getSyncJob(id))
            {
                System.out.println(count);
                List<String> csvList = List.of(getJob(id).split("\n", 2));
                if(csvList.size() < 2 )
                {
                    return;
                }
                String csv = csvList.get(1);
                long size = csv.chars().filter((c)-> ((char)c) == '\n').count();
                count -= size;
                if(count > 1)
                {
                    String getLastDate = getLastDateInCsv(csv);
                }
                break;
            }
            else
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getLastDateInCsv(String CSV)
    {
        return "";
    }

    public Boolean getSyncJob(String idJob)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = logInfos.instance_url + "/services/data/v52.0/jobs/query/"+idJob;
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization","Bearer "+logInfos.access_token);
        try {
            HttpResponse httpresponse = httpclient.execute(httpget);
            String sync =  new GsonBuilder().create().fromJson(EntityUtils.toString(httpresponse.getEntity()),JsonElement.class).getAsJsonObject().get("state").getAsString();
            System.out.println("request " + idJob + " is on : " +sync);
            return  sync.contains("JobComplete");
        }catch (Exception e)
        {
            return false;
        }
    }

    public String getJob(String idJob)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = logInfos.instance_url + "/services/data/v52.0/jobs/query/"+idJob+"/results";
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization","Bearer "+logInfos.access_token);
        try {
            HttpResponse httpresponse = httpclient.execute(httpget);
            return EntityUtils.toString(httpresponse.getEntity());
        }catch (Exception e)
        {
            return "";
        }
    }

    public void initialize()
    {
        System.out.println("initialize");
    }
}


