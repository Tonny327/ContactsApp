package com.example.contactsapp;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.UiAutomation;
import android.os.ParcelFileDescriptor;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DuplicateContactCleanerAndroidTest {

    private final Context context = ApplicationProvider.getApplicationContext();
    private final ContentResolver resolver = context.getContentResolver();
    private final List<Long> testRawContactIds = new ArrayList<>();
    private void grantPermission(String permission) {
        UiAutomation automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        String cmd = "pm grant " + context.getPackageName() + " " + permission;
        ParcelFileDescriptor pfd = automation.executeShellCommand(cmd);
        try {
            pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void insertTestContacts() {
        grantPermission("android.permission.READ_CONTACTS");
        grantPermission("android.permission.WRITE_CONTACTS");
        testRawContactIds.clear();
        // Два одинаковых контакта
        testRawContactIds.add(insertRawContact("Тестовый", "123456"));
        testRawContactIds.add(insertRawContact("Тестовый", "123456"));
        // Один уникальный
        testRawContactIds.add(insertRawContact("Уникальный", "654321"));
    }

    @After
    public void deleteTestContacts() {
        for (long id : testRawContactIds) {
            resolver.delete(
                    ContactsContract.RawContacts.CONTENT_URI,
                    ContactsContract.RawContacts._ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
        }
    }

    @Test
    public void deleteDuplicates_removesOnlyDuplicates() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final int[] deletedCount = {0};

        DuplicateContactCleaner.deleteDuplicates(resolver, (success, count) -> {
            deletedCount[0] = count;
            latch.countDown();
        });

        assertTrue("Операция не завершилась", latch.await(10, TimeUnit.SECONDS));
        assertEquals("Должен быть удалён только один дубликат", 1, deletedCount[0]);

        // Убедимся, что уникальный остался
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                new String[]{"Уникальный"},
                null
        );
        assertNotNull(cursor);
        assertTrue("Уникальный контакт должен остаться", cursor.getCount() == 1);
        cursor.close();
    }

    private long insertRawContact(String name, String number) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);

        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);

        if (rawContactUri == null) {
            throw new IllegalStateException("Не удалось вставить контакт: rawContactUri == null");
        }

        long rawContactId = android.content.ContentUris.parseId(rawContactUri);

        // Имя
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);

        // Телефон
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);

        return rawContactId;
    }

}

