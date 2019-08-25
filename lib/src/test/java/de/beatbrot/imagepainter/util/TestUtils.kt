package de.beatbrot.imagepainter.util

import android.view.MotionEvent
import de.beatbrot.imagepainter.view.ImagePainterView

fun ImagePainterView.startAt(x: Int, y: Int) {
    onTouch(
        this,
        MotionEvent.obtain(1L, 1L, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
    )
}

fun ImagePainterView.moveTo(x: Int, y: Int) {
    onTouch(
        this,
        MotionEvent.obtain(1L, 1L, MotionEvent.ACTION_MOVE, x.toFloat(), y.toFloat(), 0)
    )
}

fun ImagePainterView.lift() {
    onTouch(this, MotionEvent.obtain(1L, 1L, MotionEvent.ACTION_UP, 1F, 1F, 0))
}