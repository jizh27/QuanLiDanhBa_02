package com.btl02.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
        // sap xep contactlist dùng comparator
        SortedList<Contact> sorted = new SortedList<>(contactList);
        sorted.setComparator(
                (contact1, contact2) -> contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName()));
        contactListView = new ListView<>(sorted);
        Image logoImage = new Image(getClass().getResourceAsStream("/image/image.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100); // Đặt chiều rộng cho ảnh
        logoImageView.setPreserveRatio(true);
        Label titleLabel = new Label("Quản Lí Danh Bạ");
        titleLabel.getStyleClass().add("title");
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo tên hoặc số điện thoại...");
        searchField.getStyleClass().add("timkiem");

        Button addButton = new Button("Thêm liên hệ");
        Button editButton = new Button("Sửa liên hệ");
        Button deleteButton = new Button("Xóa liên hệ");
        Button viewButton = new Button("Xem chi tiết");
        Button mergeGroupButton = new Button("Gộp nhóm");
        Button viewMergedGroupsButton = new Button("Danh Sách Nhóm");

        List<Button> buttons = List.of(addButton, editButton, deleteButton, viewButton, mergeGroupButton,
                viewMergedGroupsButton);
        buttons.forEach(button -> button.getStyleClass().add("button")); // gộp các nút button để css

        addButton.setOnAction(e -> showAddContactDialog());
        editButton.setOnAction(e -> showEditContactDialog());
        deleteButton.setOnAction(e -> deleteSelectedContact());
        viewButton.setOnAction(e -> showContactDetail());
        mergeGroupButton.setOnAction(e -> showMergeGroupDialog());
        viewMergedGroupsButton.setOnAction(e -> showMergedGroupsDialog());
        searchField.setOnKeyReleased(e -> {
            String query = searchField.getText().toLowerCase();
            List<Contact> results = contactManager.searchContacts(query);
            contactList.setAll(results);
        });

        VBox buttonLayout = new VBox(20, logoImageView, titleLabel, searchField, addButton, editButton, deleteButton,
                viewButton, mergeGroupButton, viewMergedGroupsButton);
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
    Stage merge = new Stage();
    merge.setTitle("Gộp nhóm liên hệ");
    VBox layout = new VBox(15);
    layout.setStyle("-fx-padding: 15;");
    ScrollPane scroll = new ScrollPane();
    scroll.setFitToWidth(true);
    scroll.setFitToHeight(true);
    VBox checkBoxLayout = new VBox(7);
    TextField searchField = new TextField();
    searchField.setPromptText("Tìm kiếm liên hệ (Tên hoặc Số điện thoại)");
    // Lưu trữ trạng thái đã chọn của các Contact
    Map<Contact, Boolean> selectedContactsMap = new HashMap<>();
    layout.getChildren().add(new Label("Tìm kiếm liên hệ:"));
    layout.getChildren().add(searchField);
    // Lấy tất cả các liên hệ, sắp xếp và cập nhật CheckBox
    List<Contact> sortedContacts = new ArrayList<>(contactManager.getAllContacts());
    sortedContacts.sort((contact1, contact2) -> contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName()));
    updateCheckBoxes(sortedContacts, checkBoxLayout, selectedContactsMap);
    // Sự thay đổi trong trường tìm kiếm dùng comparator để sắp xếp
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        List<Contact> filteredContacts = contactManager.searchContacts(newValue);
        filteredContacts.sort((contact1, contact2) -> contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName()));
        updateCheckBoxes(filteredContacts, checkBoxLayout, selectedContactsMap);
    });
    scroll.setContent(checkBoxLayout);
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
        List<Contact> selectedContacts = selectedContactsMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (selectedContacts.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một liên hệ để gộp nhóm.");
            return;
        }
        contactManager.mergeGroups(groupName, selectedContacts);
        refreshContactList();
        merge.close();
        showMergedGroupsDialog();
    });
    layout.getChildren().add(mergeButton);
    layout.getChildren().add(scroll);
    Scene scene = new Scene(layout, 300, 400);
    merge.setScene(scene);
    merge.show();
}

private void updateCheckBoxes(List<Contact> contacts, VBox checkBoxLayout, Map<Contact, Boolean> selectedContactsMap) {
    checkBoxLayout.getChildren().clear();
    for (Contact contact : contacts) {
        CheckBox checkBox = new CheckBox(contact.getFullName() + " - " + contact.getPhoneNumber());
        checkBox.setSelected(selectedContactsMap.getOrDefault(contact, false));
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> selectedContactsMap.put(contact, newValue));
        checkBoxLayout.getChildren().add(checkBox);
    }
}
    private void showMergedGroupsDialog() {
        Stage viewDialog = new Stage();
        viewDialog.setTitle("Danh sách nhóm đã gộp");
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9;");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        List<String> mergedGroups = contactManager.getAllGroups();
        mergedGroups.sort(String::compareToIgnoreCase); // Sắp xếp nhóm theo thứ tự bảng chữ cái sử dụng comparator
        VBox groupsBox = new VBox(15);
        groupsBox.setStyle("-fx-padding: 15;");
        for (String group : mergedGroups) {
            VBox contactsBox = new VBox(7);
            contactsBox.setStyle("-fx-padding: 15;");
            // Sắp xếp theo firstname
            List<Contact> contactsInGroup = contactManager.getContactsInGroup(group);
            contactsInGroup.sort(Comparator.comparing(Contact::getFirstName));
            for (Contact contact : contactsInGroup) {
                Label contactLabel = new Label(contact.getFullName() + " - " + contact.getPhoneNumber());
                contactLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                contactsBox.getChildren().add(contactLabel);
            }
            TitledPane titledPane = new TitledPane(group, contactsBox);
            titledPane.setExpanded(false); // Bắt đầu ở trạng thái đóng
            titledPane.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");
            groupsBox.getChildren().add(titledPane);
        }
        scrollPane.setContent(groupsBox);
        layout.getChildren().add(scrollPane);
        Scene scene = new Scene(layout, 350, 450);
        viewDialog.setScene(scene);
        // Cho phép cửa sổ thay đổi kích thước khi cần thiết
        viewDialog.setResizable(true);
        viewDialog.show();
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