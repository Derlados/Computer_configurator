package com.derlados.computer_conf.views.decorators

import android.graphics.drawable.TransitionDrawable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R

class AnimOnTouchListener(private val listener: OnTouchListener) : OnTouchListener {

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val transitionDrawable = view.background as TransitionDrawable
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            transitionDrawable.startTransition(150)
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            transitionDrawable.reverseTransition(150)
            listener.onTouch(view, motionEvent)
        } else if (motionEvent.action == MotionEvent.ACTION_CANCEL) {
            transitionDrawable.reverseTransition(150)
        }
        return true
    }
}