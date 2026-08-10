package edu.cnm.deepdive.coffeeshop.service

import edu.cnm.deepdive.coffeeshop.model.domain.Shop
import edu.cnm.deepdive.coffeeshop.repository.ShopRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Singleton
class ShopService @Inject constructor(private val shopRepository: ShopRepository){

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getShop(id: UUID) =
        scope.future {
            shopRepository.getShop(id)
        }

    suspend fun getShops(): CompletableFuture<List<Shop>> =
        scope.future {
            shopRepository.getShops()
        }
}