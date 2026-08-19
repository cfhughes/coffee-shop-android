package edu.cnm.deepdive.coffeeshop.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentSignInBinding;
import edu.cnm.deepdive.coffeeshop.viewmodel.AuthViewModel;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

  private FragmentSignInBinding binding;
  private AuthViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentSignInBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    binding.loginButton.setOnClickListener((v) -> {
      String email = binding.emailInput.getText().toString();
      String password = binding.passwordInput.getText().toString();
      viewModel.signIn(email, password);
    });

    binding.signUpLink.setOnClickListener((v) ->
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_signInFragment_to_signUpFragment));

    viewModel.getLoading().observe(getViewLifecycleOwner(), (loading) ->
        binding.loginButton.setEnabled(!loading));

    viewModel.getErrorMessage().observe(getViewLifecycleOwner(), (message) -> {
      if (message != null) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
      }
    });


    viewModel.getSignedInProfile().observe(getViewLifecycleOwner(), (profile) -> {
      if (profile != null) {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_signInFragment_to_loggedInFragment);
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}
