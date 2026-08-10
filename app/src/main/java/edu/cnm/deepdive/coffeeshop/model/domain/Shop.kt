package edu.cnm.deepdive.coffeeshop.model.domain

import java.net.URI
import java.util.UUID

data class Shop(val id: UUID, val name: String, val address: String?, val hours: Any?, val lat: Double?
, val lng: Double?, val phone: String?, val imageUrl: URI?)
