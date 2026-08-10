package edu.cnm.deepdive.coffeeshop.service

import edu.cnm.deepdive.coffeeshop.model.domain.Profile
import edu.cnm.deepdive.coffeeshop.repository.AuthRepository
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future

@Singleton
class AuthenticationService @Inject constructor(private val repository: AuthRepository) {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun signIn(email: String, password: String): CompletableFuture<Profile> =
        serviceScope.future { repository.signIn(email, password) }

    fun signUp(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String
    ): CompletableFuture<Profile> = serviceScope.future {
        repository.signUp(name, email, password, passwordConfirm)
    }

    fun signOut(): CompletableFuture<Void?> = serviceScope.future {
        repository.signOut()
        null
    }
}
