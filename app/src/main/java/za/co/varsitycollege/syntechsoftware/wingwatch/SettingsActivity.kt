package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsActivity : AppCompatActivity() {

    //Declarations
    private lateinit var spMeasurementUnit: Spinner
    private lateinit var etMaxTravelDistance : EditText
    private lateinit var btnSaveSettings : Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Database reference
        database = FirebaseDatabase.getInstance().getReference("settings")
        auth = FirebaseAuth.getInstance()

        //Initialize
        spMeasurementUnit = findViewById(R.id.unitMeasurementSpinner)
        etMaxTravelDistance = findViewById(R.id.maxTravelDistanceEt)
        btnSaveSettings = findViewById(R.id.saveSettingsBtn)

        // Create a list of items for the spinner
        val items = listOf("Select unit of measurement", "Kilometers", "Miles")

        // Create an adapter for the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the spinner
        spMeasurementUnit.adapter = adapter

        // Set the default selection
        spMeasurementUnit.setSelection(0)

        //Call the saveSettings function when user clicks the 'Save Settings' button
        btnSaveSettings.setOnClickListener{
            saveSettings()
        }

        //Navigate back to the home page
        findViewById<Button>(R.id.backSettingsProfileBtn).setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //Log out
        findViewById<Button>(R.id.logoutBtn).setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //Save settings function
    private fun saveSettings(){

        //Initialize
        val selectedMeasurementUnit = spMeasurementUnit.selectedItem.toString()
        val maxTravelDistanceValue = etMaxTravelDistance.text.toString()
        val uid = auth.currentUser?.uid.toString()

        //Validation (Measurement unit spinner)
        if(selectedMeasurementUnit == "Select unit of measurement"){
            Toast.makeText(this,"Please select a unit of measurement", Toast.LENGTH_SHORT).show()
            return
        }

        //Validation (Maximum travel distance edit text)
        if(maxTravelDistanceValue.isEmpty()){
            Toast.makeText(this,"Please set your maximum travel distance", Toast.LENGTH_SHORT).show()
            return
        }

        //Search for userId in the settings node in the firebase real-time database
        val query = database.orderByChild("userId").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(settingsSnapshot in snapshot.children){
                        val foundUserId = settingsSnapshot.child("userId").getValue(String::class.java)
                        //If userId exists
                        if(foundUserId == uid){
                            //Get settingsId from the associated user
                            val settingsId = settingsSnapshot.child("settingsId").getValue(String::class.java).toString()

                            //Update settings data in firebase real-time database
                            val updateSettings = mapOf("measurementUnit" to selectedMeasurementUnit, "maxTravelDistance" to maxTravelDistanceValue)
                            database.child(settingsId).updateChildren(updateSettings).addOnSuccessListener {
                                Toast.makeText(this@SettingsActivity, "Settings successfully updated!", Toast.LENGTH_SHORT).show()
                                spMeasurementUnit.setSelection(0)
                                etMaxTravelDistance.text.clear()

                            }.addOnFailureListener{err ->
                                Toast.makeText(this@SettingsActivity, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    //If userId does not exist
                }else{
                    //Create unique settings id
                    val settingsId = database.push().key!!

                    //Pass captured data to settings model
                    val addSettings = SettingsModel(settingsId, selectedMeasurementUnit, maxTravelDistanceValue, uid)

                    //Store settings data in firebase real-time database
                    database.child(settingsId).setValue(addSettings).addOnCompleteListener{
                        Toast.makeText(this@SettingsActivity, "Settings successfully saved!", Toast.LENGTH_SHORT).show()
                        spMeasurementUnit.setSelection(0)
                        etMaxTravelDistance.text.clear()

                    }.addOnFailureListener{err ->
                        Toast.makeText(this@SettingsActivity, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            //Error handling (Database)
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

}
