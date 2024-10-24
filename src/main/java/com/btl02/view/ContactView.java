package com.btl02.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import com.btl02.model.Contact;
import com.btl02.model.ContactManager;

import java.util.function.Consumer;

public class ContactView {
    private final Contact contact;
    private final ContactManager contactManager;
    private final String dialogTitle;
    private Consumer<Contact> onContactAdded;
    private Consumer<Contact> onContactUpdated;

    public ContactView(Contact contact, ContactManager contactManager, String dialogTitle) {
        this.contact = contact;
        this.contactManager = contactManager;
        this.dialogTitle = dialogTitle;
    }

    public void setOnContactAdded(Consumer<Contact> onContactAdded) {
        this.onContactAdded = onContactAdded;
    }

    public void setOnContactUpdated(Consumer<Contact> onContactUpdated) {
        this.onContactUpdated = onContactUpdated;
    }

    public void show() {
        Stage dialog = new Stage();
        dialog.setTitle(dialogTitle);

        GridPane grid = new GridPane();
        TextField nameField = new TextField(contact != null ? contact.getFullName() : "");
        TextField phoneField = new TextField(contact != null ? contact.getPhoneNumber() : "");
        TextField additionalField = new TextField(contact != null ? contact.getAdditionalNumbers() : "");
        TextField groupField = new TextField(contact != null ? contact.getGroup() : "");

        grid.addRow(0, new Label("Họ và tên:"), nameField);
        grid.addRow(1, new Label("Số điện thoại:"), phoneField);
        grid.addRow(2, new Label("Thông tin khác:"), additionalField);
        grid.addRow(3, new Label("Nhóm:"), groupField);

        Button saveButton = new Button("Lưu");
        saveButton.setOnAction(e -> handleSave(nameField.getText(), phoneField.getText(), additionalField.getText(), groupField.getText(), dialog));
        grid.addRow(4, saveButton);

        Scene scene = new Scene(grid, 400, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    private void handleSave(String name, String phone, String additionalNumbers, String group, Stage dialog) {
        if (name.isEmpty() || phone.isEmpty()) {
            showAlert("Thông báo", "Tên và số điện thoại không được để trống.");
            return;
        }

        if (dialogTitle.equals("Thêm liên hệ")) {
            if (contactManager.checkPhoneNumberExists(phone)) {
                showAlert("Thông báo", "Số điện thoại đã tồn tại trong danh bạ.");
                return;
            }
            Contact newContact = new Contact(name, phone, additionalNumbers, group);
            onContactAdded.accept(newContact);
        } else {
            //sửa liên hệ
            Contact updatedContact = new Contact(name, phone, additionalNumbers, group);
            onContactUpdated.accept(updatedContact);
        }
        dialog.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

