package edu.cnm.deepdive.coffeeshop.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.coffeeshop.model.domain.Profile;
import edu.cnm.deepdive.coffeeshop.repository.SessionManager;
import edu.cnm.deepdive.coffeeshop.service.AuthenticationService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;

/**
 * Shared by {@code SignInFragment} and {@code SignUpFragment} — both screens only differ in
 * which authentication call they trigger, and expose the same loading/error/success state.
 */
@HiltViewModel
public class AuthViewModel extends ViewModel {

  private final AuthenticationService authenticationService;
  private final List<CompletableFuture<?>> pendingOperations = new CopyOnWriteArrayList<>();
  private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
  private final MutableLiveData<Profile> signedInProfile;
  private final MutableLiveData<Profile> createdProfile = new MutableLiveData<>();
  private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

  @Inject
  public AuthViewModel(AuthenticationService authenticationService, SessionManager sessionManager) {
    this.authenticationService = authenticationService;
    signedInProfile = new MutableLiveData<>(
        sessionManager.isSignedIn() ? sessionManager.getProfile() : null);
  }

  public LiveData<Boolean> getLoading() {
    return loading;
  }

  public LiveData<Profile> getSignedInProfile() {
    return signedInProfile;
  }

  public LiveData<Profile> getCreatedProfile() {
    return createdProfile;
  }

  public LiveData<String> getErrorMessage() {
    return errorMessage;
  }

  public void signIn(String email, String password) {
    loading.setValue(true);
    CompletableFuture<Profile> operation = authenticationService.signIn(email, password);
    track(operation);
    operation.whenComplete((profile, throwable) -> {
      loading.postValue(false);
      if (throwable == null) {
        signedInProfile.postValue(profile);
      } else {
        errorMessage.postValue(errorMessage(throwable));
      }
    });
  }

  public void signUp(String name, String email, String password, String passwordConfirm) {
    loading.setValue(true);
    CompletableFuture<Profile> operation =
        authenticationService.signUp(name, email, password, passwordConfirm);
    track(operation);
    operation.whenComplete((profile, throwable) -> {
      loading.postValue(false);
      if (throwable == null) {
        createdProfile.postValue(profile);
      } else {
        errorMessage.postValue(errorMessage(throwable));
      }
    });
  }

  public void signOut() {
    CompletableFuture<Void> operation = authenticationService.signOut();
    track(operation);
    operation.whenComplete((result, throwable) -> {
      if (throwable == null) {
        signedInProfile.postValue(null);
      } else {
        errorMessage.postValue(errorMessage(throwable));
      }
    });
  }

  private void track(CompletableFuture<?> operation) {
    pendingOperations.add(operation);
    operation.whenComplete((result, throwable) -> pendingOperations.remove(operation));
  }

  private static String errorMessage(Throwable throwable) {
    Throwable cause = throwable;
    while (cause instanceof CompletionException && cause.getCause() != null) {
      cause = cause.getCause();
    }
    String message = cause.getMessage();
    return (message != null && !message.isBlank())
        ? message
        : "Something went wrong. Please try again.";
  }

  @Override
  protected void onCleared() {
    pendingOperations.forEach((operation) -> operation.cancel(true));
    pendingOperations.clear();
  }

}
