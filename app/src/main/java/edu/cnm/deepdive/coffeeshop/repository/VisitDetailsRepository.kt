package edu.cnm.deepdive.coffeeshop.repository

import android.R.attr.rating
import edu.cnm.deepdive.coffeeshop.model.domain.Rating
import edu.cnm.deepdive.coffeeshop.model.domain.Visit
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.RatingDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.VisitDto
import edu.cnm.deepdive.coffeeshop.service.openapi.RatingApi
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class VisitDetailsRepository @Inject constructor(
    private val visitRepository: VisitRepository,
    private val shopRepository: ShopRepository,
    private val ratingRepository: RatingRepository,
    private val interestRepository: InterestRepository
) {

    suspend fun getVisitDetails(visitId: UUID): Visit =
        visitRepository.getVisit(visitId).toVisit()

    suspend fun getVisitsDetails(): List<Visit> {
        return visitRepository.getVisits().map { it.toVisit() }
    }

    private suspend fun VisitDto.toVisit(): Visit =
        Visit(
            id = id,
            shop = shopRepository.getShop(id),
            date = this.createdAt,
        ).also {
            it.ratings.clear()
            it.ratings.addAll(ratingRepository.getRatings(it)
                .map { it.toRating() })
        }

    private suspend fun RatingDto.toRating(): Rating =
        Rating(
            interest = interestRepository.getInterest(this.interestId),
            `value` = this.value,
        )
}