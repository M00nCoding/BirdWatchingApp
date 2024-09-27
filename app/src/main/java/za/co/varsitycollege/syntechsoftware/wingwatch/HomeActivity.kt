package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val menuButton: Button = findViewById(R.id.menu)
        val hotspotsButton: Button = findViewById(R.id.hotspots)
        val goalsButton: Button = findViewById(R.id.goals)
        val birdsButton: Button = findViewById(R.id.birds)
        val logoutIcon: ImageView = findViewById(R.id.logout)

        // Get the logged-in user's userId and username from the Intent
        intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")

        Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()

        menuButton.setOnClickListener {
            startActivity(Intent(this, menuButton::class.java))
        }

        // Navigate to HotspotsActivity (you need to replace HotspotsActivity with the correct activity class)
        hotspotsButton.setOnClickListener {
            startActivity(Intent(this, HotspotsActivity::class.java))
        }

        goalsButton.setOnClickListener {
            startActivity(Intent(this, goalsButton::class.java))
        }

        // Navigate to AddBirdsActivity
        birdsButton.setOnClickListener {
            startActivity(Intent(this, AddBirdsActivity::class.java))
        }

        // Set logout button click listener
        logoutIcon.setOnClickListener {
            logoutUser()  // Call the logout function
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

    fun onClickableImageClick() {
        val intent = Intent(this, AddBirdsActivity::class.java)
        startActivity(intent)
    }
}
