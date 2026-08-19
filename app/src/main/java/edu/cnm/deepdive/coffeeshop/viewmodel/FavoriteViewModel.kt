package edu.cnm.deepdive.coffeeshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.service.FavoriteService
import jakarta.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val favoriteService: FavoriteService) :
    ViewModel() {

    private val _favorites = MutableLiveData<List<Shop>>(emptyList())
    val favorites: LiveData<List<Shop>> = _favorites
    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    init {
        fetchFavorites()
    }


    fun fetchFavorites() {
        favoriteService.getFavorites()
            .thenAccept { shops ->
                shops?.forEach { shop ->
                    shop.isFavorite = true

                    // Attach preferences if they aren't loaded from the backend
                    if (shop.preferences.isEmpty()) {
                        val prefs = mutableListOf("Oat / Almond Milk")
                        shop.name?.let { name ->
                            if (name.contains("Amalie")) {
                                prefs.addAll(
                                    listOf(
                                        "Work / Study Friendly",
                                        "Outdoor Patio",
                                        "Comfy Seating"
                                    )
                                )
                            } else if (name.contains("Zendo")) {
                                prefs.addAll(
                                    listOf(
                                        "Pet Friendly",
                                        "Outdoor Patio",
                                        "Vegan Options"
                                    )
                                )
                            } else if (name.contains("Little Bear")) {
                                prefs.addAll(
                                    listOf(
                                        "House-Roasted Beans",
                                        "Strong Wi-Fi",
                                        "Artisan Pour-Over"
                                    )
                                )
                            } else {
                                prefs.addAll(listOf("Strong Wi-Fi", "Power Outlets"))
                            }
                        }
                        shop.preferences = prefs
                    }
                }
                _favorites.postValue(shops)
            }
            .exceptionally { throwable ->
                _error.postValue(throwable)
                null
            }
    }

    fun addFavorite(shop: Shop) {
        shop.isFavorite = true
        favoriteService.addFavorite(shop)
            .thenRun(::fetchFavorites)
            .exceptionally { throwable ->
                _error.postValue(throwable)
                null
            }
    }

    fun removeFavorite(shop: Shop) {
        shop.isFavorite = false
        favoriteService.removeFavorite(shop)
            .thenRun(::fetchFavorites)
            .exceptionally { throwable ->
                _error.postValue(throwable)
                null
            }
    }

}
