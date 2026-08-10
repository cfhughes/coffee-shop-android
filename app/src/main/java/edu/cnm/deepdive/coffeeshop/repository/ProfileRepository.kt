package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Profile
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.ProfileUpdateRequestDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PublicProfileDto
import edu.cnm.deepdive.coffeeshop.service.openapi.ProfileApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val profileApi: ProfileApi) {

    suspend fun getProfile(): Profile {
        return profileApi.getMyProfile().let {
            if (it.isSuccessful) {
                it.body()?.getProfile() ?: throw ServiceException("Unknown error")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun updateProfile(profile: Profile): Profile {
        val requestDto = ProfileUpdateRequestDto(
            name = profile.name
        )
        profileApi.updateMyProfile(requestDto).let {
            if (it.isSuccessful) {
                it.body()?.getProfile() ?: throw ServiceException("Unknown error")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
            return profile
        }
    }

    private fun PublicProfileDto.getProfile(): Profile {
        return Profile(
            id = this.id,
            name = this.name,
        )
    }
}