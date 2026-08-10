package edu.cnm.deepdive.coffeeshop.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentProfileBinding;
import java.util.ArrayList;

@AndroidEntryPoint
public class ProfilePage extends Fragment {

  private FragmentProfileBinding binding;
//  private CoffeeShopAdapter adapter;
//  private ProfileViewModel viewModel;

//  private class CoffeeShopAdapter extends RecyclerView.Adapter<CoffeeShopAdapter.ShopViewHolder> {
////    private List<Shop> favoriteShops = new ArrayList<>();
//
////    public void setFavoriteShops(List<Shop> shops) {
////      this.favoriteShops = shops;
////      notifyDataSetChanged(); // Tells the RecyclerView to redraw the list
////    }
//
//    @NonNull
//    @Override
//    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//      ItemFavoriteShopBinding binding = ItemFavoriteShopBinding.inflate(
//          LayoutInflater.from(parent.getContext()), parent, false
//      );
//      return new ShopViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
//      Shop shop = favoriteShops.get(position);
//      holder.binding.shopNameTextView.setText(shop.getName());
//    }
//
//    @Override
//    public int getItemCount() {
//      return favoriteShops == null ? 0 : favoriteShops.size();
//    }
//
//    class ShopViewHolder extends RecyclerView.ViewHolder {
//      ItemFavoriteShopBinding binding;
//
//      public ShopViewHolder(@NonNull ItemFavoriteShopBinding binding) {
//        super(binding.getRoot());
//        this.binding = binding;
//      }
//    }
//  }

}
