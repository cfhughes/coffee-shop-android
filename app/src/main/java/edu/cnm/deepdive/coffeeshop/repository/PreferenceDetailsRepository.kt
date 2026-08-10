package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Interest
import edu.cnm.deepdive.coffeeshop.model.domain.Preference
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.PreferenceDto
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.math.BigDecimal

@Singleton
class PreferenceDetailsRepository @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val interestRepository: InterestRepository
) {

    suspend fun getPreferenceDetails(interestId: Long): List<Preference> {
        return preferenceRepository.getPreferences(interestId).map { it.toPreference() }
    }

    suspend fun addPreferenceDetails(interest: Interest, importance: BigDecimal): Preference {
        return preferenceRepository.addPreference(interest, importance).toPreference()
    }

    suspend fun removePreferenceDetails(interest: Interest) {
        preferenceRepository.removePreference(interest)
    }

    suspend fun updatePreferenceDetails(interest: Interest, importance: BigDecimal): Preference {
        return preferenceRepository.updatePreference(interest, importance).toPreference()
    }

    suspend fun PreferenceDto.toPreference(): Preference {
        return Preference(
            interest = interestRepository.getInterest(this.interestId),
            importance = this.importance
        )
    }
}