package com.example.contactsapp;

import android.content.ContentResolver;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DuplicateContactCleanerTest {

    @Test
    public void deleteDuplicates_removesOnlyDuplicates() throws InterruptedException {
        // Arrange
        ContentResolver mockResolver = mock(ContentResolver.class);

        // Создаём фейковый курсор с двумя одинаковыми контактами
        MatrixCursor cursor = new MatrixCursor(new String[]{
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        });

        cursor.addRow(new Object[]{1L, "Alice", "123-456", "photo1.jpg"});
        cursor.addRow(new Object[]{2L, "Alice", "123456", "photo1.jpg"}); // дубликат

        when(mockResolver.query(any(), any(), any(), any(), any())).thenReturn(cursor);
        when(mockResolver.delete(any(Uri.class), isNull(), isNull())).thenReturn(1);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicInteger deletedCount = new AtomicInteger(0);

        // Act
        DuplicateContactCleaner.deleteDuplicates(mockResolver, (success, count) -> {
            callbackSuccess.set(success);
            deletedCount.set(count);
            latch.countDown();
        });

        // Ожидаем завершения потока
        latch.await();

        // Assert
        assertTrue(callbackSuccess.get());
        assertEquals(1, deletedCount.get());
        verify(mockResolver, times(1)).delete(any(Uri.class), isNull(), isNull());
    }
}

