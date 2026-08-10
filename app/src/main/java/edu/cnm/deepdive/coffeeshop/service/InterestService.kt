package edu.cnm.deepdive.coffeeshop.service

import edu.cnm.deepdive.coffeeshop.model.domain.Interest
import edu.cnm.deepdive.coffeeshop.repository.InterestRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Singleton
class InterestService @Inject constructor(private val interestRepository: InterestRepository){

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getInterests(): CompletableFuture<List<Interest>> =
        scope.future {
        interestRepository.getInterests()
    }

    fun getInterest(id: UUID): CompletableFuture<Interest> =
        scope.future {
        interestRepository.getInterest(id)
    }
}