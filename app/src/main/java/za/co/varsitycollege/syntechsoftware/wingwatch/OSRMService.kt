package za.co.varsitycollege.syntechsoftware.wingwatch.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import za.co.varsitycollege.syntechsoftware.wingwatch.models.OSRMRouteResponse

interface OSRMService {
    @GET("route/v1/driving/{coordinates}")
    fun getRoute(
        @Path("coordinates") coordinates: String,  // Coordinates: "startLng,startLat;endLng,endLat"
        @Query("overview") overview: String = "full", // Level of detail for the polyline
        @Query("geometries") geometries: String = "polyline"  // Geometry format
    ): Call<OSRMRouteResponse>
}