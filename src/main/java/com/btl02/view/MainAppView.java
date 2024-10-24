package com.btl02.view;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.btl02.model.Contact;
import com.btl02.model.ContactManager;

public final class MainAppView {
    private final Stage stage;
    private final ContactManager contactManager;
    private final ListView<Contact> contactListView;
    private final TextField searchField;
    private final ObservableList<Contact> contactList;

    public MainAppView(ContactManager contactManager) {
        this.contactManager = contactManager;
        stage = new Stage();
        stage.setTitle("Quản lý danh bạ điện thoại");

        contactList = FXCollections.observableArrayList(contactManager.getAllContacts());
        contactListView = new ListView<>(contactList);
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo tên hoặc số điện thoại...");

        Button addButton = new Button("Thêm liên hệ");
        Button editButton = new Button("Sửa liên hệ");
        Button deleteButton = new Button("Xóa liên hệ");
        Button viewButton = new Button("Xem chi tiết");

        addButton.setOnAction(e -> showAddContactDialog());
        editButton.setOnAction(e -> showEditContactDialog());
        deleteButton.setOnAction(e -> deleteSelectedContact());
        viewButton.setOnAction(e -> showContactDetail());
        searchField.setOnKeyReleased(e -> searchContacts());

        VBox buttonLayout = new VBox(10, searchField, addButton, editButton, deleteButton, viewButton);
        BorderPane root = new BorderPane();
        root.setCenter(contactListView);
        root.setRight(buttonLayout);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        refreshContactList();
    }

    public void show() {
        stage.show();
    }

    private void showAddContactDialog() {
        ContactView contactView = new ContactView(new Contact("", "", "", ""), contactManager, "Thêm liên hệ");
        contactView.setOnContactAdded(contact -> {
            contactManager.addContact(contact);
            refreshContactList();
        });
        contactView.show();
    }

    private void showEditContactDialog() {
        Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            ContactView contactView = new ContactView(selectedContact, contactManager, "Sửa liên hệ");
            contactView.setOnContactUpdated(contact -> {
                contactManager.updateContact(selectedContact, contact);
                refreshContactList();
            });
            contactView.show();
        } else {
            showAlert("Thông báo", "Vui lòng chọn một liên hệ để sửa.");
        }
    }

    private void deleteSelectedContact() {
        Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactManager.removeContact(selectedContact);
            refreshContactList();
        } else {
            showAlert("Thông báo", "Vui lòng chọn một liên hệ để xóa.");
        }
    }

    private void showContactDetail() {
        Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            ContactDetailView contactDetailView = new ContactDetailView(selectedContact);
            contactDetailView.show();
        } else {
            showAlert("Thông báo", "Vui lòng chọn một liên hệ để xem chi tiết.");
        }
    }

    private void searchContacts() {
        String query = searchField.getText().toLowerCase();

        // Gọi phương thức tìm kiếm từ ContactManager
        List<Contact> results = contactManager.searchContacts(query);

        // Cập nhật danh sách hiển thị
        contactList.setAll(results);
    }

    public void refreshContactList() {
        contactList.setAll(contactManager.getAllContacts());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

