package za.co.varsitycollege.syntechsoftware.wingwatch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.varsitycollege.syntechsoftware.wingwatch.network.OSRMService
import za.co.varsitycollege.syntechsoftware.wingwatch.models.OSRMRouteResponse

class HotspotsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val locationRequestCode = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var userLocation: LatLng

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

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check and request location permissions
        checkLocationPermission()
    }

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
            getUserLocation()
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
                    addHotspotsToMap()  // Adding predefined hotspots to the map
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


    // Function to resize the marker icon and ensure it has the shape of a location pin
    private fun resizeMarkerIcon(iconResId: Int, width: Int, height: Int): Bitmap {
        // Decode the resource to a Bitmap
        val imageBitmap = BitmapFactory.decodeResource(resources, iconResId)
        // Scale the bitmap to the desired size
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }


    private fun addHotspotsToMap() {
        // Replace ic_location_pin with your location-shaped icon
        val resizedIcon = BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(R.drawable.bird_map_icon, 100, 100))

        // Add the resized icon to each hotspot marker
        predefinedHotspots.forEach { hotspot ->
            map.addMarker(
                MarkerOptions()
                    .position(hotspot)
                    .title("Hotspot")
                    .icon(resizedIcon) // Use the location pin icon here
            )
        }

        // Center the camera to the first hotspot
        if (predefinedHotspots.isNotEmpty()) {
            val firstHotspot = predefinedHotspots[0]
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstHotspot, 12f))
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

}
