/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.utils

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

class SwipeDetector(view: View) : OnTouchListener {
    private var minDistance = 100
    private var downX = 0f
    private var downY = 0f
    private var upX = 0f
    private var upY = 0f
    private var swipeEventListener: OnSwipeEvent? = null

    init {
        view.setOnTouchListener(this)
    }

    fun setOnSwipeListener(listener: OnSwipeEvent?) {
        try {
            swipeEventListener = listener
        } catch (e: ClassCastException) {
            Log.e(
                "ClassCastException",
                "please pass SwipeDetector.OnSwipeEvent Interface instance",
                e
            )
        }
    }

    private fun onRightToLeftSwipe() {
        if (swipeEventListener != null) {
            swipeEventListener!!.swipeEventDetected(
                SwipeTypeEnum.RIGHT_TO_LEFT
            )
        } else {
            Log.e(
                "SwipeDetector error",
                "please pass SwipeDetector.OnSwipeEvent Interface instance"
            )
        }
    }

    private fun onLeftToRightSwipe() {
        if (swipeEventListener != null) {
            swipeEventListener!!.swipeEventDetected(
                SwipeTypeEnum.LEFT_TO_RIGHT
            )
        } else {
            Log.e(
                "SwipeDetector error",
                "please pass SwipeDetector.OnSwipeEvent Interface instance"
            )
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {
                upX = event.x
                upY = event.y
                val deltaX = downX - upX
                val deltaY = downY - upY

                if (abs(deltaX) > abs(deltaY)) {
                    if (abs(deltaX) > minDistance) {
                        if (deltaX < 0) {
                            onLeftToRightSwipe()
                            return true
                        }
                        if (deltaX > 0) {
                            onRightToLeftSwipe()
                            return true
                        }
                    } else {
                        return false
                    }
                } else {
                    return false
                }
                return true
            }
        }
        return false
    }

    interface OnSwipeEvent {
        fun swipeEventDetected(swipeType: SwipeTypeEnum)
    }

    enum class SwipeTypeEnum {
        RIGHT_TO_LEFT, LEFT_TO_RIGHT
    }
}
