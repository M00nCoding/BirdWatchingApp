package za.co.varsitycollege.syntechsoftware.wingwatch.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import za.co.varsitycollege.syntechsoftware.wingwatch.models.GeoapifyResponse

interface GeoapifyService {
    @GET("v2/places")
    fun getNearbyParks(
        @Query("categories") categories: String = "leisure.park,natural.reserve",
        @Query("filter") filter: String,  // Filter by proximity using the radius
        @Query("bias") bias: String,  // Bias to prioritize results near the user's location
        @Query("limit") limit: Int = 20,  // Limit the number of results
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>
}