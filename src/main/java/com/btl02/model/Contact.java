package com.btl02.model;

import java.time.LocalDateTime;

public class Contact {
    private final String fullName; 
    private final String phoneNumber;
    private final String additionalNumbers;  // Các số điện thoại khác
    private final String group;
    private final LocalDateTime addedTime;
    //Constructor với 4 tham số
    public Contact(String fullName, String phoneNumber, String additionalNumbers, String group) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.additionalNumbers = additionalNumbers;
        this.group = group;
        this.addedTime = LocalDateTime.now();
    }
    // Constructor với 5 tham số (để cập nhật hoặc tạo liên hệ với thời gian cụ thể)
    public Contact(String fullName, String phoneNumber, String additionalNumbers, String group, LocalDateTime addedTime) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.additionalNumbers = additionalNumbers;
        this.group = group;
        this.addedTime = addedTime;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAdditionalNumbers() {
        return additionalNumbers;
    }

    public String getGroup() {
        return group;
    }

    public LocalDateTime getAddedTime() {
        return addedTime;
    }

    @Override
    public String toString() {
        return fullName + " (" + phoneNumber + ")" + 
               (additionalNumbers != null && !additionalNumbers.isEmpty() 
                ? ", thêm: " + additionalNumbers 
                : "") + 
               (group != null && !group.isEmpty() ? " [Nhóm: " + group + "]" : "");
    }
}

