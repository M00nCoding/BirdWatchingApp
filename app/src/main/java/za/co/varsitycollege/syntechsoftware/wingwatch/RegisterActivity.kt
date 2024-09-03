package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val tvLoginLink : TextView = findViewById(R.id.loginLinkTv)

        tvLoginLink.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            // Start LoginActivity
            startActivity(intent)
        }

    }

}