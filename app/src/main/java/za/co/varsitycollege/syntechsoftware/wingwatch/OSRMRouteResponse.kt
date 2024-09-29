package za.co.varsitycollege.syntechsoftware.wingwatch.models

data class OSRMRouteResponse(
    val routes: List<Route>
)

data class Route(
    val geometry: String,  // Encoded polyline for the route
    val legs: List<Leg>,
    val distance: Double,  // Total route distance
    val duration: Double   // Total route duration
)

data class Leg(
    val summary: String,
    val distance: Double,
    val duration: Double
)