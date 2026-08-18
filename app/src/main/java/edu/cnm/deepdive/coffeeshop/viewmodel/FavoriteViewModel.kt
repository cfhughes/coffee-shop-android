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
                shops?.forEach { it.isFavorite = true }
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
