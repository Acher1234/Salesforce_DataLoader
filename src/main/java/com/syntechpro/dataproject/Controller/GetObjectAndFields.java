package com.syntechpro.dataproject.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.syntechpro.dataproject.JsonClass.SalesforceLogin;
import com.syntechpro.dataproject.Utils.Reference;
import com.syntechpro.dataproject.Utils.SobjectRepresentation;
import com.syntechpro.dataproject.Utils.StaticVariable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetObjectAndFields
{
    public Scene thisScene,downloadScene;
    Boolean isNumberCorrect;
    int countOfObject;
    public Map<String,String> nameToApi = new HashMap<String,String>();
    public static SalesforceLogin logInfos;
    @FXML
    public ChoiceBox choiceBox;
    @FXML
    public TextArea text;
    @FXML
    public Button button,buttonDownloading;
    @FXML
    public Label labelNumberOfObject;
    @FXML
    public ProgressBar progressBar;
    public Thread thread,threadGetCount;
    public SobjectRepresentation Sobject;


    public void GetAllItem(){
        System.out.println("launch function 2");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = logInfos.instance_url + "/services/data/v52.0/sobjects";
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization","Bearer "+logInfos.access_token);
        try {
            HttpResponse httpresponse = httpclient.execute(httpget);
            progressBar.setProgress(0.33);
            String textJson = EntityUtils.toString(httpresponse.getEntity());
            JsonElement response = new GsonBuilder().setPrettyPrinting().create().fromJson(textJson, JsonElement.class);
            String JsonFormater = new GsonBuilder().setPrettyPrinting().create().toJson(response);
            List<String> strListName = new ArrayList<String>();
            int size = response.getAsJsonObject().get("sobjects").getAsJsonArray().size(),base = 1;
            for (JsonElement elem:response.getAsJsonObject().get("sobjects").getAsJsonArray())
            {
                progressBar.setProgress(0.33 + (0.33 / (size/base++)));
                if(elem.getAsJsonObject().get("updateable").getAsBoolean() && elem.getAsJsonObject().get("triggerable").getAsBoolean())
                {
                    System.out.println(elem.getAsJsonObject().get("label").getAsString());
                    strListName.add(elem.getAsJsonObject().get("label").getAsString());
                    nameToApi.put(elem.getAsJsonObject().get("label").getAsString(),elem.getAsJsonObject().get("name").getAsString());
                }
            }
            choiceBox.getItems().addAll(strListName);
            choiceBox.setDisable(false);
            progressBar.setProgress(1);
        }catch(Exception e)
        {
            System.out.println("error:");
            System.out.println(e);
            return;
        }
    }


    @FXML
    public void setObjectProperty()
    {
        progressBar.setProgress(0);
        isNumberCorrect = false;
        labelNumberOfObject.setVisible(false);
        if(thread != null && thread.isAlive())
            thread.stop();
        if(threadGetCount != null && threadGetCount.isAlive())
            threadGetCount.stop();
        thread= new Thread(()->{this.getAllFields();});
        thread.start();
        threadGetCount = new Thread(()->{this.getCountSobject();});
        threadGetCount.start();

    }

    public void getCountSobject()
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String sobject = choiceBox.getValue().toString();
        String url = logInfos.instance_url + "/services/data/v52.0/limits/recordCount?sObjects="+ nameToApi.get(sobject);
        System.out.println(url);
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization","Bearer "+logInfos.access_token);
        try {
            HttpResponse httpresponse = httpclient.execute(httpget);
            Gson jsonResponse = new GsonBuilder().create();
            JsonElement json = jsonResponse.fromJson(EntityUtils.toString(httpresponse.getEntity()),JsonElement.class);
            int number = json.getAsJsonObject().get("sObjects").getAsJsonArray().get(0).getAsJsonObject().get("count").getAsInt();
            labelNumberOfObject.setVisible(true);
            Platform.runLater(()->{labelNumberOfObject.setText("Number of elements: " + number);});
            this.countOfObject = number;
            isNumberCorrect = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAllFields()
    {
        String sobject = choiceBox.getValue().toString();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = logInfos.instance_url + "/services/data/v52.0/sobjects/"+ nameToApi.get(sobject)+"/describe";
        System.out.println(url);
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization","Bearer "+logInfos.access_token);
        try
        {
            HttpResponse httpresponse = httpclient.execute(httpget);
            progressBar.setProgress(0.33);
            Sobject = new SobjectRepresentation(sobject);
            Gson jsonResponse = new GsonBuilder().create();
            JsonElement json = jsonResponse.fromJson(EntityUtils.toString(httpresponse.getEntity()),JsonElement.class);
            int numberOfElement = json.getAsJsonObject().get("fields").getAsJsonArray().size();
            int i = 1;
            for (JsonElement tempJson:json.getAsJsonObject().get("fields").getAsJsonArray())
            {
                if(tempJson.getAsJsonObject().get("updateable").getAsBoolean())
                {
                    if(StaticVariable.salesforceType.get(tempJson.getAsJsonObject().get("type").getAsString()) == null)
                    {
                        Sobject.addField(tempJson.getAsJsonObject().get("label").getAsString(),tempJson.getAsJsonObject().get("name").getAsString(),String.class);
                    }
                    else if(StaticVariable.salesforceType.get(tempJson.getAsJsonObject().get("type").getAsString()) != null)
                    {
                        if(StaticVariable.salesforceType.get(tempJson.getAsJsonObject().get("type").getAsString()) == Reference.class)
                        {
                            Sobject.addField(tempJson.getAsJsonObject().get("label").getAsString(),tempJson.getAsJsonObject().get("name").getAsString(), StaticVariable.salesforceType.get(tempJson.getAsJsonObject().get("type").getAsString()), new Reference(tempJson));
                        }
                        else
                        {
                            Sobject.addField(tempJson.getAsJsonObject().get("label").getAsString(),tempJson.getAsJsonObject().get("name").getAsString(), StaticVariable.salesforceType.get(tempJson.getAsJsonObject().get("type").getAsString()));
                        }
                    }
                    else
                    {
                        Sobject.addField(tempJson.getAsJsonObject().get("label").getAsString(),tempJson.getAsJsonObject().get("name").getAsString(),String.class);
                    }
                }
                progressBar.setProgress(0.33 + 0.66/(numberOfElement/i++));
            }
            buttonDownloading.setDisable(false);
            progressBar.setProgress(1);
            text.setText(Sobject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit()
    {

    }

    public void sceneDownloding() throws IOException {
        thisScene = buttonDownloading.getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DownloadFile.fxml"));
        downloadScene = new Scene(loader.load());
        DownloadObjectScene controler = (DownloadObjectScene)loader.getController();
        controler.downSobject = this.Sobject;
        controler.logInfos = logInfos;
        while(!this.isNumberCorrect);
        controler.count = this.countOfObject;
        ((Stage)thisScene.getWindow()).setScene(downloadScene);
        controler.Start();
    }



    public void initialize()
    {
        System.out.println(choiceBox);
        buttonDownloading.setDisable(true);
        choiceBox.setDisable(true);
        labelNumberOfObject.setVisible(false);
        choiceBox.setOnAction((e)->{this.setObjectProperty();});
        Thread t1 = new Thread(()->{this.GetAllItem();});
        t1.start();
    }

}
