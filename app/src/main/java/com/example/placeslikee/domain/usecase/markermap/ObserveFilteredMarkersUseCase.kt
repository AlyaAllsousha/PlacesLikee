package com.example.placeslikee.domain.usecase.markermap

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.models.extensions.FilterOption
import com.example.placeslikee.domain.models.extensions.FilterSortState
import com.example.placeslikee.domain.models.extensions.SortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveFilteredMarkersUseCase @Inject constructor(
    private val getMapMarkUseCase: GetMapMarkUseCase,
) {
    operator fun invoke(
        sourceFlow: Flow<List<UIMarker>>,
        queryFlow: Flow<String>,
        filterStateFlow: Flow<FilterSortState>
    ): Flow<List<UIMarker>> {
        return combine(
            sourceFlow,
            queryFlow,
            filterStateFlow
        ) { markers, query, filterState ->
            var result = markers

            if (query.isNotBlank()) {
                val lowerCaseQuery = query.lowercase()
                result = result.filter { marker ->
                    marker.name.lowercase().contains(lowerCaseQuery) ||
                            (marker.authorName).lowercase().contains(lowerCaseQuery) ||
                            (lowerCaseQuery.startsWith("#") && (marker.description
                                ?: "").lowercase().contains(lowerCaseQuery))
                }
            }
            result = when(filterState.filterOption){
                FilterOption.ALL -> result
                FilterOption.WITH_PHOTO -> result.filter{it.image != null && it.image.isNotBlank()}
            }
            result = when(filterState.sortOption){
                SortOption.DATE_DESC -> result.sortedByDescending{it.createdAt}
                SortOption.DATE_ASC -> result.sortedBy{it.createdAt}
                SortOption.LIKE_ASC -> result.sortedBy{it.likesAmount}
                SortOption.LIKES_DESC -> result.sortedByDescending{it.likesAmount}
                SortOption.NAME_ASC -> result.sortedBy{it.authorName}
                SortOption.NAME_DESC -> result.sortedByDescending{it.authorName}
            }
            result
        }.flowOn(Dispatchers.Default)
    }
}