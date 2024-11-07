package com.btl02.view;

import java.util.ArrayList;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        Image logoImage = new Image(getClass().getResourceAsStream("/image/image.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100); // Đặt chiều rộng cho ảnh
        logoImageView.setPreserveRatio(true);
        Label titleLabel = new Label("Quản Lí Danh Bạ");
        titleLabel.getStyleClass().add("title");
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo tên hoặc số điện thoại...");
        searchField.getStyleClass().add("text-field");

        Button addButton = new Button("Thêm liên hệ");
        Button editButton = new Button("Sửa liên hệ");
        Button deleteButton = new Button("Xóa liên hệ");
        Button viewButton = new Button("Xem chi tiết");
        Button mergeGroupButton = new Button("Gộp nhóm");
        Button viewMergedGroupsButton = new Button("Danh Sách Nhóm");

        List<Button> buttons = List.of(addButton, editButton, deleteButton, viewButton, mergeGroupButton, viewMergedGroupsButton);
        buttons.forEach(button -> button.getStyleClass().add("button")); //gộp các nút button để css

        addButton.setOnAction(e -> showAddContactDialog());
        editButton.setOnAction(e -> showEditContactDialog());
        deleteButton.setOnAction(e -> deleteSelectedContact());
        viewButton.setOnAction(e -> showContactDetail());
        mergeGroupButton.setOnAction(e -> showMergeGroupDialog());
        viewMergedGroupsButton.setOnAction(e -> showMergedGroupsDialog());
        searchField.setOnKeyReleased(e -> searchContacts());

        VBox buttonLayout = new VBox(15, logoImageView, titleLabel, searchField, addButton, editButton, deleteButton, viewButton, mergeGroupButton, viewMergedGroupsButton);
        buttonLayout.setAlignment(Pos.CENTER); // Căn giữa các thành phần trong VBox
        buttonLayout.getStyleClass().add("button-layout");
        BorderPane root = new BorderPane();
        root.setCenter(contactListView);
        root.setRight(buttonLayout);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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

    private void showMergeGroupDialog() {
        Stage mergeDialog = new Stage();
        mergeDialog.setTitle("Gộp nhóm liên hệ");
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");
        List<CheckBox> contactCheckboxes = new ArrayList<>();
        for (Contact contact : contactManager.getAllContacts()) {
            CheckBox checkBox = new CheckBox(contact.getFullName() + " - " + contact.getPhoneNumber());
            contactCheckboxes.add(checkBox);
            layout.getChildren().add(checkBox);
        }
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Tên nhóm mới");
        layout.getChildren().add(new Label("Tên nhóm mới:"));
        layout.getChildren().add(groupNameField);
        Button mergeButton = new Button("Gộp nhóm");
        mergeButton.setOnAction(e -> {
            String groupName = groupNameField.getText();
            if (groupName.isEmpty()) {
                showAlert("Thông báo", "Tên nhóm không được để trống.");
                return;
            }
            List<Contact> selectedContacts = new ArrayList<>();
            for (int i = 0; i < contactCheckboxes.size(); i++) {
                if (contactCheckboxes.get(i).isSelected()) {
                    selectedContacts.add(contactManager.getAllContacts().get(i));
                }
            }
            if (selectedContacts.isEmpty()) {
                showAlert("Thông báo", "Vui lòng chọn ít nhất một liên hệ để gộp nhóm.");
                return;
            }
            // Gộp nhóm các liên hệ được chọn
            contactManager.mergeGroups(groupName, selectedContacts);
            // Làm mới danh sách liên hệ và nhóm đã gộp
            refreshContactList(); // Cập nhật ListView
            mergeDialog.close();

            // Hiển thị nhóm mới nhất
            showMergedGroupsDialog();
        });
        layout.getChildren().add(mergeButton);
        Scene scene = new Scene(layout, 300, 400);
        mergeDialog.setScene(scene);
        mergeDialog.show();
    }

    // Xem danh sách nhóm đã gộp
    private void showMergedGroupsDialog() {
        Stage viewDialog = new Stage();
        viewDialog.setTitle("Danh sách nhóm đã gộp");
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9;");
        List<String> mergedGroups = contactManager.getAllGroups();
        for (String group : mergedGroups) {
            // Tạo danh sách liên hệ cho mỗi nhóm
            VBox contactsBox = new VBox(5);
            contactsBox.setStyle("-fx-padding: 10;");
            List<Contact> contactsInGroup = contactManager.getContactsInGroup(group);
            for (Contact contact : contactsInGroup) {
                Label contactLabel = new Label(contact.getFullName() + " - " + contact.getPhoneNumber());
                contactLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                contactsBox.getChildren().add(contactLabel);
            }
            // Tạo TitledPane cho mỗi nhóm để có thể mở/đóng
            TitledPane titledPane = new TitledPane(group, contactsBox);
            titledPane.setExpanded(false); // Bắt đầu ở trạng thái đóng
            titledPane.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");

            layout.getChildren().add(titledPane);
        }

        Scene scene = new Scene(layout, 350, 450);
        viewDialog.setScene(scene);
        viewDialog.show();
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

