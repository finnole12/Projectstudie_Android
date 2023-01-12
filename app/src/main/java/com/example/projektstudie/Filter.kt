package com.example.projektstudie

data class Filter (
    var radius: Float = 1f,
    var sortMethod: SortMethods = SortMethods.Distance
)

enum class SortMethods {
    Distance,
    Popularity,
    Rating,
    Price
}