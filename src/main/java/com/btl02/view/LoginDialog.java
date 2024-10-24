package com.btl02.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.btl02.controler.Controler;
import com.btl02.model.UserDao;

public class LoginDialog {
    private boolean loggedIn = false;
    private UserDao userDao;

    public LoginDialog(Stage owner, Controler controller) {
        userDao = new UserDao();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Đăng nhập");

        Label userLabel = new Label("Tên đăng nhập:");
        TextField userField = new TextField();
        Label passwordLabel = new Label("Mật khẩu:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Đăng nhập");
        Button cancelButton = new Button("Hủy");

        // Xử lý sự kiện khi nhấn nút Đăng nhập
        loginButton.setOnAction(e -> {
            if (userDao.checkLogin(userField.getText(), passwordField.getText())) {
                loggedIn = true;
                dialog.close();
            } else {
                showAlert("Thông báo", "Tên đăng nhập hoặc mật khẩu không chính xác.");
                userField.clear();
                passwordField.clear();
            }
        });

        // Xử lý sự kiện khi nhấn nút Hủy
        cancelButton.setOnAction(e -> {
            dialog.close();
        });

        VBox layout = new VBox(10, userLabel, userField, passwordLabel, passwordField, loginButton, cancelButton);
        Scene scene = new Scene(layout, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

