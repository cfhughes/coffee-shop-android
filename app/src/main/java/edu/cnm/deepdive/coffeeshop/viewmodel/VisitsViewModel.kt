package edu.cnm.deepdive.coffeeshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.model.domain.Visit
import edu.cnm.deepdive.coffeeshop.service.VisitService
import jakarta.inject.Inject
import java.util.UUID

@HiltViewModel
class VisitsViewModel @Inject constructor(private val visitService: VisitService) : ViewModel() {

    private val _visits = MutableLiveData<List<Visit>>()
    val visits: LiveData<List<Visit>> = _visits

    init {
        fetchVisits()
    }

    fun fetchVisits() {
        // Mock visits locally inside the ViewModel
        _visits.value = buildTestVisits()
    }

    private fun buildTestVisits(): List<Visit> {
        val shop1 = Shop(
            id = UUID.randomUUID(),
            name = "Little Bear Coffee",
            address = "2632 Pennsylvania St NE",
            hours = "7:00 AM - 5:00 PM",
            lat = 35.0880,
            lng = -106.6510,
            phone = "505-555-0100",
            imageUrl = null,
            isFavorite = true
        )

        val shop2 = Shop(
            id = UUID.randomUUID(),
            name = "Espresso Express",
            address = "123 Main St",
            hours = "6:30 AM - 4:00 PM",
            lat = 35.0850,
            lng = -106.6500,
            phone = "505-555-0102",
            imageUrl = null,
            isFavorite = false
        )

        val shop3 = Shop(
            id = UUID.randomUUID(),
            name = "Bike In Coffee",
            address = "949 Montoya St NW, Albuquerque, NM 87104",
            hours = "7:00 AM - 2:00 PM",
            lat = 35.0991,
            lng = -106.6712,
            phone = "505-555-0103",
            imageUrl = null,
            isFavorite = true
        )

        val shop4 = Shop(
            id = UUID.randomUUID(),
            name = "The Well Coffee",
            address = "5500 San Mateo Blvd NE, Ste 104, Albuquerque, NM 87109",
            hours = "7:00 AM - 5:00 PM",
            lat = 35.1432,
            lng = -106.5861,
            phone = "505-555-0104",
            imageUrl = null,
            isFavorite = false
        )

        val shop5 = Shop(
            id = UUID.randomUUID(),
            name = "Café Lush",
            address = "700 Tijeras Ave NW, Albuquerque, NM 87102",
            hours = "7:00 AM - 2:00 PM",
            lat = 35.0874,
            lng = -106.6558,
            phone = "505-555-0105",
            imageUrl = null,
            isFavorite = true
        )

        val shop6 = Shop(
            id = UUID.randomUUID(),
            name = "Drop Cafe",
            address = "5011 Coors Blvd NW, Ste A, Albuquerque, NM 87120",
            hours = "6:00 AM - 4:00 PM",
            lat = 35.1389,
            lng = -106.7025,
            phone = "505-555-0106",
            imageUrl = null,
            isFavorite = false
        )

        val shop7 = Shop(
            id = UUID.randomUUID(),
            name = "Citizen Coffee",
            address = "7518 Oak St NE, Albuquerque, NM 87110",
            hours = "7:00 AM - 3:00 PM",
            lat = 35.1012,
            lng = -106.6210,
            phone = "505-555-0107",
            imageUrl = null,
            isFavorite = true
        )

        val shop8 = Shop(
            id = UUID.randomUUID(),
            name = "Golden Crown Panaderia",
            address = "1103 Mountain Rd NW, Albuquerque, NM 87102",
            hours = "7:00 AM - 8:00 PM",
            lat = 35.0935,
            lng = -106.6603,
            phone = "505-555-0108",
            imageUrl = null,
            isFavorite = true
        )

        return listOf(
            Visit(id = UUID.randomUUID(), shop = shop1),
            Visit(id = UUID.randomUUID(), shop = shop2),
            Visit(id = UUID.randomUUID(), shop = shop3),
            Visit(id = UUID.randomUUID(), shop = shop4),
            Visit(id = UUID.randomUUID(), shop = shop5),
            Visit(id = UUID.randomUUID(), shop = shop6),
            Visit(id = UUID.randomUUID(), shop = shop7),
            Visit(id = UUID.randomUUID(), shop = shop8)
        )
    }

}