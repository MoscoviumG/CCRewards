package com.example.ccrewards.ui.mycards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.databinding.ItemUserCardBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;

import java.util.Objects;

public class UserCardAdapter extends ListAdapter<UserCardWithDetails, UserCardAdapter.ViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(UserCardWithDetails item);
    }

    private OnCardClickListener listener;
    public UserCardAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserCardBinding binding = ItemUserCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserCardBinding binding;

        ViewHolder(ItemUserCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserCardWithDetails item) {
            if (item.definition == null) return;

            // Color strip (cardColorPrimary is stored as long ARGB, cast to int for Android API)
            binding.cardColorStrip.setBackgroundColor((int) item.definition.cardColorPrimary);

            // Card name
            binding.tvCardName.setText(item.definition.displayName);

            // Annual fee
            binding.tvAnnualFee.setText(CurrencyUtil.formatAnnualFee(item.definition.annualFee));

            // Issuer / network
            binding.tvIssuer.setText(item.definition.issuer + " · " + item.definition.network);

            // Business badge
            binding.chipBusinessBadge.setVisibility(
                    item.definition.isBusinessCard ? View.VISIBLE : View.GONE);

            // Nickname (shown only if set)
            if (item.userCard.nickname != null && !item.userCard.nickname.isEmpty()) {
                binding.tvNickname.setVisibility(View.VISIBLE);
                binding.tvNickname.setText("\u201C" + item.userCard.nickname + "\u201D");
            } else {
                binding.tvNickname.setVisibility(View.GONE);
            }

            // Open / close date
            binding.tvOpenDate.setText(
                    DateUtil.formatCardAge(item.userCard.openDate, item.userCard.closeDate));

            // Reward currency chip
            binding.chipCurrency.setText(item.definition.rewardCurrencyName);

            // Click
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(item);
            });
        }
    }

    private static final DiffUtil.ItemCallback<UserCardWithDetails> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserCardWithDetails>() {
                @Override
                public boolean areItemsTheSame(@NonNull UserCardWithDetails oldItem,
                                               @NonNull UserCardWithDetails newItem) {
                    return oldItem.userCard.id == newItem.userCard.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull UserCardWithDetails oldItem,
                                                  @NonNull UserCardWithDetails newItem) {
                    return oldItem.userCard.id == newItem.userCard.id
                            && Objects.equals(oldItem.userCard.cardDefinitionId,
                                              newItem.userCard.cardDefinitionId)
                            && Objects.equals(oldItem.userCard.nickname, newItem.userCard.nickname)
                            && Objects.equals(oldItem.userCard.openDate, newItem.userCard.openDate);
                }
            };
}
