package com.common.enums;

public enum TOPMENU {
    ABOUT("About"),
    SUPPORT("Support"),
    CHANGE_PASSWORD("Change Password"),
    LOGOUT("Logout");

    private final String menuName;

    TOPMENU(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }
}
