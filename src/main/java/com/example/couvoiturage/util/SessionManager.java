package com.example.couvoiturage.util;

import com.example.couvoiturage.model.UserEntry;

public class SessionManager {
    private static UserEntry currentUser;

    public static void setCurrentUser(UserEntry user) {
        currentUser = user;
    }

    public static UserEntry getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
