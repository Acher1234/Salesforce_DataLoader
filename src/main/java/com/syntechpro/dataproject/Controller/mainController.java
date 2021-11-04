package com.syntechpro.dataproject.Controller;

import com.google.gson.GsonBuilder;
import com.syntechpro.dataproject.DataLoaderApplication;
import com.syntechpro.dataproject.JsonClass.SalesforceLogin;
import com.syntechpro.dataproject.Utils.StaticVariable;
import com.syntechpro.dataproject.Utils.TokenAccessSauvegarde;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.BitSet;

public class mainController {
    public static Scene scene,sceneLogin,sceneObject;
    public static Stage stage,stageLogin,stageObject;
    public static SalesforceLogin loginInfo;
    public static Boolean isConnected = false ;

    @FXML
    public TextArea text;
    @FXML
    public Button buttonLogin;
    @FXML
    public Button buttonLogout;
    @FXML
    public Label labelChoiceBox;
    @FXML
    public ChoiceBox<String> choiceBoxType;

    @FXML
    protected void MainAction(ActionEvent e) throws IOException {
        if(isConnected)
        {
            SeeAllObject(e);
        }
        else
        {
            loginAction(e);
        }

    }

    protected void SeeAllObject(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ObjectScene.fxml"));
        Parent root = loader.load();
        GetObjectAndFields.logInfos = loginInfo;
        sceneObject = new Scene(root);
        stage.setScene(sceneObject);
    }

    protected void loginAction(ActionEvent e) throws IOException {
        WebViewController.mainController = this;
        stageLogin = new Stage();
        WebViewController.stage = stageLogin;
        WebViewController.URL = choiceBoxType.getValue().toString() == "test" ? StaticVariable.URLLoginTest : StaticVariable.URLLogin;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondScene.fxml"));
        Parent root = loader.load();
        WebViewController controler = loader.getController();
        sceneLogin = new Scene(root);
        stageLogin.getIcons().add(DataLoaderApplication.icon);
        stageLogin.setTitle("Login");
        stage.hide();
        stageLogin.setScene(sceneLogin);
        stageLogin.show();
    }

    protected void returnToken(String Json)
    {
        //stage2.close();
        loginInfo = SalesforceLogin.deserialize(Json);
        TokenAccessSauvegarde.writeOnFiles(Json.replaceAll("\n",""));
        isConnected = true;
        loginned();
        stageLogin.close();
        stage.show();
    }

    protected void returnMainFromWebView()
    {
        stageLogin.close();
        stage.show();
    }

    protected void loginned()
    {
        text.setVisible(true);
        buttonLogout.setVisible(true);
        buttonLogin.setText("See And Download Object");
        text.setText(loginInfo.Json());
        labelChoiceBox.setVisible(false);
        choiceBoxType.setVisible(false);
    }

    @FXML
    public void logout()
    {
        buttonLogin.setText("Connect In");
        buttonLogout.setVisible(false);
        text.setText("");
        TokenAccessSauvegarde.logout();
        isConnected = false;
        labelChoiceBox.setVisible(true);
        choiceBoxType.setVisible(true);
    }

    @FXML
    public  void initialiseScene(Stage _stage,Scene _scene)
    {
        stage = _stage;
        stage.setResizable(false);
        scene = _scene;
    }

    @FXML
    public void initialize() {
        text.setVisible(false);
        buttonLogout.setVisible(false);
        buttonLogin.setText("Connect In");
        choiceBoxType.getItems().addAll("login","test");
        choiceBoxType.setValue("login");
        if(TokenAccessSauvegarde.isConnected())
        {
            loginInfo = TokenAccessSauvegarde.getConnection();
            isConnected = true;
            text.setText(loginInfo.Json());
            loginned();
            return;
        }
        System.out.println("Constructor");
    }
}