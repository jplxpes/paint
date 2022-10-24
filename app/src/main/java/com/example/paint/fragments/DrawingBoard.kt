package com.example.paint.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.paint.R
import com.example.paint.mainActivity.MainActivityViewModel

class DrawingBoard : Fragment(R.layout.fragment_drawing_board) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_drawing_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.canvasColor.observe(viewLifecycleOwner) {
            view.findViewById<View>(R.id.drawCanvas).setBackgroundColor(it)
        }
    }

}