package com.btl02.model;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContactManager {
    private final List<Contact> contacts;

    public ContactManager() {
        contacts = new ArrayList<>();
    }

    public boolean addContact(Contact contact) {
        if (checkPhoneNumberExists(contact.getPhoneNumber())) {
            return false; // Số điện thoại đã tồn tại
        }
        contacts.add(contact);
        return true; // Thêm thành công
    }
    

    public void removeContact(Contact contact) {
        contacts.remove(contact);
    }

    public void updateContact(Contact oldContact, Contact newContact) {
        int index = contacts.indexOf(oldContact);
        if (index >= 0) {
            contacts.set(index, newContact);
        }
    }

    public List<Contact> searchContacts(String query) {
        if (query == null || query.isEmpty()) {
            return getAllContacts(); // Nếu không có từ khóa, trả về toàn bộ danh bạ
        }
        String lowerCaseQuery = query.toLowerCase();
        return contacts.stream()
                .filter(contact -> contact.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                                   contact.getPhoneNumber().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts); // Trả về một bản sao của danh sách
    }
    
    public boolean checkPhoneNumberExists(String phoneNumber) {
        return contacts.stream()
                .anyMatch(contact -> contact.getPhoneNumber().equals(phoneNumber));
    }
    
}

