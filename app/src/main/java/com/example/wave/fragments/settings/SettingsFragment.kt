package com.example.wave.fragments.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wave.R
import com.example.wave.customDialog.CustomDialog
import com.example.wave.customDialog.CustomToast
import com.example.wave.databinding.FragmentSettingsBinding
import com.example.wave.loginactivity.LoginActivity
import com.example.wave.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var customToast: CustomToast
    private lateinit var customDialog: CustomDialog

    private var thisUser = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        customDialog = CustomDialog(requireContext())
        customToast = CustomToast(requireContext())
        customDialog.stopLoading()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("user").child(auth.uid!!)

        setDrawable(user.isEmailVerified)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                customDialog.stopLoading()
                userDetailsFound(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.verifyBtnSettings.setOnClickListener {
            showDialog()
        }

        binding.userEmailSettings.setText(user.email)
        binding.userEmailSettings.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setDrawable(user.isEmailVerified)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.updateLogin.setOnClickListener {
            val newEmail = binding.userEmailSettings.text.toString().trim()
            val newPass = binding.userPasswordSettings.text.toString()
            val newConfirmPass = binding.confirmPasswordSettings.text.toString()

            if (conditionCheck(newEmail, newPass, newConfirmPass)) {
                customDialog.startLoading()
                updateLoginDetails(newEmail, newPass)
            }
        }

        return binding.root
    }

    private fun updateLoginDetails(newEmail: String, newPass: String) {

        if (newEmail != user.email) {
            thisUser.email = newEmail
            user.updateEmail(newEmail)
                .addOnSuccessListener {
                    reference.setValue(thisUser)
                        .addOnSuccessListener {
                            updatePassword(newPass)
                        }
                        .addOnFailureListener { e0 ->
                            failed(e0)
                        }
                }
                .addOnFailureListener { e1 ->
                    failed(e1)
                }
        } else {
            updatePassword(newPass)
        }
    }

    private fun updatePassword(newPass: String) {
        user.updatePassword(newPass)
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e2 ->
                failed(e2)
            }
    }

    private fun userDetailsFound(snapshot: DataSnapshot) {
        try {
            val userTemp = snapshot.getValue(User::class.java)
            thisUser = userTemp!!
            setDrawable(user.isEmailVerified)

        } catch (e: Exception) {
            Log.d("HelloGoru", "Hello  " + e.message.toString())
        }
    }

    private fun setDrawable(yoo: Boolean) {
        Log.d("KKKK", yoo.toString())
        if (yoo) {
            binding.verifyBtnSettings.isClickable = false
            binding.verifyBtnSettings.visibility = View.GONE
            binding.userEmailSettings.error = null
            binding.texLay1.setEndIconDrawable(R.drawable.ic_check)
        } else {
            binding.verifyBtnSettings.isClickable = true
            binding.verifyBtnSettings.visibility = View.VISIBLE
            binding.userEmailSettings.error = "Email Is Not Verified"
            binding.texLay1.endIconDrawable = null
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext(), R.style.customDialog)
//        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_logout_dialog)
        val noBtn: TextView = dialog.findViewById(R.id.noButton)
        val yesBtn: TextView = dialog.findViewById(R.id.yesButton)
        val img: ImageView = dialog.findViewById(R.id.img)
        val txt: TextView = dialog.findViewById(R.id.txt)

        "After Verification\nPlease Log In Again ...".also { txt.text = it }
//        txt.textSize = 20F
        img.visibility = View.GONE

        yesBtn.setOnClickListener {
            customDialog.startLoading()
            sendVerificationCode()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun sendVerificationCode() {
        user.sendEmailVerification().addOnCompleteListener { task0 ->
            if (task0.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Verification Link Is Sent To Your Email.",
                    Toast.LENGTH_SHORT
                ).show()
                returnToLoginActivity()
            } else if (task0.isCanceled) {
                Toast.makeText(requireContext(), "Error Occurred !!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun returnToLoginActivity() {
        auth.signOut()
        customDialog.stopLoading()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun success() {
        customDialog.stopLoading()
        customToast.showToast("SuccessFul")
        Log.d("UDate", "Successful")
    }

    private fun failed(e: Exception) {
        customDialog.stopLoading()
        customToast.showToast("Failed")
        Log.d("UDate", e.message.toString())
    }

    private fun conditionCheck(newEmail: String, newPass: String, newConfirmPass: String): Boolean {

        return when {
            !(android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail)).matches() -> {
                binding.userEmailSettings.error = "Invalid Email"
                customToast.showToast("Please Enter A Valid Email Address")
                false
            }

            newPass.isEmpty() -> {
                binding.userPasswordSettings.error = "Invalid Password"
                false
            }

            newPass != newConfirmPass -> {
                binding.confirmPasswordSettings.error = "Password Does Not Match"
                false
            }
            else -> true
        }
    }

    override fun onResume() {
        super.onResume()
        setDrawable(user.isEmailVerified)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}