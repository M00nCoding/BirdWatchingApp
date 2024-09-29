package za.co.varsitycollege.syntechsoftware.wingwatch

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//import android.util.Log
import com.bumptech.glide.Glide
//import com.google.firebase.storage.FirebaseStorage

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private val locationRequestCode = 101

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var birdsContainer: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("birds")

        // Initialize Views
        val hotspotsButton: Button = findViewById(R.id.hotspots)
        val birdsButton: Button = findViewById(R.id.birds)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val logoutIcon: ImageView = findViewById(R.id.logout)
        birdsContainer = findViewById(R.id.birdsContainer)

        /* Navigate to HomeActivity (Menu - Navigation bar)
        menuButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }*/

        // Navigate to HotspotsActivity (Hotspots - Navigation bar)
        hotspotsButton.setOnClickListener {
            startActivity(Intent(this, HotspotsActivity::class.java))
        }

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

        // Get the logged-in user's userId and username from the Intent
        intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")

        Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()

        // Display the current user's birds
        displayUserBirds()

        // Check if the location permission is granted
        checkLocationPermission()
    }

    private fun displayUserBirds() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            // Query the birds node for entries added by the current user
            database.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        birdsContainer.removeAllViews() // Clear any existing views

                        if (dataSnapshot.exists()) {
                            for (birdSnapshot in dataSnapshot.children) {
                                val bird = birdSnapshot.getValue(BirdModel::class.java)
                                bird?.let { addBirdToLayout(it) }  // Add bird to the layout
                            }
                        } else {
                            Toast.makeText(this@HomeActivity, "No birds added yet.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@HomeActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addBirdToLayout(bird: BirdModel) {
        // Create a new vertical LinearLayout for each bird entry
        val birdLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24) // Adjust padding to make the layout smaller
            background = resources.getDrawable(R.drawable.bird_info_background, null) // Set background image with rounded corners
        }

        // Bird Name TextView
        val birdNameTextView = TextView(this).apply {
            text = "Common Name: ${bird.birdName}"
            textSize = 18f
            setTextColor(resources.getColor(R.color.brown))
        }
        birdLayout.addView(birdNameTextView)

        // Bird Description TextView
        val birdDescriptionTextView = TextView(this).apply {
            text = "Description: ${bird.birdDescription}"
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        birdLayout.addView(birdDescriptionTextView)

        // Bird Location TextView
        val birdLocationTextView = TextView(this).apply {
            text = "Location: ${bird.birdLocation}"
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        birdLayout.addView(birdLocationTextView)

        // Date Sighted TextView
        val dateSightedTextView = TextView(this).apply {
            text = "Date Sighted: ${bird.dateSighted}"
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        birdLayout.addView(dateSightedTextView)

        // Bird ImageView
        val birdImageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT // Allow the height to wrap content based on image size
            )
            adjustViewBounds = true  // Maintain aspect ratio
            scaleType = ImageView.ScaleType.FIT_CENTER // Fit the image within the ImageView, preserving aspect ratio

            // Load the bird image from the URL using Glide
            Glide.with(this@HomeActivity)
                .load(bird.imageUrl)
                .into(this)
        }
        birdLayout.addView(birdImageView)

        // Add the completed bird layout to the birdsContainer
        birdsContainer.addView(birdLayout)
    }











    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if it's not granted
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode
            )
        }
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
