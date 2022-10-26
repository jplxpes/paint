package com.example.paint.mainActivity

import android.app.Application
import android.graphics.Color
import android.graphics.Path
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.paint.PathWrapper
import com.example.paint.blackColor
import com.example.paint.defaultColor
import java.util.LinkedList


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    fun concludePath() {
        drawingHistory.value!!.add(current)
        current = PathWrapper(Path(), brushColor.value!!, brushSize.value!!)
    }

    fun switchMode(turnOnLightMode: Boolean) {

        var linesColor = Color.BLACK
        var backgroundColor = Color.WHITE

        if (!turnOnLightMode) {
            linesColor = Color.WHITE
            backgroundColor = Color.BLACK
        }

        canvasColor.postValue(backgroundColor)

        currentColor.postValue(linesColor)
        brushColor.postValue(linesColor)

        drawingHistory.value?.forEach {
            it.color = linesColor
        }
        removedHistory.value?.forEach {
            it.color = linesColor
        }

    }

    var canvasColor: MutableLiveData<Int> = MutableLiveData(Color.WHITE)

    var currentColor: MutableLiveData<Int> = MutableLiveData(defaultColor)

    var brushColor: MutableLiveData<Int> = MutableLiveData(blackColor)

    var brushSize: MutableLiveData<Float> = MutableLiveData(10F)

    var drawingHistory: MutableLiveData<LinkedList<PathWrapper>> = MutableLiveData(LinkedList())

    var removedHistory: MutableLiveData<LinkedList<PathWrapper>> = MutableLiveData(LinkedList())

    var current: PathWrapper = PathWrapper(Path(), brushColor.value!!, brushSize.value!!)

}