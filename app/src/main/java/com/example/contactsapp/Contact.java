package com.example.contactsapp;
import android.net.Uri;
public class Contact implements ListItem {
    public final long id;
    public final String name;
    public final String number;
    public final Uri photoUri;

    public Contact(long id, String name, String number, Uri photoUri) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.photoUri = photoUri;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact)) return false;
        Contact other = (Contact) obj;
        return id == other.id && name.equals(other.name)
                && number.equals(other.number)
                && ((photoUri == null && other.photoUri == null)
                || (photoUri != null && photoUri.equals(other.photoUri)));
    }

    @Override
    public int getType() {
        return TYPE_CONTACT;
    }
}
