package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HotspotsActivity: AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_REQUEST_CODE = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspots)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE
            )
        } else {
            // Permission is already granted
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        // Check if the location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            // Get the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // If location is found, get latitude and longitude
                    val latitude = it.latitude
                    val longitude = it.longitude

                    // Fetch nearby bird hotspots using these coordinates
                    fetchNearbyBirdHotspots(latitude, longitude)
                } ?: run {
                    // Handle case where location is null (GPS turned off, etc.)
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // Request location permission if it's not granted
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, fetch the user location
                getUserLocation()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchNearbyBirdHotspots(lat: Double, lng: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.ebird.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val eBirdService = retrofit.create(EBirdService::class.java)

        eBirdService.getNearbyHotspots(lat, lng, 10, "q1lknch0u797")
            .enqueue(object : Callback<List<Hotspot>> {
                override fun onResponse(
                    call: Call<List<Hotspot>>, response: Response<List<Hotspot>>
                ) {
                    val hotspots = response.body()
                    hotspots?.let {
                        // Display hotspots on the map
                        addHotspotsToMap(it)
                    }
                }

                override fun onFailure(call: Call<List<Hotspot>>, t: Throwable) {
                    // Handle failure
                }
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Enable user location if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }
    }

    private fun addHotspotsToMap(hotspots: List<Hotspot>) {
        for (hotspot in hotspots) {
            val location = LatLng(hotspot.lat, hotspot.lng)
            map.addMarker(MarkerOptions().position(location).title(hotspot.locName))
        }

        // Move the camera to the first hotspot
        if (hotspots.isNotEmpty()) {
            val firstHotspot = LatLng(hotspots[0].lat, hotspots[0].lng)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstHotspot, 12f))
        }
    }

}