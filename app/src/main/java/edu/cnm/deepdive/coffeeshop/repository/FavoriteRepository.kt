package edu.cnm.deepdive.coffeeshop.repository

import edu.cnm.deepdive.coffeeshop.model.conversion.toShop
import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.FavoriteRequestDto
import edu.cnm.deepdive.coffeeshop.model.dto.openapi.ShopDto
import edu.cnm.deepdive.coffeeshop.service.openapi.FavoriteApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val favoriteApi: FavoriteApi){

    suspend fun addFavorite(shop: Shop): Shop {
        val requestDto = FavoriteRequestDto(shopId = shop.id)
        return favoriteApi.createFavorite(requestDto).let {
            if (it.isSuccessful) {
                it.body()?.toShop() ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun removeFavorite(shop: Shop) {
        return favoriteApi.deleteFavorite(shop.id).let {
            if (!it.isSuccessful) {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    suspend fun getFavorites(): List<Shop>{
        return favoriteApi.listMyFavorites().let {
            if (it.isSuccessful) {
                it.body()?.map { it.toShop() } ?: throw ServiceException("Unexpected response")
            } else {
                throw ServiceException(it.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

}