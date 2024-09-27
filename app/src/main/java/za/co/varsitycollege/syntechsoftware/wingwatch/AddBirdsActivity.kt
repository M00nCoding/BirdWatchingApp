package za.co.varsitycollege.syntechsoftware.wingwatch

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

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
    private lateinit var imageBitmap: Bitmap

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

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
        locationET = findViewById(R.id.locationEt)
        submitBirdBtn = findViewById(R.id.submitbutton)

        dbRef = FirebaseDatabase.getInstance().getReference("birds")
        auth = FirebaseAuth.getInstance()

        // Camera Launcher using Activity Result API
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras
                val imageBitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable("data", Bitmap::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras?.getParcelable("data")
                }

                if (imageBitmap != null) {
                    birdImageIV.setImageBitmap(imageBitmap)
                    this.imageBitmap = imageBitmap
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Camera button click event
        birdImageBtn.setOnClickListener {
            dispatchTakePictureIntent()  // Directly launch the camera
        }

        // Date picker button
        birdDateFoundBtn.setOnClickListener {
            showDatePickerDialog()
        }

        // Submit bird button
        submitBirdBtn.setOnClickListener {
            if (validateInputs()) {
                if (::imageBitmap.isInitialized) {
                    saveBirdDataWithImage()
                } else {
                    saveBirdData()
                }
            }
        }
    }

    // Launch camera intent
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(takePictureIntent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val selectedDate = sdf.format(calendar.time)
                dateSightTv.text = selectedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        val birdName = birdNameET.text.toString().trim()
        val birdDescription = birdDescriptionET.text.toString().trim()
        val birdLocation = locationET.text.toString().trim()
        val dateSighted = dateSightTv.text.toString().trim()

        if (birdName.isEmpty()) {
            birdNameET.error = "Enter Bird Name"
            return false
        }

        if (birdDescription.isEmpty()) {
            birdDescriptionET.error = "Enter Description"
            return false
        }

        if (birdLocation.isEmpty()) {
            locationET.error = "Enter Location"
            return false
        }

        if (dateSighted.isEmpty()) {
            Toast.makeText(this, "Select Date Sighted", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveBirdData() {
        val birdName = birdNameET.text.toString()
        val birdDescription = birdDescriptionET.text.toString()
        val birdLocation = locationET.text.toString()
        val dateSighted = dateSightTv.text.toString()

        val birdId = dbRef.push().key!!
        val uid = auth.currentUser?.uid.toString()

        val bird = BirdModel(birdId, birdName, birdDescription, birdLocation, dateSighted, "", uid)

        dbRef.child(birdId).setValue(bird)
            .addOnCompleteListener {
                Toast.makeText(this, "Bird added successfully!", Toast.LENGTH_LONG).show()
                clearFields()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveBirdDataWithImage() {
        val birdName = birdNameET.text.toString()
        val birdDescription = birdDescriptionET.text.toString()
        val birdLocation = locationET.text.toString()
        val dateSighted = dateSightTv.text.toString()

        uploadBitmapToFirebase(imageBitmap, birdName) { imageUrl ->
            val birdId = dbRef.push().key!!
            val uid = auth.currentUser?.uid.toString()

            val bird = BirdModel(birdId, birdName, birdDescription, birdLocation, dateSighted, imageUrl, uid)

            dbRef.child(birdId).setValue(bird)
                .addOnCompleteListener {
                    Toast.makeText(this, "Bird added successfully!", Toast.LENGTH_LONG).show()
                    clearFields()
                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadBitmapToFirebase(bitmap: Bitmap, imageName: String, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference.child("bird_images/$imageName.jpg")

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()

        storageReference.putBytes(data)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        birdNameET.text.clear()
        birdDescriptionET.text.clear()
        locationET.text.clear()
        dateSightTv.text = ""
        birdImageIV.setImageResource(0)
    }
}
