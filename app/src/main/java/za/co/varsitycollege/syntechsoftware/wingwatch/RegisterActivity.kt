package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Get references to the input fields and button
        val usernameEt = findViewById<EditText>(R.id.usernameEtR)
        val emailEt = findViewById<EditText>(R.id.emailEtR)
        val passwordEt = findViewById<EditText>(R.id.passwordEtR)
        val createAccountBtn = findViewById<Button>(R.id.createAccountBtn)
        val termsCb = findViewById<CheckBox>(R.id.termsCb)

        createAccountBtn.setOnClickListener {
            val username = usernameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (username.length > 10) {
                Toast.makeText(this, "Username cannot be more than 10 characters", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Password must be at most 8 characters, include 1 capital letter, 2 special characters, and 3 numbers",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!termsCb.isChecked) {
                Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, email, password)
            }
        }

        // Navigate to LoginActivity if the user already has an account
        val tvLoginLink: TextView = findViewById(R.id.loginLinkTv)
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Hash the password before storing
        val hashedPassword = hashPassword(password)

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    val userMap = hashMapOf(
                        "userId" to userId,
                        "username" to username,
                        "email" to email,
                        "password" to hashedPassword // Store hashed password
                    )

                    // Store user info in Firebase Realtime Database
                    if (userId != null) {
                        database.child(userId).setValue(userMap).addOnCompleteListener {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                            // Navigate to LoginActivity
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    // Handle registration failure
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }


    // Function to hash the password using SHA-256
    private fun hashPassword(password: String): String {
        val digest = SHA256.Digest()
        val hash = digest.digest(password.toByteArray())
        return Hex.toHexString(hash)
    }
}

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>])(?=.*\\d).{1,8}\$"
        return password.matches(passwordPattern.toRegex())
    }

