package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val menuButton: Button = findViewById(R.id.menu)
        val hotspotsButton: Button = findViewById(R.id.hotspots)
        val goalsButton: Button = findViewById(R.id.goals)
        val birdsButton: Button = findViewById(R.id.birds)
        // Get the logged-in user's userId and username from the Intent
        intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")

        Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()

        menuButton.setOnClickListener {
            startActivity(Intent(this, menuButton::class.java))
        }

        hotspotsButton.setOnClickListener {
            startActivity(Intent(this, hotspotsButton::class.java))
        }

        goalsButton.setOnClickListener {
            startActivity(Intent(this, goalsButton::class.java))
        }

        birdsButton.setOnClickListener {
            startActivity(Intent(this, birdsButton::class.java))
        }
    }

    // function still needs to be added to the AddBirdsActivity so the info is not added to that activity yet as I just created it for now
    fun onClickableImageClick() {
        // Assuming there's an AddBirdsActivity class
        val intent = Intent(this, AddBirdsActivity::class.java)
        startActivity(intent)
    }
}
