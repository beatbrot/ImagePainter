package de.beatbrot.imagepainter.util

import android.view.MotionEvent
import de.beatbrot.imagepainter.view.ImagePainterView

fun ImagePainterView.startAt(x: Int, y: Int) {
    onTouchEvent(motionEvent(MotionEvent.ACTION_DOWN, x, y))
}

fun ImagePainterView.moveTo(x: Int, y: Int) {
    onTouchEvent(motionEvent(MotionEvent.ACTION_MOVE, x, y))
}

fun ImagePainterView.lift() {
    onTouchEvent(motionEvent(MotionEvent.ACTION_UP, 1, 1))
}

private fun motionEvent(action: Int, x: Int, y: Int): MotionEvent {
    return MotionEvent.obtain(1L, 1L, action, x.toFloat(), y.toFloat(), 0)
}