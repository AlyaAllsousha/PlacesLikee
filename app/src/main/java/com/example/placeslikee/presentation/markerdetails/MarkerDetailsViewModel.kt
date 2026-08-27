package com.example.placeslikee.presentation.markerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.auth.GetCurrentIdUseCase
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMarkerByIdUseCase
import com.example.placeslikee.domain.usecase.profile.DeleteMarkerUseCase
import com.example.placeslikee.domain.usecase.subscribe.ObserveSubscriptionUseCase
import com.example.placeslikee.domain.usecase.subscribe.ToggleSubscribeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MarkerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val toggleLikedUseCase: ToggleLikedUseCase,
    private val getMarkerByIdUseCase: GetMarkerByIdUseCase,
    private val getCurrentIdUseCase: GetCurrentIdUseCase,
    private val deleteMarkerUseCase: DeleteMarkerUseCase,
    private val toggleSubscribeUseCase: ToggleSubscribeUseCase,
    private val observeSubscriptionUseCase: ObserveSubscriptionUseCase
) : ViewModel() {


    val markerId: String = checkNotNull(savedStateHandle["markerId"])

    private val _markerDetails = MutableStateFlow<DetailsState>(DetailsState.Idle)
    val markerDetails = _markerDetails.asStateFlow()

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed = _isSubscribed.asStateFlow()

    private var observeSubscriptionJob: Job? = null
    //Defence from liking spam
    private var isLiking = false
    private var lastClickTime = 0L

    //Defence from subscribing spam
    private var isSubscribing = false
    private var lastSubscribeClickTime = 0L

    val currUserId = getCurrentIdUseCase()

    init {
        viewModelScope.launch {
            _markerDetails.value = DetailsState.Loading
            getMarkerByIdUseCase(markerId).collect { marker ->
                _markerDetails.value = DetailsState.Success(marker)
                observeSubscriptionJob?.cancel()
                val authorId = marker?.authorId
                if(authorId != null && authorId != currUserId){
                    observeSubscriptionJob = viewModelScope.launch {
                        observeSubscriptionUseCase(authorId).collect{subscribed ->
                            _isSubscribed.value = subscribed
                        }
                    }
                }
            }
        }
    }

    fun onToggleLike() {
        val currentTime = System.currentTimeMillis()
        if (isLiking || currentTime - lastClickTime < 500) return
        isLiking = true
        lastClickTime = currentTime
        viewModelScope.launch {
            try {
                toggleLikedUseCase(markerId)
            }
            finally {
                isLiking = false
            }
        }
    }

    fun onToggleSubscribe(){
        val currentTime = System.currentTimeMillis()
        if(isSubscribing || currentTime - lastSubscribeClickTime < 500) return

        val currentState = _markerDetails.value
        if(currentState !is DetailsState.Success) return

        val authorId = currentState.marker?.authorId
        val authorName = currentState.marker?.authorName ?: "Автор"

        if(authorId == null)  return

        isSubscribing = true
        lastSubscribeClickTime = currentTime
        viewModelScope.launch {
            try{
                toggleSubscribeUseCase(
                    authorId = authorId,
                    authorName = authorName,
                    isSubscribed = _isSubscribed.value
                )
            }
            finally {
                isSubscribing = false
            }
        }

    }

    fun onDeleteMarker(){
        viewModelScope.launch {
            deleteMarkerUseCase(markerId)
        }
    }
}