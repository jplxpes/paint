package com.example.paint.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.paint.onlineActivity.OnlineActivity
import com.example.paint.R

class OnlineFragment : Fragment(R.layout.fragment_online) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val online = view.findViewById<Button>(R.id.online)
        online?.setOnClickListener {
            startActivity(Intent(activity, OnlineActivity::class.java))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_online, container, false)
    }


}