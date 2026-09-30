package com.example.smartpantry.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener listener;
    private final boolean expiryAlertsEnabled;

    public PantryAdapter(List<PantryItem> items, OnItemClickListener listener, boolean expiryAlertsEnabled) {
        this.items = items;
        this.listener = listener;
        this.expiryAlertsEnabled = expiryAlertsEnabled;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        String qty = trimTrailingZero(item.getQuantity()) + " " + safe(item.getUnit());
        holder.quantity.setText(qty);

        if (item.hasExpiry()) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + item.getExpiryDate());
            if (expiryAlertsEnabled && isExpiringSoon(item.getExpiryDate())) {
                holder.expiry.setTextColor(Color.parseColor("#EF6C00"));
            } else {
                holder.expiry.setTextColor(Color.parseColor("#6B6B6B"));
            }
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean isExpiringSoon(String isoDate) {
        try {
            LocalDate expiry = LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE);
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiry);
            return daysUntil <= 3;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String trimTrailingZero(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtItemName);
            quantity = itemView.findViewById(R.id.txtItemQuantity);
            expiry = itemView.findViewById(R.id.txtItemExpiry);
        }
    }
}