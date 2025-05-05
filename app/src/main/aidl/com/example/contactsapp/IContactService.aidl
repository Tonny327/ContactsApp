package com.example.contactsapp;

import com.example.contactsapp.IContactCallback;

interface IContactService {
    void deleteDuplicateContacts(IContactCallback callback);
}
