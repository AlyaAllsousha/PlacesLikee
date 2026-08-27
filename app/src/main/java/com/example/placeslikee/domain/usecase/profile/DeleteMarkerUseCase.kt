package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.repositories.MarkRepository
import javax.inject.Inject

class DeleteMarkerUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    suspend operator fun invoke(id: String){
        repository.deleteMark(id)
    }
}