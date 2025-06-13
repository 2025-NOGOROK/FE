package com.example.nogorok.network.dto

import com.google.gson.annotations.SerializedName

data class TourResponse(
    @SerializedName("response")
    val response: TourOuterResponse
)

data class TourOuterResponse(
    @SerializedName("body")
    val body: TourBody
)

data class TourBody(
    @SerializedName("items")
    val items: TourItems
)

data class TourItems(
    @SerializedName("item")
    val item: List<TourItem>
)

data class TourItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("addr1")
    val address: String,
    @SerializedName("mapy")
    val latitude: Double,
    @SerializedName("mapx")
    val longitude: Double,
    @SerializedName("firstimage")
    val firstImage: String,
    @SerializedName("firstimage2")
    val firstImage2: String
)
