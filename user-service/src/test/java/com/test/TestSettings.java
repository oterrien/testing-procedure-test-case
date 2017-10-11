package com.test;

import lombok.Getter;

import java.util.ResourceBundle;

public enum TestSettings {
    BUILD_DIR("buildDir"),
    BASE_DIR("baseDir");

    @Getter
    private String value;

    private static ResourceBundle bundle;

    TestSettings(String key) {
        this.value = getBundle().getString(key);
    }

    private static ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("test-settings");
        }
        return bundle;
    }
}