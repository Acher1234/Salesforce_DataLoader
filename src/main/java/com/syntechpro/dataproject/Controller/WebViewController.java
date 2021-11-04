package com.syntechpro.dataproject.Controller;

import com.syntechpro.dataproject.Utils.StaticVariable;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class WebViewController {
    public static Stage stage;
    public static Scene scene;
    public static mainController mainController;
    public  WebEngine webEngine;
    public static String URL; // have to be static because i get the controler after the initialize function...
    public String clientID = StaticVariable.clientID;
    public String clientSecret = StaticVariable.clientSecret;
    public String redirectURI = "http://localhost:8888/";
    public String Json;

    @FXML
    public WebView webView;

    @FXML
    public TextField text;

    @FXML
    public void initialize() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mainController.returnMainFromWebView();
            }
        });
        System.out.println("Constructor for webView");
        scene = webView.getScene();
        text.addEventHandler(KeyEvent.KEY_PRESSED,e -> {keyPressed(e);});
        webEngine = webView.getEngine();
        webEngine.load(text.getText());
        webEngine.getLoadWorker().stateProperty().addListener(((observableValue, state, t1) ->
        {
            if(Worker.State.SUCCEEDED.equals(t1))
            {
                text.setText(webEngine.getLocation());
            }
            if(Worker.State.FAILED.equals(t1))
            {
                if(webEngine.getLoadWorker().getException() != null)
                    System.out.println(webEngine.getLoadWorker().getException().toString());
            }
            if(Worker.State.CANCELLED.equals(t1))
            {
                if(webEngine.getLoadWorker().getMessage() != null)
                    System.out.println(webEngine.getLoadWorker().getMessage().toString());
            }
            if(webEngine.getLocation().contains("localhost") && !webEngine.getLocation().contains("login.salesforce") && !webEngine.getLocation().contains("test.salesforce") && Worker.State.SCHEDULED.equals(t1))
            {
                System.out.println(t1);
                String code = webEngine.getLocation().toString().split("code=")[1];
                System.out.println(code);
                try {
                    Json = requestToken(code);
                    mainController.returnToken(Json);
                } catch (UnsupportedEncodingException e) {
                    System.out.println("beug on paramater");
                }

            }
        }));
        initialiseAddress();
    }

    public String requestToken(String code) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(URL+"services/oauth2/token");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code.replace("%3D","=")));
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("redirect_uri", redirectURI));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end");
        return null;
    }

    public void initialiseAddress()
    {
        String URLLoaded = URL+"services/oauth2/authorize?client_id=" + clientID +"&redirect_uri=" + redirectURI + "&response_type=code";
        System.out.println(URLLoaded);
        webView.getEngine().load(URLLoaded);
    }


    public  void  keyPressed(KeyEvent e)
    {
        if(e.getCode() == KeyCode.ENTER)
        {
            webView.getEngine().load(text.getText());
        }
    }


}
