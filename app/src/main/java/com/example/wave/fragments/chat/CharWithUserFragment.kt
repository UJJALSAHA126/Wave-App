package com.example.wave.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.wave.R
import com.example.wave.ShowDP
import com.example.wave.databinding.FragmentCharWithUserBinding
import com.example.wave.fragments.chat.adapter.MessageAdapter
import com.example.wave.model.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CharWithUserFragment : Fragment() {

    private val args by navArgs<CharWithUserFragmentArgs>()

    private var _binding: FragmentCharWithUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var allMessages: ArrayList<Messages>
    private lateinit var adapter: MessageAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    //    private lateinit var databaseReference0: DatabaseReference
    private lateinit var databaseReference1: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference

    private var receiverName = ""
    private var receiverUID = ""
    private var receiverPhoto = ""

    private var myName = ""
    private var myUID = ""
    private var myPhoto = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCharWithUserBinding.inflate(layoutInflater, container, false)

        allMessages = ArrayList()
        adapter = MessageAdapter(requireContext(), binding.chatRecyclerView)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = linearLayoutManager
        binding.chatRecyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        receiverName = args.connectionDetails.receiverName
        receiverUID = args.connectionDetails.receiverUID
        receiverPhoto = args.connectionDetails.receiverImage

        myName = args.connectionDetails.myName
        myPhoto = args.connectionDetails.myImage
        myUID = args.connectionDetails.myUID

        databaseReference1 = database.reference.child("chat").child(auth.uid!!).child(
            receiverUID
        )
        databaseReference2 = database.reference.child("chat").child(receiverUID)
            .child(auth.uid!!)

        databaseReference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("Size 1" ,allMessages.size.toString())
                allMessages.clear()
                for (dataSnap in snapshot.children) {
                    try {
                        val msg = dataSnap.getValue(Messages::class.java)
                        msg?.let {
                            allMessages.add(it)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "SnapShot Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
//                Log.d("Size 11" ,allMessages.size.toString())
                adapter.setMessages(allMessages)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        binding.receiverPhoto.load(receiverPhoto) {
            placeholder(R.drawable.placeholder_image)
        }
        binding.receiverName.text = receiverName
        binding.receiverPhoto.setOnClickListener {
            ShowDP.showDialog(requireContext(), receiverPhoto, receiverName)
        }

        binding.sendButton.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.send_button_anim)
            it.startAnimation(animation)
            val msgTxt = binding.sendMessage.text.toString().trim()
            var msg = ""
            for (i in msgTxt.indices) {
                msg += msgTxt[i] + 65
            }

            if (msgTxt == "") {
                return@setOnClickListener
            }
            binding.sendMessage.setText("")
            val time = System.currentTimeMillis()
            val message = Messages(
                msg, myName, myUID, myPhoto,
                receiverName, receiverUID, receiverPhoto, time
            )
            databaseReference1.push().setValue(message)
            databaseReference2.push().setValue(message)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}