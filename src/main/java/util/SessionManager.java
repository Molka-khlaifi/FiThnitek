package util;

import models.UserEntry;

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

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    private static Integer selectedTripId;
    public static void setSelectedTripId(Integer id) {
        selectedTripId = id;
    }
    public static Integer getSelectedTripId() {
        return selectedTripId;
    }
}