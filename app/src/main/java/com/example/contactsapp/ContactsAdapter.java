package com.example.contactsapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsapp.databinding.ItemContactBinding;

import java.util.Objects;


public class ContactsAdapter extends ListAdapter<ListItem, RecyclerView.ViewHolder> {

    public interface ClickListener {
        void onContactClick(Contact contact);
    }

    private final ClickListener clickListener;

    public ContactsAdapter(ClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    // ViewHolder для заголовков
    static class HeaderVH extends RecyclerView.ViewHolder {
        android.widget.TextView title;

        HeaderVH(android.view.View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.header_title);
        }
    }

    // ViewHolder для контактов
    static class ContactVH extends RecyclerView.ViewHolder {
        final ItemContactBinding b;

        ContactVH(ItemContactBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());

        if (viewType == ListItem.TYPE_HEADER) {
            android.view.View v = inf.inflate(R.layout.item_header, parent, false);
            return new HeaderVH(v);
        } else {
            ItemContactBinding binding = ItemContactBinding.inflate(inf, parent, false);
            return new ContactVH(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ListItem item = getItem(position);

        if (vh instanceof HeaderVH) {
            HeaderItem header = (HeaderItem) item;
            ((HeaderVH) vh).title.setText(header.getTitle());
        } else {
            Contact contact = (Contact) item;
            ContactVH holder = (ContactVH) vh;

            holder.b.name.setText(contact.name);
            holder.b.number.setText(contact.number != null ? contact.number : "Нет номера");

            if (contact.photoUri != null) {
                holder.b.photo.setImageURI(contact.photoUri);
            } else {
                holder.b.photo.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            holder.itemView.setOnClickListener(v -> clickListener.onContactClick(contact));
        }
    }

    // DiffUtil
    static final DiffUtil.ItemCallback<ListItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            if (oldItem.getType() != newItem.getType()) return false;
            if (oldItem instanceof HeaderItem && newItem instanceof HeaderItem)
                return Objects.equals(((HeaderItem) oldItem).getTitle(), ((HeaderItem) newItem).getTitle());
            if (oldItem instanceof Contact && newItem instanceof Contact)
                return ((Contact) oldItem).id == ((Contact) newItem).id;
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            return oldItem.equals(newItem);
        }
    };

}





