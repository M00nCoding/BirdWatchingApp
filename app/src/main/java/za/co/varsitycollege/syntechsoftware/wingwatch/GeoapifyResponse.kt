package za.co.varsitycollege.syntechsoftware.wingwatch.models

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val name: String,
    val category: List<String>
)

data class Geometry(
    val coordinates: List<Double>
)
