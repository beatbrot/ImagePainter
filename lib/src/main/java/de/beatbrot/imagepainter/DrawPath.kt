package de.beatbrot.imagepainter

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import de.beatbrot.imagepainter.view.NoOffsetScale
import de.beatbrot.imagepainter.view.OffsetScale
import de.beatbrot.imagepainter.view.XOffsetScale
import de.beatbrot.imagepainter.view.YOffsetScale

internal data class DrawPath(val paint: Paint, val path: Path = Path()) {
    fun addDot(x: Float, y: Float) {
        if (path.isEmpty) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    fun scale(offsetScale: OffsetScale): DrawPath {
        if (offsetScale is NoOffsetScale && offsetScale.scale == 1F) {
            return this
        }

        val newPath = Path()
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(offsetScale.scale, offsetScale.scale)
        path.transform(scaleMatrix, newPath)

        if (offsetScale is XOffsetScale) {
            scaleMatrix.setTranslate(-offsetScale.offset, 0F)
            path.transform(scaleMatrix, newPath)
        } else if (offsetScale is YOffsetScale) {
            scaleMatrix.setTranslate(0F, -offsetScale.offset)
            path.transform(scaleMatrix, newPath)
        }

        val newPaint = Paint(paint)
        newPaint.strokeWidth *= offsetScale.scale

        return copy(path = newPath, paint = newPaint)
    }
}