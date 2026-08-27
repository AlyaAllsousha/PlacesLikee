package com.example.placeslikee.domain.usecase.subscribe

import com.example.placeslikee.domain.repositories.SubscriptionsRepository
import javax.inject.Inject

class ToggleSubscribeUseCase @Inject constructor(
    private val repository: SubscriptionsRepository
) {
    suspend operator fun invoke(authorId: String, authorName: String, isSubscribed: Boolean) {
        if(isSubscribed){
            repository.unsubscribeFromAuthor(authorId)
        }
        else{
            repository.subscribeToAuthor(authorId, authorName)
        }

    }
}