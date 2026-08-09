package com.common.enums;

public enum LEFTMENU {
    PIM("PIM"),
    ADMIN("Admin"),
    LEAVE("Leave"),
    TIME("Time"),
    RECRUITMENT("Recruitment"),
    MYINFO("My Info"),
    PERFORMANCE("Performance"),
    DASHBOARD("Dashboard"),
    DIRECTORY("Directory"),
    MAINTENANCE("Maintenance"),
    BUZZ("Buzz"),
    CLAIMS("Claim");

    private final String menuName;

    LEFTMENU(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }
}
