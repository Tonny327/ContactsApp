package com.example.contactsapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    public static final int REQUEST_READ_CONTACTS = 100;

    public static boolean hasReadPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                REQUEST_READ_CONTACTS);
    }
}
