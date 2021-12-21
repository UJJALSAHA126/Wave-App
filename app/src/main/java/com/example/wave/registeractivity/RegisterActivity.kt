package com.example.wave.registeractivity

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wave.CustomProgress
import com.example.wave.GetDatePicker
import com.example.wave.MainActivity
import com.example.wave.databinding.ActivityRegisterBinding
import com.example.wave.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val defaultImage =
        "https://firebasestorage.googleapis.com/v0/b/wave-50eff.appspot.com/o/profile_image.png?alt=media&token=3e9d75be-db05-413a-bd62-0b89f5fe56fa"

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var userName: String
    private lateinit var userPhone: String
    private lateinit var userDOB: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userProfileImageUri: String
    private lateinit var userAbout: String

    private lateinit var datePicker: GetDatePicker

    private var uri: Uri? = null

    private val IMAGE_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        datePicker = GetDatePicker(this, this)

        binding.datePickerBtn.setOnClickListener {

            datePicker.build().apply {
                datePicker.maxDate = Date().time
                show()
            }
        }

        binding.cardVBtn.setOnClickListener {

            val button = CustomProgress(this, it)
            button.clicked()

            val name = binding.userNameRegister.text.toString().trim()
            val surname = binding.userSurNameRegister.text.toString().trim()
            userDOB = binding.userDOBRegister.text.toString().trim()
            userPhone = binding.userPhoneRegister.text.toString()
            userEmail = binding.userEmailRegister.text.toString().trim()
            userAbout = "Let's Wave Together !"
            val pass1 = binding.userPasswordRegister.text.toString()
            val pass2 = binding.confirmUserPassword.text.toString()

            val condition: Boolean =
                checkCondition(name, surname, userDOB, userPhone, userEmail, pass1, pass2)

            if (condition) {
                userName = "$name $surname"
                userPassword = pass1
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { tsk ->
                        if (tsk.isSuccessful) {
                            try {
                                val timeStamp = System.currentTimeMillis()
                                val tempName = "($userName)${auth.uid!!}$timeStamp"
                                reference = database.reference.child("user").child(auth.uid!!)
                                storageReference =
                                    storage.reference.child("upload").child(tempName)

                                if (uri != null) {
                                    storageReference.putFile(uri!!).addOnCompleteListener { newIt ->
                                        if (newIt.isSuccessful) {
                                            storageReference.downloadUrl.addOnSuccessListener { imageU ->
                                                userProfileImageUri = imageU.toString()
                                                val user = User(
                                                    auth.uid!!, userName, userDOB, "+91$userPhone",
                                                    userEmail, userProfileImageUri, userAbout
                                                )
                                                addUser(user, button)
                                            }
                                        }
                                    }
                                } else {
                                    userProfileImageUri = defaultImage
                                    val user = User(
                                        auth.uid!!,
                                        userName, userDOB,
                                        "+91$userPhone",
                                        userEmail,
                                        userProfileImageUri, userAbout
                                    )
                                    addUser(user, button)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this, "Could Not Create Account", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Error in Registration\nCheck Your Network Connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                button.reset("Register")
            }
        }
        binding.userProfileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE) {
            if (data != null) {
                uri = data.data
                binding.userProfileImage.setImageURI(uri)
            }
        }
    }

    private fun addUser(user: User, button: CustomProgress) {
        reference.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                button.done()
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error Occurred !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCondition(
        name: String,
        surname: String,
        userDOB: String,
        userPhone: String,
        userEmail: String,
        pass1: String,
        pass2: String,
    ): Boolean {
        return if (name == "" || surname == "") {
            Toast.makeText(this, "Please Enter A Valid Name", Toast.LENGTH_SHORT).show()
            false
        } else if (userDOB.isEmpty()) {
            Toast.makeText(this, "Please Select Your Date Of Birth", Toast.LENGTH_SHORT).show()
            false
        } else if (userPhone == "" || userPhone.length < 10) {
            Toast.makeText(this, "Please Enter A Valid Phone Number", Toast.LENGTH_SHORT).show()
            false
        } else if (userEmail == "" || (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail)
                .matches())
        ) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show()
            false
        } else if (pass1 != pass2) {
            Toast.makeText(this, "Confirm Password Does Not Match !!", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date: String = "" + dayOfMonth + "/" + (month + 1) + "/" + year
        binding.userDOBRegister.setText(date)
    }

}