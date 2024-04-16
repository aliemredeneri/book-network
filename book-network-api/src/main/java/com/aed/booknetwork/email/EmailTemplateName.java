package com.aed.booknetwork.email;

public enum EmailTemplateName {
    ACTIVATION("activation");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
