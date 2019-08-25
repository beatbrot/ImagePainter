package de.beatbrot.imagepainter.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import de.beatbrot.imagepainter.DrawPath
import de.beatbrot.imagepainter.RedoStatusChangeListener
import de.beatbrot.imagepainter.UndoStatusChangeListener
import java.util.*

class ImagePainterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    @get:ColorInt
    var strokeColor: Int
        get() = paint.color
        set(@ColorInt value) {
            paint.color = value
        }

    var strokeWidth: Float
        get() = paint.strokeWidth
        set(value) {
            paint.strokeWidth = value
        }

    var strokeCap: Paint.Cap
        get() = paint.strokeCap
        set(value) {
            paint.strokeCap = value
        }

    private var undoStatusChangeListener: UndoStatusChangeListener? = null

    private var redoStatusChangeListener: RedoStatusChangeListener? = null

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5F
        color = Color.BLACK
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val undoStack: Deque<DrawPath> = LinkedList()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val redoStack: Deque<DrawPath> = LinkedList()

    fun undo() {
        if (!canUndo()) {
            throw UnsupportedOperationException("Cannot undo, no operation on the stack")
        }
        redoStack.push(undoStack.removeLast())
        invalidate()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun canUndo() = undoStack.isNotEmpty()

    fun redo() {
        if (!canRedo()) {
            throw UnsupportedOperationException("Cannot redo, no operation on the stack")
        }
        undoStack.addLast(redoStack.pop())
        invalidate()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun canRedo() = redoStack.isNotEmpty()

    fun reset() {
        undoStack.clear()
        redoStack.clear()
        invalidate()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    @JvmOverloads
    fun exportImage(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        val bitmap =
            Bitmap.createBitmap((width * scaleX).toInt(), (height * scaleY).toInt(), config)
        draw(Canvas(bitmap))
        return bitmap
    }

    fun setUndoStatusChangeListener(undoStatusChangeListener: UndoStatusChangeListener) {
        this.undoStatusChangeListener = undoStatusChangeListener
        this.undoStatusChangeListener?.undoStatusChanged(canUndo())
    }

    fun setUndoStatusChangeListener(undoStatusChangeListener: (Boolean) -> Unit) {
        setUndoStatusChangeListener(object : UndoStatusChangeListener {
            override fun undoStatusChanged(canUndo: Boolean) {
                undoStatusChangeListener(canUndo)
            }
        })
    }

    fun setRedoStatusChangeListener(redoStatusChangeListener: RedoStatusChangeListener) {
        this.redoStatusChangeListener = redoStatusChangeListener
        this.redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    fun setRedoStatusChangeListener(redoStatusChangeListener: (Boolean) -> Unit) {
        setRedoStatusChangeListener(object : RedoStatusChangeListener {
            override fun redoStatusChanged(canRedo: Boolean) {
                redoStatusChangeListener(canRedo)
            }
        })
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        for (path in undoStack) {
            canvas?.drawPath(path)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                redoStack.clear()
                redoStatusChangeListener?.redoStatusChanged(canRedo())
                val path = DrawPath(paint)
                path.addDot(event.x / scaleX, event.y / scaleY)
                undoStack.addLast(path)
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                undoStack.last.addDot(event.x / scaleX, event.y / scaleY)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                undoStatusChangeListener?.undoStatusChanged(canUndo())
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    private fun Canvas.drawPath(path: DrawPath) {
        drawPath(path.path, path.paint)
    }
}
