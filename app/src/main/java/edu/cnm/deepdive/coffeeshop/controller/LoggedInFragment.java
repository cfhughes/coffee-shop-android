package edu.cnm.deepdive.coffeeshop.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentLoggedInBinding;
import edu.cnm.deepdive.coffeeshop.repository.SessionManager;
import edu.cnm.deepdive.coffeeshop.viewmodel.AuthViewModel;
import javax.inject.Inject;

@AndroidEntryPoint
public class LoggedInFragment extends Fragment {

  @Inject
  SessionManager sessionManager;

  private FragmentLoggedInBinding binding;
  private AuthViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentLoggedInBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    binding.welcomeText.setText(getString(R.string.welcome_message, sessionManager.getProfileName()));

    binding.signOutButton.setOnClickListener((v) -> viewModel.signOut());

    viewModel.getSignedInProfile().observe(getViewLifecycleOwner(), (profile) -> {
      if (profile == null) {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_loggedInFragment_to_signInFragment);
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}
