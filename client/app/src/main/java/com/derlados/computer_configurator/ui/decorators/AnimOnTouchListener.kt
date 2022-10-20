package com.derlados.computer_configurator.ui.decorators

import android.graphics.drawable.TransitionDrawable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class AnimOnTouchListener(private val listener: OnTouchListener) : OnTouchListener {

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val transitionDrawable = view.background as TransitionDrawable
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                transitionDrawable.startTransition(150)
            }
            MotionEvent.ACTION_UP -> {
                transitionDrawable.reverseTransition(150)
                listener.onTouch(view, motionEvent)
            }
            MotionEvent.ACTION_CANCEL -> {
                transitionDrawable.reverseTransition(150)
            }
        }
        return true
    }
}