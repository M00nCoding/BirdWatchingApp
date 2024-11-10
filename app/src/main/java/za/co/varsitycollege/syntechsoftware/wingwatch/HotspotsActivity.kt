package za.co.varsitycollege.syntechsoftware.wingwatch

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.varsitycollege.syntechsoftware.wingwatch.network.OSRMService
import za.co.varsitycollege.syntechsoftware.wingwatch.models.OSRMRouteResponse
import java.util.Date
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper



class HotspotsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val locationRequestCode = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var userLocation: LatLng

    private lateinit var database: DatabaseReference
    private lateinit var birdObservationsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var settingsListener: ValueEventListener

    // Updated list of predefined hotspots with more locations
    private val predefinedHotspots = listOf(
        LatLng(-34.0181, 18.4241),  // Cape Town
        LatLng(-25.7461, 28.1881),  // Pretoria
        LatLng(-26.2041, 28.0473),  // Johannesburg
        LatLng(-33.9249, 18.4241),  // Cape Town (Table Mountain)
        LatLng(-29.8587, 31.0218),  // Durban
        LatLng(-34.0522, 18.5562),  // Hermanus
        LatLng(-33.9333, 22.4676),  // Knysna
        LatLng(-32.3899, 26.8212),  // East London
        LatLng(-28.4791, 30.3792),  // Underberg
        LatLng(-29.5972, 30.5967),  // Howick
        LatLng(-32.2185, 27.9509),  // Port Elizabeth
        LatLng(-29.4486, 30.5552),  // Pietermaritzburg
        LatLng(-28.4142, 24.8474),  // Klerksdorp
        // National Parks
        LatLng(-24.9965, 31.5547),  // Kruger National Park
        LatLng(-33.9714, 19.2234),  // Table Mountain National Park
        LatLng(-29.0643, 28.5764),  // Addo Elephant National Park
        LatLng(-22.0437, 14.7818),  // Kgalagadi Transfrontier Park
        LatLng(-34.0515, 20.2258),  // Bontebok National Park
        LatLng(-31.8017, 26.5504),  // Mountain Zebra National Park
        LatLng(-31.5645, 24.1443),  // Karoo National Park
        LatLng(-33.5424, 19.1604),  // West Coast National Park
        LatLng(-29.0651, 30.4952),  // Ujung Kulon National Park
        LatLng(-25.5000, 28.8600),  // Marakele National Park
        LatLng(-29.5000, 31.0000),  // Hluhluwe-Imfolozi Park
        LatLng(-26.5345, 27.8553),  // Pilanesberg National Park
        LatLng(-29.1150, 31.0025),  // iSimangaliso Wetland Park
        LatLng(-32.1432, 22.4528),  // Addo Elephant National Park (Zuurberg)
        LatLng(-33.7712, 18.3713),  // Cape Point Nature Reserve
        // Additional Hotspots
        LatLng(-24.7917, 30.1648),  // Blyde River Canyon
        LatLng(-28.1506, 26.2304),  // Golden Gate Highlands National Park
        LatLng(-33.9587, 22.6599),  // Tsitsikamma National Park
        LatLng(-29.0469, 29.2242),  // Royal Natal National Park
        LatLng(-26.1313, 27.8694),  // Cradle of Humankind
        LatLng(-25.7038, 28.7762),  // Dinokeng Game Reserve
        LatLng(-33.7265, 19.2584),  // Agulhas National Park
        LatLng(-28.8003, 30.8780),  // Drakensberg Mountains
        LatLng(-34.1845, 19.2921),  // De Hoop Nature Reserve
        LatLng(-33.5881, 18.3386),  // Kirstenbosch National Botanical Garden
        LatLng(-29.8628, 31.0223),  // Oribi Gorge Nature Reserve
        LatLng(-25.7941, 29.5261),  // Mpumalanga Nature Reserve
        LatLng(-29.4127, 30.0855),  // Sani Pass
        LatLng(-34.4102, 19.4240),  // Ceres Valley
        LatLng(-26.1118, 28.9650),  // Delta Park
        LatLng(-29.5170, 30.6546),  // Balgowan
        LatLng(-26.0661, 28.5134),  // Modderfontein Reserve
        LatLng(-29.8722, 31.0413)   // False Bay Nature Reserve
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspots)

        // Database reference
        database = FirebaseDatabase.getInstance().getReference("settings")
        birdObservationsRef = FirebaseDatabase.getInstance().getReference("birds")
        auth = FirebaseAuth.getInstance()
        fetchSettingsFromFirebase()
        //Initialize
        val homeButton: Button = findViewById(R.id.home)
        val birdsButton: Button = findViewById(R.id.birds)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val logoutIcon: ImageView = findViewById(R.id.logout)

        // Navigate to HomeActivity (Menu - Navigation bar)
        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        /*
        //Not yet functional (Goals - Navigation bar)
        goalsButton.setOnClickListener {
            startActivity(Intent(this, goalsButton::class.java))
        }
        */
        // Navigate to AddBirdsActivity (Birds - Navigation bar)
        birdsButton.setOnClickListener {
            startActivity(Intent(this, AddBirdsActivity::class.java))
        }
        // Navigate to SettingsActivity (Settings)
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        // Set logout button click listener (Log out)
        logoutIcon.setOnClickListener {
            logoutUser()  // Call the logout function
        }
        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Check and request location permissions
        checkLocationPermission()

    }

    // Function to add a bird observation at the user's current location
    private fun addBirdObservation() {
        if (this::userLocation.isInitialized) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val observation = mapOf(
                    "userId" to userId,
                    "latitude" to userLocation.latitude,
                    "longitude" to userLocation.longitude,
                    "timestamp" to Date().time
                )

                // Push observation to Firebase with userId as part of the data
                birdObservationsRef.push().setValue(observation)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Observation added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add observation: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Location not initialized yet.", Toast.LENGTH_SHORT).show()
        }
    }


    // Check location permissions and request if needed
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationRequestCode
            )
        } else {
            getUserLocation() // Get user location if permission is granted
            displayPredefinedHotspots() // Display hotspots if permission is granted
            displayUserObservations()  // Display all user observations on map
        }
    }


    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)

                } ?: run {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error fetching location: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        displayPredefinedHotspots()  // Directly call to confirm the hotspots display

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        map.setOnMarkerClickListener { marker ->
            calculateRouteToHotspot(marker.position)
            true
        }
    }

    // Function to display bird observations for the current user on the map
    private fun displayUserObservations() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            birdObservationsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    map.clear()  // Clear any existing markers on the map

                    snapshot.children.forEach { observationSnapshot ->
                        val storedUserId = observationSnapshot.child("userId").getValue(String::class.java)
                        if (storedUserId == userId) {
                            val latitude = observationSnapshot.child("latitude").getValue(Double::class.java)!!
                            val longitude = observationSnapshot.child("longitude").getValue(Double::class.java)!!
                            val birdName = observationSnapshot.child("birdName").getValue(String::class.java) ?: "Unknown Bird"
                            val imageUrl = observationSnapshot.child("imageUrl").getValue(String::class.java) // optional field for image URL

                            // Create LatLng for the observation's coordinates
                            val observationLocation = LatLng(latitude, longitude)

                            // Create the marker on the map
                            val markerOptions = MarkerOptions()
                                .position(observationLocation)
                                .title(birdName)

                            // Optionally, set a custom icon (bird image)
                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get().load(imageUrl).into(object : Target {
                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        // Check if bitmap is null
                                        if (bitmap != null) {
                                            val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
                                            markerOptions.icon(icon)
                                        } else {
                                            // Handle the case where bitmap is null (fallback icon)
                                            Log.e("HotspotsActivity", "Bitmap is null, setting default icon.")
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker())
                                        }
                                    }

                                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                        // Handle failure in loading bitmap
                                        Log.e("HotspotsActivity", "Failed to load bitmap", e)
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker())
                                    }

                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                        // Handle placeholder image (optional)
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker())
                                    }
                                })
                            } else {
                                // If there's no image URL, use a default marker
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker())
                            }

                            // Add the marker to the map
                            map.addMarker(markerOptions)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HotspotsActivity", "Failed to read bird observations: ${error.message}")
                }
            })
        }
    }

    // Simulate fetching maxTravelDistance and measurementUnit from Firebase
    private fun fetchSettingsFromFirebase() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            settingsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("SettingsFetch", "Snapshot: ${snapshot.value}")

                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val maxTravelDistanceStr = childSnapshot.child("maxTravelDistance").getValue(String::class.java)
                            val measurementUnit = childSnapshot.child("measurementUnit").getValue(String::class.java) ?: "Kilometers"
                            val maxTravelDistance = maxTravelDistanceStr?.toFloatOrNull()
                            updateHotspots(maxTravelDistance, measurementUnit)
                            break
                        }
                    } else {
                        Log.e("SettingsFetch", "No settings found for the current user.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SettingsFetch", "Failed to fetch settings: ${error.message}")
                }
            }

            database.orderByChild("userId").equalTo(uid).addValueEventListener(settingsListener)
        } else {
            Log.e("SettingsFetch", "User not logged in.")
        }
    }

    private fun updateHotspots(maxTravelDistance: Float?, measurementUnit: String?) {
        val effectiveMeasurementUnit = measurementUnit ?: "Kilometers"
        val distanceInKilometers = if (effectiveMeasurementUnit == "Miles") {
            maxTravelDistance?.times(1.60934f)
        } else {
            maxTravelDistance
        }

        if (distanceInKilometers == null || maxTravelDistance == null) {
            displayPredefinedHotspots()
            displayUserObservations()
        } else {
            addHotspotsToMap(distanceInKilometers)
        }
    }

    private fun displayPredefinedHotspots() {
        if (::map.isInitialized) {
            // Introduce a slight delay to ensure the map is fully initialized before adding markers
            Handler(Looper.getMainLooper()).postDelayed({
            // Resize the custom icon before applying it to the markers
                val resizedIcon = BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(R.drawable.bird_map_icon, 100, 100))


                // Add predefined hotspots to the map with the resized icon
                for (hotspot in predefinedHotspots) {
                    map.addMarker(
                        MarkerOptions()
                            .position(hotspot)
                            .title("Hotspot")
                            .icon(resizedIcon) // Set custom icon here
                    )
                }

                // Optionally, move the camera to the first hotspot or user location
                if (predefinedHotspots.isNotEmpty()) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(predefinedHotspots[0], 8f))
                }
            }, 500) // Delay of 500ms to ensure map initialization
        } else {
            Log.e("HotspotsActivity", "Map is not initialized.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the listener to avoid memory leaks
        if (::settingsListener.isInitialized) {
            database.removeEventListener(settingsListener)
        }
    }

    // Function to resize the marker icon and ensure it has the shape of a location pin
    private fun resizeMarkerIcon(iconResId: Int, width: Int, height: Int): Bitmap {
        // Decode the resource to a Bitmap
        val imageBitmap = BitmapFactory.decodeResource(resources, iconResId)
        // Scale the bitmap to the desired size
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    private fun distanceBetween(latLng1: LatLng, latLng2: LatLng): Float {
        val location1 = Location("").apply {
            latitude = latLng1.latitude
            longitude = latLng1.longitude
        }
        val location2 = Location("").apply {
            latitude = latLng2.latitude
            longitude = latLng2.longitude
        }
        return location1.distanceTo(location2) // Distance in meters
    }

    private fun filterHotspotsByDistance(maxDistance: Float): List<LatLng> {
        return if (maxDistance == -1f) {
            // Return all hotspots if maxDistance is -1 (indicating that max distance is not found)
            predefinedHotspots
        } else {
            // Filter hotspots based on the distance if maxDistance is set
            predefinedHotspots.filter { hotspot ->
                val distance = distanceBetween(userLocation, hotspot)
                distance <= maxDistance * 1000  // Convert maxDistance to meters
            }
        }
    }

    private fun addHotspotsToMap(maxDistance: Float) {
        // Clear existing markers on the map
        map.clear()

        // Replace ic_location_pin with your location-shaped icon
        val resizedIcon = BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(R.drawable.bird_map_icon, 100, 100))


        // Filter hotspots based on the distance
        val nearbyHotspots = filterHotspotsByDistance(maxDistance)

        // Add the resized icon to each nearby hotspot marker
        nearbyHotspots.forEach { hotspot ->
            map.addMarker(
                MarkerOptions()
                    .position(hotspot)
                    .title("Hotspot")
                    .icon(resizedIcon) // Use the location pin icon here
            )
        }


        // Center the camera to the first nearby hotspot
        if (nearbyHotspots.isNotEmpty()) {
            val firstHotspot = nearbyHotspots[0]
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstHotspot, 12f))
        } else {
            Toast.makeText(this, "No hotspots within the specified distance", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateRouteToHotspot(destination: LatLng) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://router.project-osrm.org/")  // Use HTTPS for secure connection
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val osrmService = retrofit.create(OSRMService::class.java)
        val coordinates =
            "${userLocation.longitude},${userLocation.latitude};${destination.longitude},${destination.latitude}"

        osrmService.getRoute(coordinates).enqueue(object : Callback<OSRMRouteResponse> {
            @SuppressLint("DefaultLocale")
            override fun onResponse(
                call: Call<OSRMRouteResponse>,
                response: Response<OSRMRouteResponse>
            ) {
                if (response.isSuccessful) {
                    val routes = response.body()?.routes
                    if (!routes.isNullOrEmpty()) {
                        val polyline = routes[0].geometry
                        val distance = routes[0].distance / 1000  // Convert distance to km
                        val duration = routes[0].duration / 60    // Convert duration to minutes

                        // Rounding distance and duration to 2 decimal places
                        val roundedDistance = String.format("%.2f", distance)
                        val roundedDuration = String.format("%.2f", duration)

                        drawRouteOnMap(polyline)
                        Toast.makeText(
                            this@HotspotsActivity,
                            "Distance: $roundedDistance km, Duration: $roundedDuration min",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@HotspotsActivity, "No routes found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this@HotspotsActivity,
                        "Error fetching route: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<OSRMRouteResponse>, t: Throwable) {
                Toast.makeText(
                    this@HotspotsActivity,
                    "Failed to calculate route: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun drawRouteOnMap(encodedPolyline: String) {
        val decodedPath = PolyUtil.decode(encodedPolyline)
        val polylineOptions = PolylineOptions()
            .addAll(decodedPath)
            .color(android.graphics.Color.BLUE)
            .width(10f)

        map.addPolyline(polylineOptions)
    }

    fun logoutUser() {
        // Clear shared preferences or any stored session
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.clear()  // Clears all saved data
        editor.apply()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
        startActivity(intent)

        // Show a message to the user
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
