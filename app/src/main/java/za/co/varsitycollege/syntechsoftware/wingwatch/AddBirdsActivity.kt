package za.co.varsitycollege.syntechsoftware.wingwatch

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class AddBirdsActivity : AppCompatActivity() {

    private lateinit var birdImageIV: ImageView
    private lateinit var birdImageBtn: ImageButton
    private lateinit var birdDateFoundBtn: Button
    private lateinit var dateSightTv: TextView
    private lateinit var birdNameET: EditText
    private lateinit var birdDescriptionET: EditText
    private lateinit var locationET: EditText
    private lateinit var submitBirdBtn: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbird)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        birdImageIV = findViewById(R.id.birdImageIV)
        birdImageBtn = findViewById(R.id.birdImageBtn)
        birdDateFoundBtn = findViewById(R.id.birdDateFoundBtn)
        dateSightTv = findViewById(R.id.dateSightTv)
        birdNameET = findViewById(R.id.birdNameEt)
        birdDescriptionET = findViewById(R.id.birdDescriptionEt)
        locationET = findViewById(R.id.birdLocationEt)
        submitBirdBtn = findViewById(R.id.addBirdBtn)

        // Activity result for capturing image
        val getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        birdImageIV.setImageBitmap(bitmap)
                        imageBitmap = bitmap
                    }
                }
            }

        // Firebase references
        dbRef = FirebaseDatabase.getInstance().getReference("birds")
        auth = FirebaseAuth.getInstance()

        // Take photo button listener
        birdImageBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(intent)
        }

        // Date picker for bird sighting
        birdDateFoundBtn.setOnClickListener {
            showDatePickerDialog()
        }

        // Submit bird data button listener
        submitBirdBtn.setOnClickListener {
            if (validateInputs()) {
                saveBirdDataWithImage()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                dateSightTv.text = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        return when {
            birdNameET.text.isNullOrEmpty() -> {
                birdNameET.error = "Please enter bird name"
                false
            }
            birdDescriptionET.text.isNullOrEmpty() -> {
                birdDescriptionET.error = "Please enter bird description"
                false
            }
            locationET.text.isNullOrEmpty() -> {
                locationET.error = "Please enter location"
                false
            }
            dateSightTv.text.isNullOrEmpty() -> {
                dateSightTv.error = "Please select date sighted"
                false
            }
            imageBitmap == null -> {
                showAlertDialog()
                false
            }
            else -> true
        }
    }

    // Show alert dialog without repeating identical arguments
    private fun showAlertDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Image Missing")
        builder.setMessage("Please capture or select an image of the bird.")
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun saveBirdDataWithImage() {
        // Save bird data and image to Firebase
        // Function implementation for saving data
    }
}
