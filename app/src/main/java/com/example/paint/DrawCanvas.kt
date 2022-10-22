package com.example.paint

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.paint.mainActivity.MainActivityViewModel

class DrawCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs),
    View.OnTouchListener {

    private val activity: FragmentActivity by lazy {
        try {
            context as FragmentActivity
        } catch (exception: ClassCastException) {
            throw ClassCastException("Please ensure that the provided Context is a valid FragmentActivity")
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(activity).get<MainActivityViewModel>()
    }

    init {
        viewModel.drawingHistory.observe(activity){
            invalidate()
        }
    }

    private val brush = Paint()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event == null)
            throw NullPointerException()

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                viewModel.current.color = viewModel.brushColor.value!!
                viewModel.current.path.moveTo(event.x, event.y)
                viewModel.removedHistory.postValue(ArrayList())
            }

            MotionEvent.ACTION_MOVE -> {
                viewModel.current.path.lineTo(
                    event.x,
                    event.y
                )
            }

            MotionEvent.ACTION_UP -> {
                viewModel.concludePath()
                performClick()
            }

        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        brush.strokeWidth = 10F
        brush.style = Paint.Style.STROKE
        brush.color = viewModel.brushColor.value!!
        canvas.drawPath(viewModel.current.path, brush)

        if (viewModel.drawingHistory.value!!.size != 0) {

            viewModel.drawingHistory.value!!.forEach { pathWrapper ->
                brush.color = pathWrapper.color
                canvas.drawPath(pathWrapper.path, brush)
            }
        }
    }

}