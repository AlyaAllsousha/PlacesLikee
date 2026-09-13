package com.example.placeslikee.domain.models.extensions

enum class SortOption {
    DATE_DESC,
    DATE_ASC,
    LIKE_ASC,
    LIKES_DESC,
    NAME_ASC,
    NAME_DESC
}

enum class FilterOption {
    ALL,
    WITH_PHOTO

}

data class FilterSortState(
    val sortOption: SortOption = SortOption.DATE_DESC,
    val filterOption: FilterOption = FilterOption.ALL
)