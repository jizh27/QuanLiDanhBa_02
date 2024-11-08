package com.btl02.controler;

import javafx.stage.Stage;
import com.btl02.model.ContactManager;
import com.btl02.view.LoginDialog;
import com.btl02.view.MainAppView;

public class Controler {
    public void start(Stage primaryStage) {
        ContactManager contactManager = new ContactManager();
        contactManager.loadFromXmlFile("src/main/resources/contacts.xml");
        LoginDialog loginDialog = new LoginDialog(primaryStage, this);

        if (loginDialog.isLoggedIn()) {
            MainAppView mainAppView = new MainAppView(contactManager);
            mainAppView.show();
        } else {
            primaryStage.close();
        }
    }
}

