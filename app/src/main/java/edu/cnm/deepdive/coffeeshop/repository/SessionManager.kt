package edu.cnm.deepdive.coffeeshop.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cnm.deepdive.coffeeshop.model.domain.Profile
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(token: String, profile: Profile) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_PROFILE_ID, profile.id.toString())
            .putString(KEY_PROFILE_NAME, profile.name)
            .apply()
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    fun getToken(): String? = preferences.getString(KEY_TOKEN, null)

    fun getProfile(): Profile? {
        val id = preferences.getString(KEY_PROFILE_ID, null) ?: return null
        val name = preferences.getString(KEY_PROFILE_NAME, null) ?: return null
        return try {
            Profile(id = UUID.fromString(id), name = name)
        } catch (_: IllegalArgumentException) {
            clearSession()
            null
        }
    }

    fun getProfileId(): UUID? = getProfile()?.id

    fun getProfileName(): String? = getProfile()?.name

    fun isSignedIn(): Boolean = !getToken().isNullOrBlank() && getProfile() != null

    private companion object {
        const val PREFS_NAME = "session"
        const val KEY_TOKEN = "token"
        const val KEY_PROFILE_ID = "profile_id"
        const val KEY_PROFILE_NAME = "profile_name"
    }
}
