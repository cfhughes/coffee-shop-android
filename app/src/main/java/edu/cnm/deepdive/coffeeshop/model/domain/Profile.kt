package edu.cnm.deepdive.coffeeshop.model.domain

import java.util.UUID


data class Profile(
    val id: UUID,
    var name: String,
    val favorites: MutableList<Shop> = mutableListOf(),
    val preferences: MutableList<Preference> = mutableListOf(),
    val visits: MutableList<Visit> = mutableListOf()
)
