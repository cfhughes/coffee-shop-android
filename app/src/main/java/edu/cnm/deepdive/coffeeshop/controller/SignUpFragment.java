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
import edu.cnm.deepdive.coffeeshop.databinding.FragmentSignUpBinding;
import edu.cnm.deepdive.coffeeshop.viewmodel.AuthViewModel;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

  private FragmentSignUpBinding binding;
  private AuthViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentSignUpBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    binding.signUpButton.setOnClickListener((v) -> {
      String name = binding.nameInput.getText().toString();
      String email = binding.emailInput.getText().toString();
      String password = binding.passwordInput.getText().toString();
      String passwordConfirm = binding.passwordConfirmInput.getText().toString();
      viewModel.signUp(name, email, password, passwordConfirm);
    });

    binding.signInLink.setOnClickListener((v) ->
        NavHostFragment.findNavController(this).popBackStack());

    viewModel.getLoading().observe(getViewLifecycleOwner(), (loading) ->
        binding.signUpButton.setEnabled(!loading));

    viewModel.getErrorMessage().observe(getViewLifecycleOwner(), (message) -> {
      if (message != null) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
      }
    });

    viewModel.getCreatedProfile().observe(getViewLifecycleOwner(), (profile) -> {
      if (profile != null) {
        NavHostFragment.findNavController(this).popBackStack();
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}
