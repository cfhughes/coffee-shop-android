package edu.cnm.deepdive.coffeeshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.databinding.ItemShopCardBinding;
import edu.cnm.deepdive.coffeeshop.model.domain.Shop;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


  private List<Shop> shops = new ArrayList<>();
  private final OnFavoriteClickListener favoriteClickListener;
  private final OnRatingChangedListener ratingChangedListener;
  private final Map<UUID, Integer> ratings = new HashMap<>();

  public ShopFeedAdapter(OnFavoriteClickListener listener) {
    this(listener, null);
  }

  public ShopFeedAdapter(OnFavoriteClickListener favoriteClickListener,
      OnRatingChangedListener ratingChangedListener) {
    this.favoriteClickListener = favoriteClickListener;
    this.ratingChangedListener = ratingChangedListener;
  }

  public void setShops(List<Shop> shops) {
    this.shops = shops;
    notifyDataSetChanged();
  }

  public void setRatings(Map<UUID, Integer> ratings) {
    this.ratings.clear();
    this.ratings.putAll(ratings);
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemShopCardBinding binding = ItemShopCardBinding.inflate(
        LayoutInflater.from(parent.getContext()), parent, false
    );
    return new ShopViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    ((ShopViewHolder) holder).bind(shops.get(position));
  }

  @Override
  public int getItemCount() {
    return shops != null ? shops.size() : 0;
  }

  class ShopViewHolder extends RecyclerView.ViewHolder {

    private final ItemShopCardBinding binding;

    public ShopViewHolder(@NonNull ItemShopCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(Shop shop) {
      binding.tvShopTitle.setText(shop.getName());
      binding.tvShopDescription.setText(shop.getAddress() == null ? "" : shop.getAddress());
      if (shop.getImageUrl() != null && !shop.getImageUrl().toString().isBlank()) {
        Glide.with(binding.ivShopImage)
            .load(shop.getImageUrl().toString())
            .placeholder(R.drawable.coffee_shop)
            .error(R.drawable.coffee_shop)
            .fallback(R.drawable.coffee_shop)
            .into(binding.ivShopImage);
      } else {
        binding.ivShopImage.setImageResource(R.drawable.coffee_shop);
      }
      binding.btnFavorite.setImageResource(shop.isFavorite() ? android.R.drawable.btn_star_big_on
          : android.R.drawable.btn_star_big_off);
      // Favorite button toggle
      binding.btnFavorite.setOnClickListener(_ -> {
        shop.setFavorite(!shop.isFavorite());
        binding.btnFavorite.setImageResource(
            shop.isFavorite() ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off
        );
        if (favoriteClickListener != null) {
          favoriteClickListener.onFavoriteClick(shop, shop.isFavorite());
        }
      });
      binding.ratingBarPreference.setOnRatingBarChangeListener(null);
      binding.ratingBarPreference.setIsIndicator(ratingChangedListener == null);
      binding.ratingBarPreference.setRating(ratings.getOrDefault(shop.getId(), 0));
      binding.ratingBarPreference.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
        if (fromUser && ratingChangedListener != null) {
          ratingChangedListener.onRatingChanged(shop, Math.round(rating));
        }
      });
      List<String> preferences = shop.getPreferences();
      if (!preferences.isEmpty()) {
        String formattedPreferences = String.join(" • ", preferences);
        binding.textPreferences.setText(formattedPreferences);
        binding.textPreferences.setVisibility(View.VISIBLE);
      } else {
        binding.textPreferences.setVisibility(View.GONE);
      }
      if (!preferences.isEmpty()) {
        String formattedPreferences = String.join(" • ", preferences);
        binding.textPreferences.setText(formattedPreferences);
        binding.textPreferences.setVisibility(View.VISIBLE);
      } else {
        binding.textPreferences.setVisibility(View.GONE);
      }
    }

  }


  public interface OnFavoriteClickListener {

    void onFavoriteClick(Shop shop, boolean isFavorite);
  }

  public interface OnRatingChangedListener {

    void onRatingChanged(Shop shop, int rating);
  }
}
