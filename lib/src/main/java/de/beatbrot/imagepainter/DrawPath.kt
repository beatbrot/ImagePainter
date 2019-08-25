package de.beatbrot.imagepainter

import android.graphics.Paint
import android.graphics.Path

class DrawPath(paint: Paint) {
    val path = Path()

    val paint: Paint = Paint(paint)

    internal fun addDot(x: Float, y: Float) {
        if (path.isEmpty) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrawPath

        if (path != other.path) return false
        if (paint != other.paint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + paint.hashCode()
        return result
    }


}