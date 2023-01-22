package com.example.projektstudie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseArray : ArrayList<ResponseObject>()

data class ResponseObject (
    @SerializedName("id") val ID: String,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("phonenumber") val phoneNumber: String,
    @SerializedName("picture") val picture: Any,
    @SerializedName("price_range") val price_range: Int,
    @SerializedName("menu") val menu: ArrayList<MenuEntry>,
    @SerializedName("ratings") val ratings: ArrayList<Rating>,
    @SerializedName("distance") val distance: Double,
    @SerializedName("highlight") val highlight: Boolean,
    @SerializedName("avg_rating") val avg_rating: Float,
): Serializable

data class MenuEntry (
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Float,
): Serializable

data class Rating (
    @SerializedName("id") val id: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("text") val text: String,
): Serializable