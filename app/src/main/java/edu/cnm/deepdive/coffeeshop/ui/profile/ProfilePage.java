package edu.cnm.deepdive.coffeeshop.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.adapter.ShopFeedAdapter;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentProfilePageBinding;
import edu.cnm.deepdive.coffeeshop.model.domain.Profile;
import edu.cnm.deepdive.coffeeshop.model.domain.Shop;
import edu.cnm.deepdive.coffeeshop.model.domain.Visit;
import edu.cnm.deepdive.coffeeshop.viewmodel.ProfileViewModel;
import edu.cnm.deepdive.coffeeshop.viewmodel.VisitsViewModel;
import java.util.List;

@AndroidEntryPoint
public class ProfilePage extends Fragment {

  private FragmentProfilePageBinding binding;
  private ShopFeedAdapter favoritesAdapter;
  private ShopFeedAdapter visitedAdapter;
  private ProfileViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentProfilePageBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    binding.rvFavorites.setLayoutManager(
        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    );
    binding.rvVisited.setLayoutManager(
        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    );
    favoritesAdapter = new ShopFeedAdapter((shop, isFavorite) -> {
      viewModel.setFavorite(shop, isFavorite);
    });
    visitedAdapter = new ShopFeedAdapter((shop, isFavorite) -> {
      viewModel.setFavorite(shop, isFavorite);
    });

    binding.rvFavorites.setAdapter(favoritesAdapter);
    binding.rvVisited.setAdapter(visitedAdapter);
    viewModel.getProfile().observe(getViewLifecycleOwner(), (profile) -> {
      if (profile != null) {
        profile.getName();
        binding.textName.setText(profile.getName());
        profile.getFavorites();
        favoritesAdapter.setShops(profile.getFavorites());
      }
    });

    VisitsViewModel visitsViewModel = new ViewModelProvider(this).get(VisitsViewModel.class);
    visitsViewModel.getVisits().observe(getViewLifecycleOwner(), (visits) -> {
      if (!visits.isEmpty()) {
        List<Shop> visitedShops = visits.stream()
            .filter(v -> {
              v.getShop();
              return true;
            })
            .map(Visit::getShop)
            .toList();

        visitedAdapter.setShops(visitedShops);
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}
