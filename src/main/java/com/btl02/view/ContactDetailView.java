package com.btl02.view;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.btl02.model.Contact;

public class ContactDetailView {
    private final Contact contact;

    public ContactDetailView(Contact contact) {
        this.contact = contact;
    }

    public void show() {
        Stage detailStage = new Stage();
        detailStage.setTitle("Chi tiết liên hệ");

        Label nameLabel = new Label("Họ và tên: " + contact.getFullName());
        Label phoneLabel = new Label("Số điện thoại: " + contact.getPhoneNumber());
        Label additionalLabel = new Label("Thông tin khác: " + contact.getAdditionalNumbers());
        Label groupLabel = new Label("Nhóm: " + contact.getGroup());
        Label addedTimeLabel = new Label("Thời gian thêm: " + contact.getAddedTime());

        Button closeButton = new Button("Đóng");
        closeButton.setOnAction(e -> detailStage.close());

        VBox layout = new VBox(10, nameLabel, phoneLabel, additionalLabel, groupLabel, addedTimeLabel, closeButton);
        Scene scene = new Scene(layout, 400, 250);
        detailStage.setScene(scene);
        detailStage.show();
    }
}

