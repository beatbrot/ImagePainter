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

        var newPath = when (offsetScale) {
            is XOffsetScale -> path.translate(-offsetScale.offset, 0F)
            is YOffsetScale -> path.translate(0F, -offsetScale.offset)
            else -> path
        }

        newPath = newPath.scale(1 / offsetScale.scale)

        val newPaint = Paint(paint)
        newPaint.strokeWidth *= 1 / offsetScale.scale

        return copy(path = newPath, paint = newPaint)
    }

    private fun Path.translate(xOffset: Float, yOffset: Float): Path {
        val newPath = Path()
        val matrix = Matrix()
        matrix.setTranslate(xOffset, yOffset)
        transform(matrix, newPath)
        return newPath
    }

    private fun Path.scale(factor: Float): Path {
        val newPath = Path()
        val matrix = Matrix()
        matrix.setScale(factor, factor)
        transform(matrix, newPath)
        return newPath
    }
}