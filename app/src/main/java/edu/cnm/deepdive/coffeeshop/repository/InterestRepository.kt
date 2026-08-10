package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Interest
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.InterestDto
import edu.cnm.deepdive.coffeeshop.service.openapi.InterestApi
import jakarta.inject.Inject
import java.util.UUID
import jakarta.inject.Singleton

@Singleton
class InterestRepository @Inject constructor(private val interestApi: InterestApi) {

    suspend fun getInterests(): List<Interest> {
        val response = interestApi.listInterests()
        return if (response.isSuccessful) {
            response.body()?.map { it.toInterest() } ?: emptyList()
        } else {
            throw ServiceException(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    suspend fun getInterest(id: UUID): Interest{
       return interestApi.getInterestById(id).let {
           if (it.isSuccessful) {
               it.body()?.toInterest() ?: throw ServiceException("Unknown error")
           } else {
               throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
           }
       }
    }

    private fun InterestDto.toInterest(): Interest =
        Interest(
            id = this.id,
            category = this.category,
        )
}