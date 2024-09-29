package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")


        // Get references to input fields and buttons
        val usernameEt = findViewById<EditText>(R.id.usernameEtL)
        val passwordEt = findViewById<EditText>(R.id.passwordEtL)
        val btnLogin = findViewById<Button>(R.id.loginBtn)
        val tvRegisterLink = findViewById<TextView>(R.id.registerLinkTv)


        // Login button click listener
        btnLogin.setOnClickListener {
            val username = usernameEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        // Navigate to RegisterActivity
        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        // Query the database for the username
        val query: Query = database.orderByChild("username").equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username exists, retrieve the email associated with this username
                    for (userSnapshot in dataSnapshot.children) {
                        val email = userSnapshot.child("email").value.toString()

                        // Now authenticate the user with FirebaseAuth using the email and password
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this@LoginActivity) { task ->
                                if (task.isSuccessful) {
                                    // Login successful
                                    val userId = auth.currentUser?.uid
                                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                                    // Redirect to HomeActivity
                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("username", username)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Authentication failed (e.g., wrong password)
                                    Toast.makeText(this@LoginActivity, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        break // Exit after finding the first match
                    }
                } else {
                    // Username not found
                    Toast.makeText(this@LoginActivity, "User does not exist. Please register or check your credentials again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@LoginActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}