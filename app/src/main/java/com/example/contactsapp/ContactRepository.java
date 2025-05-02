package com.example.contactsapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

public class ContactRepository {

    public List<Contact> getContacts(ContentResolver cr) {
        List<Contact> result = new ArrayList<>();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY
        };

        String selection = ContactsContract.CommonDataKinds.Phone.TYPE + " = ?";
        String[] selectionArgs = {
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        };

        HashMap<Long, Contact> map = new HashMap<>();

        try (Cursor cursor = cr.query(uri, projection, selection, selectionArgs,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")) {

            if (cursor == null) return result;

            int idIdx = cursor.getColumnIndexOrThrow(projection[0]);
            int nameIdx = cursor.getColumnIndexOrThrow(projection[1]);
            int numberIdx = cursor.getColumnIndexOrThrow(projection[2]);
            int photoIdx = cursor.getColumnIndexOrThrow(projection[3]);
            int primaryIdx = cursor.getColumnIndexOrThrow(projection[5]);

            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(idIdx);
                String name = cursor.getString(nameIdx);
                String number = cursor.getString(numberIdx);
                String photoUriStr = cursor.getString(photoIdx);
                Uri photoUri = photoUriStr == null ? null : Uri.parse(photoUriStr);
                boolean isPrimary = cursor.getInt(primaryIdx) == 1;

                if (number == null || number.isEmpty()) continue;

                Contact newContact = new Contact(contactId, name, number, photoUri);

                // если ещё не добавлен контакт — просто добавляем
                if (!map.containsKey(contactId)) {
                    map.put(contactId, newContact);
                } else {
                    // если уже есть, но этот — primary, заменяем
                    if (isPrimary) {
                        map.put(contactId, newContact);
                    }
                }
            }
        }

        result.addAll(map.values());
        return result;
    }

    public List<ListItem> getGroupedContacts(ContentResolver cr) {
        List<Contact> rawContacts = getContacts(cr);
        Map<String, List<Contact>> grouped = new TreeMap<>(new SectionComparator());

        for (Contact c : rawContacts) {
            String key = c.name == null || c.name.isEmpty()
                    ? "#" : normalizeKey(c.name);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<Contact>> entry : grouped.entrySet()) {
            result.add(new HeaderItem(entry.getKey()));
            result.addAll(entry.getValue());
        }

        return result;
    }

    private String normalizeKey(String name) {
        String first = name.substring(0, 1).toUpperCase();

        // Заменим ё → Е
        if (first.equals("Ё")) return "Е";

        // Приведём латиницу к верхнему регистру
        if (first.matches("[a-zA-Zа-яА-ЯёЁ]")) {
            return first.toUpperCase();
        }

        return "#";
    }


}
