package com.example.wave.fragments.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wave.databinding.FragmentChatBinding
import com.example.wave.fragments.chat.adapter.UsersAdapter
import com.example.wave.fragments.chat.listener.ClickedOnChat
import com.example.wave.fragments.chat.model.ChatDetails
import com.example.wave.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class ChatFragment : Fragment(), ClickedOnChat {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference1: DatabaseReference
    private lateinit var allUsers: ArrayList<User>

    private var thisUser = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        Log.d("Destroy", "ChatFragmentCreated")

        requireActivity().toolbar.title = "Chats"
        binding.userRecyclerView.visibility = View.GONE
        binding.waitingText.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("user")
        databaseReference1 = database.reference.child("user").child(auth.uid!!)
        allUsers = ArrayList()
        val adapter = UsersAdapter(requireContext(), this)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = adapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                var count = 0
                allUsers.clear()
                Log.d("Destroy", "ChatFragmentFetched")
                if (_binding != null) {
                    binding.userRecyclerView.visibility = View.VISIBLE
                    binding.waitingText.visibility = View.GONE
                }

                for (dataSnapshot in snapshot.children) {
//                    count++
                    try {
                        val userTemp = dataSnapshot.getValue(User::class.java)
                        val user = userTemp!!
                        if (user.uid != auth.uid) {
                            allUsers.add(user)
                        }
                    } catch (e: Exception) {
                        Log.d("SnapShot", e.message.toString())
                    }
                }
//                Toast.makeText(requireContext(), "Count = $count", Toast.LENGTH_SHORT).show()
                adapter.setData(allUsers)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        databaseReference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userTemp = snapshot.getValue(User::class.java)
                    val user = userTemp!!

                    thisUser = user

                } catch (e: Exception) {
                    Log.d("HelloGoru", "Hello  " + e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback {
            try {
                requireActivity().finishAffinity()
            } catch (e: Exception) {
            }
        }

        return binding.root
    }

    override fun clicked(receiverName: String, receiverPhoto: String, receiverUID: String) {
        val receiver = ChatDetails(
            receiverName, receiverPhoto, receiverUID,
            thisUser.name, thisUser.photo, thisUser.uid
        )
        val action = ChatFragmentDirections.actionChatFragmentToCharWithUserFragment(receiver)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        Log.d("Destroy", "ChatFragmentDestroyed")
        super.onDestroyView()
        _binding = null
    }

}