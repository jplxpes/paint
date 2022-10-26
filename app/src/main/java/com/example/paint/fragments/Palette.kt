package com.example.paint.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.paint.ColorDTO
import com.example.paint.R
import com.example.paint.mainActivity.MainActivityViewModel

class Palette : Fragment(R.layout.fragment_drawing_board) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startSeeKBar(view.findViewById(R.id.b), "blue")
        startSeeKBar(view.findViewById(R.id.g), "green")
        startSeeKBar(view.findViewById(R.id.r), "red")
        startSeeKBar(view.findViewById(R.id.size), "size")

        val undo = view.findViewById<Button>(R.id.undo)
        undo?.setOnClickListener {
            if (viewModel.drawingHistory.value!!.size > 0) {
                val removed = viewModel.drawingHistory.value!!.removeLast()
                viewModel.removedHistory.value!!.addFirst(removed)
                viewModel.removedHistory.postValue(viewModel.removedHistory.value)
                viewModel.drawingHistory.postValue(viewModel.drawingHistory.value)
            }
        }

        viewModel.brushColor.observe(viewLifecycleOwner) {
            view.findViewById<LinearLayout>(R.id.colorView).setBackgroundColor(it)
            view.findViewById<SeekBar>(R.id.r).progress = it.red
            view.findViewById<SeekBar>(R.id.g).progress = it.green
            view.findViewById<SeekBar>(R.id.b).progress = it.blue
        }


        val redo = view.findViewById<Button>(R.id.redo)
        redo?.setOnClickListener {
            if (viewModel.removedHistory.value!!.size > 0) {
                val new = viewModel.removedHistory.value!!.removeFirst()
                viewModel.drawingHistory.value!!.add(new)
                viewModel.drawingHistory.postValue(viewModel.drawingHistory.value)
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_palette, container, false)
    }

    private fun startSeeKBar(seekBar: SeekBar, param: String) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(
                seek: SeekBar, progress: Int, fromUser: Boolean
            ) {
                when (param) {
                    "red" -> {
                        val c = ColorDTO(
                            blue = viewModel.brushColor.value!! and 0xFF,
                            green = (viewModel.brushColor.value!! shr 8) and 0xFF,
                            red = progress
                        )

                        viewModel.brushColor.postValue(
                            Color.rgb(
                                c.red ?: 0,
                                c.green ?: 0,
                                c.blue ?: 0,
                            )
                        )
                        return
                    }
                    "green" -> {
                        val c = ColorDTO(
                            blue = viewModel.brushColor.value!! and 0xFF,
                            green = progress,
                            red = (viewModel.brushColor.value!! shr 16) and 0xFF
                        )
                        viewModel.brushColor.postValue(
                            Color.rgb(
                                c.red ?: 0,
                                c.green ?: 0,
                                c.blue ?: 0,
                            )
                        )
                        return
                    }
                    "blue" -> {
                        val c = ColorDTO(
                            blue = progress,
                            green = (viewModel.brushColor.value!! shr 8) and 0xFF,
                            red = (viewModel.brushColor.value!! shr 16) and 0xFF
                        )
                        viewModel.brushColor.postValue(
                            Color.rgb(
                                c.red ?: 0,
                                c.green ?: 0,
                                c.blue ?: 0,
                            )
                        )
                        return
                    }
                    "size" -> {
                        viewModel.brushSize.postValue(progress.toFloat())
                        return
                    }
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
    }


}