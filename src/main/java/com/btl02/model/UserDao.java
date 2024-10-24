package com.btl02.model;

import java.util.HashMap;
import java.util.Map;

public class UserDao {
    private final Map<String, String> users;

    public UserDao() {
        users = new HashMap<>();
        users.put("admin", "password");
        users.put("user", "1234");
    }

    public boolean checkLogin(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}

