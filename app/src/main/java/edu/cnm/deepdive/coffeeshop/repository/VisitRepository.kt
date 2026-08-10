package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Visit
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.VisitDto
import edu.cnm.deepdive.coffeeshop.service.openapi.VisitApi
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class VisitRepository @Inject constructor(private val visitApi: VisitApi) {

    suspend fun createVisit(visit: Visit): VisitDto {
        return visitApi.createVisit(visit).let {
            if (it.isSuccessful) {
                it.body() ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun getVisit(id: UUID): VisitDto {
        return visitApi.getVisitById(id = id).let {
            if (it.isSuccessful) {
                it.body() ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun getVisits(): List<VisitDto> {
        return visitApi.listMyVisits().let {
            if (it.isSuccessful) {
                it.body() ?: emptyList()
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

}
