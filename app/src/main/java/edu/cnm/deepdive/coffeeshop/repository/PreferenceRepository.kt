package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Interest
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PreferenceDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PreferenceRequestDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PreferenceUpdateRequestDto
import edu.cnm.deepdive.coffeeshop.service.openapi.PreferenceApi
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.math.BigDecimal
import kotlin.collections.emptyList

@Singleton
class PreferenceRepository @Inject constructor(private val preferenceApi: PreferenceApi) {

    suspend fun getPreferences(interestId: Long): List<PreferenceDto> =
        preferenceApi.listMyPreferences().let {
            if (it.isSuccessful) {
                it.body() ?: emptyList()
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }

    suspend fun addPreference(interest: Interest, importance: BigDecimal): PreferenceDto =
        preferenceApi.createPreference(
            PreferenceRequestDto(
                interestId = interest.id,
                importance = importance
            )
        ).let {
            if (it.isSuccessful) {
                it.body() ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }

    suspend fun removePreference(interest: Interest) =
        preferenceApi.deletePreference(interest.id).let {
            if (!it.isSuccessful) {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }

    suspend fun updatePreference(interest: Interest, importance: BigDecimal): PreferenceDto =
        preferenceApi.updatePreference(interest.id, PreferenceUpdateRequestDto(
            importance
        )).let {
            if (it.isSuccessful) {
                it.body() ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }

}