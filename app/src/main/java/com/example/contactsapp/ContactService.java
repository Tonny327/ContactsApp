package com.example.contactsapp;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactService extends Service {

    private final IContactService.Stub binder = new IContactService.Stub() {
        @Override
        public void deleteDuplicateContacts(IContactCallback callback) throws RemoteException {
            new Thread(() -> {
                int deletedCount = 0;

                try {
                    ContentResolver resolver = getContentResolver();
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
                        long rawContactId = cursor.getLong(cursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

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
                    try {
                        callback.onResult(false, 0);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}

