package com.ui.driverfactory;

public enum EnvironmentType {
    LOCAL("Local"),
    GRID("Grid"),
    SAUCE_LABS("Sauce_Labs"),
    BROWSER_STACK("Browser_Stack");

    private final String environmentName;

    EnvironmentType(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }
}
