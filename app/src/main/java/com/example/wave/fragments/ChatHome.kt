package com.example.wave.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.wave.databinding.FragmentChatHomeBinding
import com.example.wave.singleton.MySingleTon

class ChatHome : Fragment() {

    private var _binding: FragmentChatHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatHomeBinding.inflate(inflater, container, false)

//        val newsUrl =
//            "https://newsapi.org/v2/everything?q=tesla&from=2021-07-12&sortBy=publishedAt&apiKey=ce2e56fcd432488492974b72ac57c13e"
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//            {
//                val jsonArray = it.getJSONArray("articles")
//                for (i in 0 until jsonArray.length()) {
//                    val obj = jsonArray.getJSONObject(i)
//                    val title = obj.getString("title")
//                    Log.d("KKKK", title)
//                }
//            },
//            {
//                it.cause.toString()
//                Log.d("KKKK", it.cause.toString())
//            })
//
//        MySingleTon(requireContext()).addToRequestQueue(jsonObjectRequest)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}