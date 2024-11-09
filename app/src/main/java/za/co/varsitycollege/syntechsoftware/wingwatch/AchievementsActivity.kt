package za.co.varsitycollege.syntechsoftware.wingwatch

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AchievementsActivity: AppCompatActivity() {

    //Declarations
    private lateinit var bronzeProgressBar: ProgressBar
    private lateinit var silverProgressBar: ProgressBar
    private lateinit var goldProgressBar: ProgressBar
    private lateinit var diamondProgressBar: ProgressBar

    private lateinit var bronzePercentage: TextView
    private lateinit var silverPercentage: TextView
    private lateinit var goldPercentage: TextView
    private lateinit var diamondPercentage: TextView

    private lateinit var bronzeBadge : ImageView
    private lateinit var silverBadge : ImageView
    private lateinit var goldBadge : ImageView
    private lateinit var diamondBadge : ImageView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "AchievementsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        // Initialize variables
        bronzeProgressBar = findViewById(R.id.achievement_bar_bronze)
        silverProgressBar = findViewById(R.id.achievement_bar_silver)
        goldProgressBar = findViewById(R.id.achievement_bar_gold)
        diamondProgressBar = findViewById(R.id.achievement_bar_diamond)

        bronzePercentage = findViewById(R.id.achievement_percentage_bronze)
        silverPercentage = findViewById(R.id.achievement_percentage_silver)
        goldPercentage = findViewById(R.id.achievement_percentage_gold)
        diamondPercentage = findViewById(R.id.achievement_percentage_diamond)

        bronzeBadge = findViewById(R.id.achievement_badge_bronze)
        silverBadge = findViewById(R.id.achievement_badge_silver)
        goldBadge = findViewById(R.id.achievement_badge_gold)
        diamondBadge = findViewById(R.id.achievement_badge_diamond)

        val backButton: Button = findViewById(R.id.achievementsBackBtn)

        // Gets the current user that is logged in
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Gets the user's ID
            val uid = currentUser.uid
            database = FirebaseDatabase.getInstance().reference

            // Method to execute
            setProgressBars(uid)
        }

        // Navigate to AddBirdsActivity (Birds - Navigation bar)
        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setProgressBars(uid: String) {
        val birdsRef = database.child("birds").orderByChild("userId").equalTo(uid)

        birdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(birdSnapshot: DataSnapshot) {
                // Gets the user's total bird observations from firebase real-time database
                val totalBirds = birdSnapshot.childrenCount.toInt()

                // The current progress is equal to the total bird observations captured
                val currentProgress = totalBirds

                // Set max values for the progress bars (NB: Max values were multiplied by 100 to give a smoother animation to the progress bars)
                bronzeProgressBar.max = 1 * 100
                silverProgressBar.max = 25 * 100
                goldProgressBar.max = 50 * 100
                diamondProgressBar.max = 100 * 100

                // Set progress values for the progress bars (NB: Current progress values were multiplied by 100 to give a smoother animation to the progress bars)
                ObjectAnimator.ofInt(bronzeProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(silverProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(goldProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(diamondProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()

                // Calculate and display percentage for the bronze achievement
                bronzePercentage.text = if (currentProgress >= 1) {
                    bronzeBadge.setImageResource(R.drawable.bronze_badge)
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 1)
                }

                // Calculate and display percentage for the silver achievement
                silverPercentage.text = if (currentProgress >= 25) {
                    silverBadge.setImageResource(R.drawable.silver_badge)
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 25)
                }

                // Calculate and display percentage for the gold achievement
                goldPercentage.text = if (currentProgress >= 50) {
                    goldBadge.setImageResource(R.drawable.gold_badge)
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 50)
                }

                // Calculate and display percentage for the diamond achievement
                diamondPercentage.text = if (currentProgress >= 100) {
                    diamondBadge.setImageResource(R.drawable.diamond_badge)
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 100)
                }
            }

            // Error handling if the application failed to retrieve the user's total bird observations
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve total bird observations: ${error.message}")
                Toast.makeText(
                    this@AchievementsActivity,
                    "Failed to retrieve total bird observations",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}