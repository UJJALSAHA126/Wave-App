package com.example.wave.fragments.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.wave.CustomProgress
import com.example.wave.GetDatePicker
import com.example.wave.R
import com.example.wave.ShowDP.Companion.showDialog
import com.example.wave.customDialog.CustomDialog
import com.example.wave.databinding.FragmentProfileBinding
import com.example.wave.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var customDialog: CustomDialog
    private lateinit var datePicker: GetDatePicker
    private var thisUser = User()

    private var cDay = 29
    private var cMonth = 5
    private var cYear = 2001

    private val IMAGE_CODE = 101
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        requireActivity().toolbar.title = "Update Profile"

        customDialog = CustomDialog(requireContext())
        customDialog.startLoading()
        datePicker = GetDatePicker(requireContext(), this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        reference = database.reference.child("user").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                customDialog.stopLoading()
                userDetailsFound(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.myDOBProfile.setOnClickListener {

            if (binding.myDOBProfile.text.isEmpty()) {
                datePicker.build().apply {
                    datePicker.maxDate = Date().time
                    show()
                }

            } else {
                var tempDOB: String = binding.myDOBProfile.text.toString()
                val x = tempDOB.indexOf('/')
                cDay = tempDOB.take(x).toInt()
                tempDOB = tempDOB.drop(x + 1)
                val y = tempDOB.indexOf('/')
                cMonth = tempDOB.take(y).toInt() - 1
                tempDOB = tempDOB.drop(y + 1)
                cYear = tempDOB.toInt()
            }

            datePicker.build(cDay,cMonth,cYear).apply {
                datePicker.maxDate = Date().time
                show()
            }
        }

        binding.myDPProfile.setOnClickListener {
            showDialog(requireContext(), thisUser.photo, thisUser.name)
        }

        binding.editDP.setOnClickListener {
            getDpFromStorage()
        }

        binding.cardVBtn.setOnClickListener { v ->
            binding.cardVBtn.isClickable = false
            val button = CustomProgress(requireContext(), v)
            button.clicked()
            val newName = binding.myNameProfile.text.toString().trim()
            val newPhone = "+91" + binding.myPhoneProfile.text.toString().trim()
            thisUser.dob = binding.myDOBProfile.text.toString().trim()
            thisUser.about = if (binding.myAboutProfile.text.isEmpty()) {
                "Let's Wave Together !"
            } else {
                binding.myAboutProfile.text.toString().trim()
            }

            if (checkCondition(newName, newPhone, thisUser.dob)) {
                thisUser.name = newName
                thisUser.phone = newPhone

                if (uri == null) {
                    updateUser(thisUser, button)
                } else {
                    storageReference.putFile(uri!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storageReference.downloadUrl.addOnSuccessListener {
                                val finalURL = it.toString()
                                thisUser.photo = finalURL
                                updateUser(thisUser, button)
                            }
                        } else if (task.isCanceled) {
                            button.reset("Failed")
                        }
                    }
                }
            } else {
                button.reset("Update")
                binding.cardVBtn.isClickable = true
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            try {
                requireActivity().finishAffinity()
            } catch (e: Exception) {
            }
        }

        return binding.root
    }

    private fun checkCondition(name: String, phone: String, myDOB: String): Boolean {
        return when {

            name.isEmpty() -> {
                Toast.makeText(context, "Please Fill Your Name", Toast.LENGTH_SHORT).show()
                false
            }

            (phone.length != 13) -> {
                Toast.makeText(
                    context,
                    "Please Fill Your Phone Number Properly",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            myDOB.isEmpty() || myDOB == "null" -> {
                Toast.makeText(
                    context,
                    "Please Please Provide Your Date Of Birth",
                    Toast.LENGTH_SHORT
                ).show()

                false
            }

            else -> true
        }
    }

    private fun getDpFromStorage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            IMAGE_CODE
        )
    }

    private fun userDetailsFound(snapshot: DataSnapshot) {
        try {
            val userTemp = snapshot.getValue(User::class.java)
            thisUser = userTemp!!

            val timeStamp = System.currentTimeMillis()
            storageReference =
                storage.reference.child("upload").child("${thisUser.name}${auth.uid!!}$timeStamp")

            binding.myDPProfile.load(thisUser.photo) {
                placeholder(R.drawable.placeholder_image)
            }

            binding.myNameProfile.setText(thisUser.name)
            binding.myDOBProfile.text = thisUser.dob
            binding.myPhoneProfile.setText(thisUser.phone.drop(3))
            binding.myAboutProfile.setText(thisUser.about)

        } catch (e: Exception) {
            Log.d("HelloGoru", "Hello  " + e.message.toString())
        }
    }

    private fun updateUser(user: User, button: CustomProgress) {
        reference.setValue(user).addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT)
                    .show()
                binding.cardVBtn.isClickable = true
                lifecycleScope.launch {
                    button.done()
                    delay(1500L)
                    button.reset("Update")
                }
            }
            if (task1.isCanceled) {
                binding.cardVBtn.isClickable = true
                button.reset("Failed")
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE) {
            data?.let {
                uri = it.data
                binding.myDPProfile.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date: String = "" + dayOfMonth + "/" + (month + 1) + "/" + year
        binding.myDOBProfile.text = date
    }

}