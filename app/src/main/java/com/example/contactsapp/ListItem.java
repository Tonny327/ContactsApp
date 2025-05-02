package com.example.contactsapp;

public interface ListItem {
    int TYPE_HEADER = 0;
    int TYPE_CONTACT = 1;

    int getType();
}
