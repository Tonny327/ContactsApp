package com.example.contactsapp;

public class HeaderItem implements ListItem {
    private final String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}

