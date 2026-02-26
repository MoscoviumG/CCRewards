package com.example.ccrewards.ui.mycards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.databinding.ItemCardCatalogBinding;
import com.example.ccrewards.databinding.ItemCreateCustomCardBinding;
import com.example.ccrewards.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CREATE_CUSTOM = 0;
    private static final int TYPE_CARD = 1;

    public interface OnItemClickListener {
        void onCardClick(CardDefinition card);
        void onCreateCustomClick();
    }

    private List<CardDefinition> cards = new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CardDefinition> newList) {
        cards = newList == null ? new ArrayList<>() : new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_CREATE_CUSTOM : TYPE_CARD;
    }

    @Override
    public int getItemCount() {
        return cards.size() + 1; // +1 for the "Create Custom Card" header
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CREATE_CUSTOM) {
            ItemCreateCustomCardBinding binding = ItemCreateCustomCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new CreateCustomViewHolder(binding);
        }
        ItemCardCatalogBinding binding = ItemCardCatalogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CreateCustomViewHolder) {
            ((CreateCustomViewHolder) holder).bind();
        } else if (holder instanceof CardViewHolder) {
            ((CardViewHolder) holder).bind(cards.get(position - 1));
        }
    }

    class CreateCustomViewHolder extends RecyclerView.ViewHolder {
        private final ItemCreateCustomCardBinding binding;

        CreateCustomViewHolder(ItemCreateCustomCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind() {
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCreateCustomClick();
            });
        }
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private final ItemCardCatalogBinding binding;

        CardViewHolder(ItemCardCatalogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CardDefinition card) {
            binding.catalogColorStrip.setBackgroundColor((int) card.cardColorPrimary);
            binding.tvCatalogName.setText(card.displayName);
            binding.tvCatalogFee.setText(CurrencyUtil.formatAnnualFee(card.annualFee));
            binding.tvCatalogIssuer.setText(card.issuer + " · " + card.network);
            binding.chipCatalogBusiness.setVisibility(
                    card.isBusinessCard ? View.VISIBLE : View.GONE);
            binding.chipCatalogCurrency.setText(card.rewardCurrencyName);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(card);
            });
        }
    }
}
