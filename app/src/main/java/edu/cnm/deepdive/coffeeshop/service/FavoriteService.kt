package edu.cnm.deepdive.coffeeshop.service

import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.repository.FavoriteRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

@Singleton
class FavoriteService @Inject constructor(private val favoriteRepository: FavoriteRepository){

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun addFavorite(shop: Shop)=
        scope.future {
            favoriteRepository.addFavorite(shop)
        }

    suspend fun removeFavorite(shop: Shop)=
        scope.future {
            favoriteRepository.removeFavorite(shop)
        }

    suspend fun getFavorites(): CompletableFuture<List<Shop>> =
        scope.future {
        favoriteRepository.getFavorites()
    }
}