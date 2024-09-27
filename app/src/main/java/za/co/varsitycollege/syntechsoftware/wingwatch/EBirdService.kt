package za.co.varsitycollege.syntechsoftware.wingwatch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EBirdService {
    @GET("ref/hotspot/geo")
    fun getNearbyHotspots(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("dist") distance: Int = 10, // Distance in kilometers
        @Header("X-eBirdApiToken") token: String
    ): Call<List<Hotspot>>
}