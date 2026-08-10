package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.domain.Interest
import edu.cnm.deepdive.coffeeshop.model.domain.Visit
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.RatingDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.RatingRequestDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.RatingUpdateRequestDto
import edu.cnm.deepdive.coffeeshop.service.openapi.RatingApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RatingRepository @Inject constructor(private val ratingApi: RatingApi) {

    suspend fun addRating(visit: Visit, ratingRequestDto: RatingRequestDto): RatingDto {
        return ratingApi.createRating(visit.id, ratingRequestDto).let {
            if(it.isSuccessful) {
                it.body() ?: throw ServiceException("Unexpected response")
            } else{
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun removeRating(visit: Visit, interest: Interest) {
        ratingApi.deleteRating(visitId = visit.id, interestId = interest.id).let {
            if (!it.isSuccessful) {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun getRatings(visit: Visit): List<RatingDto> {
        ratingApi.listRatings(
            visitId = visit.id
        ).let {
            if (it.isSuccessful) {
                return it.body() ?: emptyList()
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    // FIXME: Debug invalid generation of ratingApi
//    suspend fun updateRating(visit: Visit, ratingRequestDto: RatingRequestDto) {
//        ratingApi.updateRating(
//            visit.id, ratingRequestDto,
//            ratingUpdateRequestDto = RatingUpdateRequestDto(
//                value = ratingRequestDto.value
//            )
//        ).let {
//            if (!it.isSuccessful) {
//                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
//            }
//        }
//    }

}