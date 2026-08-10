package edu.cnm.deepdive.coffeeshop.model.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Visit(
    val id: UUID,
    val shop: Shop,
    val date: OffsetDateTime = OffsetDateTime.now(),
    val ratings: MutableList<Rating> = mutableListOf()
)
