package com.example.contactsapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.*;

public class DuplicateContactCleaner {

    public interface ResultCallback {
        void onResult(boolean success, int deletedCount);
    }

    public static void deleteDuplicates(ContentResolver resolver, ResultCallback callback) {
        new Thread(() -> {
            int deletedCount = 0;

            try {
                Cursor cursor = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{
                                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                        },
                        null, null, null
                );

                if (cursor == null) {
                    callback.onResult(false, 0);
                    return;
                }

                Map<String, List<Long>> contactMap = new HashMap<>();

                while (cursor.moveToNext()) {
                    long rawContactId = cursor.getLong(0);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String photoUri = cursor.getString(3);

                    if (name == null || number == null) continue;

                    String normalizedName = name.trim().toLowerCase();
                    String normalizedNumber = number.replaceAll("\\D", "");
                    String normalizedPhoto = (photoUri != null) ? photoUri.trim() : "";

                    String key = normalizedName + "|" + normalizedNumber + "|" + normalizedPhoto;

                    contactMap.computeIfAbsent(key, k -> new ArrayList<>()).add(rawContactId);
                }

                cursor.close();

                for (List<Long> ids : contactMap.values()) {
                    if (ids.size() > 1) {
                        for (int i = 1; i < ids.size(); i++) {
                            Uri uri = ContentUris.withAppendedId(
                                    ContactsContract.RawContacts.CONTENT_URI, ids.get(i));
                            int deleted = resolver.delete(uri, null, null);
                            if (deleted > 0) deletedCount++;
                        }
                    }
                }

                callback.onResult(true, deletedCount);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(false, 0);
            }
        }).start();
    }
}
