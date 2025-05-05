package com.example.contactsapp;

import android.content.ContentResolver;

import java.util.List;

public class TestableContactRepository extends ContactRepository {
    private final List<Contact> mockContacts;

    public TestableContactRepository(List<Contact> contacts) {
        this.mockContacts = contacts;
    }

    @Override
    public List<Contact> getContacts(ContentResolver cr) {
        return mockContacts; // игнорируем ContentResolver
    }
}

