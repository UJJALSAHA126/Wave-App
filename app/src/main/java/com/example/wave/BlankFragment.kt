package com.example.wave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

//        val msg = "Hello Goru"
//        val code = Base64.encode(msg.toByteArray(),1).toString()
//        print(code)
//        val x = msg.encodeToByteArray()
//        val y = x.decodeToString()

//        val x = msg.htmlEncode()



        return View(requireContext())
    }

}