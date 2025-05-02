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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.contactsapp.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // RecyclerView
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(new ContactsAdapter(this::onContactClicked));

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
        if (contact.number == null) return;

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.number));
        startActivity(intent);
    }
}
