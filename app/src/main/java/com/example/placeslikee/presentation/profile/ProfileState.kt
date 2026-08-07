package com.example.placeslikee.presentation.profile

import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.domain.models.UIMarker

sealed interface ProfileState {
    data object Loading: ProfileState
    data object Idle: ProfileState
    data object Unauthorized: ProfileState
    class Success(
        val markersList: List<UIMarker>,
        val user: UserEntity
    ): ProfileState
}
sealed class ProfileUiEvent {
    data class NameChanged(val message: String) : ProfileUiEvent()
    data class  EmailChangeEmailSent(val result: Result<String>) : ProfileUiEvent()
}