
package edu.cnm.deepdive.coffeeshop.repository

import com.squareup.moshi.Moshi
import edu.cnm.deepdive.coffeeshop.model.domain.Profile
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.ErrorResponseDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PublicProfileDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.SignInRequestDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.SignUpRequestDto
import edu.cnm.deepdive.coffeeshop.service.openapi.AuthApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import retrofit2.Response

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val moshi: Moshi
) {

    suspend fun signIn(email: String, password: String): Profile {
        val response = authApi.signIn(SignInRequestDto(email, password))
        val profile = response.bodyOrThrow().toDomain()
        val token = response.headers()[AUTHORIZATION_HEADER]
            ?.removePrefix(BEARER_PREFIX)
            ?.takeIf(String::isNotBlank)
            ?: throw AuthRepositoryException(DEFAULT_ERROR_MESSAGE)
        sessionManager.saveSession(token, profile)
        return profile
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String
    ): Profile = authApi
        .signUp(SignUpRequestDto(name, email, password, passwordConfirm))
        .bodyOrThrow()
        .toDomain()

    suspend fun signOut() {
        try {
            authApi.signOut()
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            // Signing out must still discard the local bearer token when the network is unavailable.
        } finally {
            sessionManager.clearSession()
        }
    }

    private fun <T : Any> Response<T>.bodyOrThrow(): T {
        if (isSuccessful) {
            body()?.let { return it }
        }
        val message = errorBody()?.let { body ->
            runCatching {
                moshi.adapter(ErrorResponseDto::class.java).fromJson(body.string())?.message
            }.getOrNull()
        }
        throw AuthRepositoryException(message ?: DEFAULT_ERROR_MESSAGE)
    }

    private fun PublicProfileDto.toDomain() = Profile(id = id, name = name)

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val DEFAULT_ERROR_MESSAGE = "Something went wrong. Please try again."
    }
}

class AuthRepositoryException(message: String) : Exception(message)
