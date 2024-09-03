package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin : Button = findViewById(R.id.loginBtn)

        btnLogin.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            // Start RegisterActivity
            startActivity(intent)
        }

        val tvRegisterLink : TextView = findViewById(R.id.registerLinkTv)

        tvRegisterLink.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            // Start RegisterActivity
            startActivity(intent)
        }
    }
}