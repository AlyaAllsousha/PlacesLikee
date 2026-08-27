package com.example.placeslikee.domain.usecase.subscribe

import com.example.placeslikee.domain.repositories.SubscriptionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionsRepository
) {
    operator fun invoke (authorId: String) : Flow<Boolean>{
        return repository.observeIsSubscribed(authorId)
    }
}