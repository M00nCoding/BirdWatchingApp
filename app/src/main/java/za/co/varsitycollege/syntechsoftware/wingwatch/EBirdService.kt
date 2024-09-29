package za.co.varsitycollege.syntechsoftware.wingwatch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Define your API service interface
interface EBirdService {
    @GET("hotspot/nearby")
    fun getNearbyHotspots(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("maxResults") maxResults: Int,
        @Query("appId") appId: String
    ): Call<List<Hotspot>>
}