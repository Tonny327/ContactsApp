package com.example.contactsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.contactsapp.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.contactsapp.IContactService;
import com.google.android.material.snackbar.Snackbar;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import androidx.appcompat.app.AlertDialog;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final ContactRepository repository = new ContactRepository();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Запрос разрешения через новый API
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadContacts();
                } else {
                    // можно показать сообщение, что без разрешения ничего не будет
                }
            });
    private Contact lastCalledContact = null;
    private final ActivityResultLauncher<String> callPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && lastCalledContact != null) {
                    onContactClicked(lastCalledContact); // повторим вызов
                }
            });
    private IContactService contactService;
    private boolean isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            contactService = IContactService.Stub.asInterface(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactService = null;
            isBound = false;
        }
    };
    private final ActivityResultLauncher<String> writeContactsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    deleteDuplicates(); // если разрешение получено — удалим
                } else {
                    Snackbar.make(binding.getRoot(), "Нет разрешения на удаление", Snackbar.LENGTH_SHORT).show();
                }
            });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // привязка Fast Scroll к RecyclerView
        new FastScrollerBuilder(binding.recycler).build();

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(new ContactsAdapter(this::onContactClicked));
        // Привязка к ContactService
        Intent serviceIntent = new Intent(this, ContactService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);


        // RecyclerView
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(new ContactsAdapter(this::onContactClicked));

        binding.deleteDuplicatesButton.setOnClickListener(v -> deleteDuplicates());

        // Разрешение
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        executor.execute(() -> {
            List<ListItem> items = repository.getGroupedContacts(getContentResolver());
            runOnUiThread(() -> {
                ContactsAdapter adapter = (ContactsAdapter) binding.recycler.getAdapter();
                adapter.submitList(items);
            });
        });
    }


    private void onContactClicked(Contact contact) {
        if (contact.number == null || contact.number.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact.number));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            lastCalledContact = contact;
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void deleteDuplicates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            writeContactsPermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS);
            return;
        }

        if (isBound && contactService != null) {
            try {
                contactService.deleteDuplicateContacts(new IContactCallback.Stub() {
                    @Override
                    public void onResult(boolean success, int deletedCount) throws RemoteException {
                        runOnUiThread(() -> {
                            if (success) {
                                String message = deletedCount > 0
                                        ? "Удаление завершено\n(дубликатов удалено: " + deletedCount + ")"
                                        : "Дубликаты не найдены";
                                showCenteredToast(message);
                                loadContacts(); // обновляем список
                            } else {
                                showCenteredToast("Ошибка при удалении");
                            }
                        });
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
                showCenteredToast("Ошибка при вызове сервиса");
            }
        } else {
            showCenteredToast("Сервис не привязан");
        }
    }

    private void showCenteredToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_centered, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }



}
