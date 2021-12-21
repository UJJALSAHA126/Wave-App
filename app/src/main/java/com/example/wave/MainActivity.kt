package com.example.wave

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import coil.load
import com.example.wave.databinding.ActivityMainBinding
import com.example.wave.databinding.NavHeaderBinding
import com.example.wave.fragments.ChatHome
import com.example.wave.fragments.meme.MemeFragment
import com.example.wave.fragments.profile.ProfileFragment
import com.example.wave.fragments.settings.SettingsFragment
import com.example.wave.loginactivity.LoginActivity
import com.example.wave.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var thisUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val header = NavHeaderBinding.inflate(layoutInflater, binding.navigationDrawer, false)
        binding.navigationDrawer.addHeaderView(header.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("user").child(auth.uid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userDetailsFound(snapshot, header)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.Open,
            R.string.Close
        )
        toggle.isDrawerIndicatorEnabled = true
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationDrawer.setCheckedItem(R.id.chatMenu)
        changeFragment(ChatHome(), "Chats")

        header.userDrawerDp.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            ShowDP.showDialog(this, thisUser.photo, thisUser.name)
        }

        header.editIcon.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (binding.navigationDrawer.checkedItem?.itemId != R.id.profileMenu) {
                binding.navigationDrawer.setCheckedItem(R.id.profileMenu)
                changeFragment(ProfileFragment(), "Update Profile")
            }
        }

        binding.navigationDrawer.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (it.itemId != binding.navigationDrawer.checkedItem?.itemId) {
                when (it.itemId) {

                    R.id.chatMenu -> {
                        changeFragment(ChatHome(), "Chats")
                    }

                    R.id.profileMenu -> {
                        changeFragment(ProfileFragment(), "Profile")
                    }

                    R.id.settingsMenu -> {
                        changeFragment(SettingsFragment(), "Settings")
                    }

                    R.id.memeMenu -> {
                        changeFragment(MemeFragment(), "7U Memes")
                    }

                    R.id.logOutMenu -> {
                        logOutUser()
                    }
                }
            }

            true
        }

    }

    private fun logOutUser() {

        val dialog = Dialog(this, R.style.customDialog)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_logout_dialog)
        val noBtn: TextView = dialog.findViewById(R.id.noButton)
        val yesBtn: TextView = dialog.findViewById(R.id.yesButton)

        yesBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun userDetailsFound(snapshot: DataSnapshot, header: NavHeaderBinding) {
        try {
            val userTemp = snapshot.getValue(User::class.java)
            thisUser = userTemp!!

            header.userDrawerDp.load(thisUser.photo) {
                placeholder(R.drawable.placeholder_image)
            }

            header.userDrawerName.text = thisUser.name
            header.userDrawerPhone.text = thisUser.phone
            header.userDrawerEmail.text = thisUser.email
            if(auth.currentUser!!.isEmailVerified){
                header.userDrawerEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check,0)
            }
            else{
                header.userDrawerEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_error_24,0)
            }

        } catch (e: Exception) {
            Log.d("HelloGoru", "Hello  " + e.message.toString())
        }
    }

    private fun changeFragment(frag: Fragment, title: String) {
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.frame, frag).commit()
        supportActionBar?.title = title
    }

}
//        navController = findNavController(R.id.fragments)
//        binding.navigationDrawer.setupWithNavController(navController)
//
//        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
//        setupActionBarWithNavController(navController, appBarConfiguration)
