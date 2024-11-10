package za.co.varsitycollege.syntechsoftware.wingwatch

data class BirdModel(
    val birdId: String = "",
    val birdName: String = "",
    val birdDescription: String = "",
    val birdLocation: String = "",
    val dateSighted: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

