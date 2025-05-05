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
            DuplicateContactCleaner.deleteDuplicates(getContentResolver(), (success, deletedCount) -> {
                try {
                    callback.onResult(success, deletedCount);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}

