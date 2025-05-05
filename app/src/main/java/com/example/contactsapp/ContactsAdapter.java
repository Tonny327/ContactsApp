package com.example.contactsapp;

import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contactsapp.databinding.ItemContactBinding;

import java.util.Objects;


public class ContactsAdapter extends ListAdapter<ListItem, RecyclerView.ViewHolder>
        implements me.zhanghai.android.fastscroll.PopupTextProvider {

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

            int avatarSize = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    48, // размер в dp
                    holder.b.photo.getResources().getDisplayMetrics()
            );


            holder.b.name.setText(contact.name);
            holder.b.number.setText(contact.number != null ? contact.number : "Нет номера");

            if (contact.photoUri != null) {
                Glide.with(holder.b.photo.getContext())
                        .load(contact.photoUri)
                        .placeholder(TextDrawableUtil.createAvatar(holder.b.photo.getContext(), contact.name, avatarSize))
                        .error(TextDrawableUtil.createAvatar(holder.b.photo.getContext(), contact.name, avatarSize))
                        .circleCrop()
                        .into(holder.b.photo);
            } else {
                holder.b.photo.setImageDrawable(
                        TextDrawableUtil.createAvatar(holder.b.photo.getContext(), contact.name, avatarSize)
                );
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
    @Override
    public String getPopupText(@NonNull View view, int position) {
        ListItem item = getItem(position);
        if (item instanceof HeaderItem) {
            return ((HeaderItem) item).getTitle();
        } else if (item instanceof Contact) {
            String name = ((Contact) item).name;
            return name != null && !name.isEmpty()
                    ? name.substring(0, 1).toUpperCase()
                    : "#";
        }
        return "#";
    }



}





