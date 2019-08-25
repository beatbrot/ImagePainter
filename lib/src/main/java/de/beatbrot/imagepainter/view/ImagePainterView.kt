package de.beatbrot.imagepainter.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import de.beatbrot.imagepainter.DrawPath
import de.beatbrot.imagepainter.RedoStatusChangeListener
import de.beatbrot.imagepainter.UndoStatusChangeListener
import java.util.*

class ImagePainterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener {

    var image: Bitmap? = null
        set(value) {
            field = value
            bitmapCache = value?.copy(value.config, true)!!
            canvas = Canvas(bitmapCache)
            setImageBitmap(field)
            undoStack.clear()
            redoStack.clear()
            undoStatusChangeListener?.undoStatusChanged(canUndo())
            redoStatusChangeListener?.redoStatusChanged(canRedo())
        }

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

    private lateinit var canvas: Canvas

    private lateinit var bitmapCache: Bitmap

    private val scale: Float
        get() = width.toFloat() / bitmapCache.width

    internal val undoStack: Deque<DrawPath> = LinkedList()

    internal val redoStack: Deque<DrawPath> = LinkedList()

    init {
        setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (image == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                redoStack.clear()
                redoStatusChangeListener?.redoStatusChanged(canRedo())
                val path = DrawPath(paint)
                path.addDot(event.x / scale, event.y / scale)
                undoStack.addLast(path)
            }
            MotionEvent.ACTION_MOVE -> {
                undoStack.last.addDot(event.x / scale, event.y / scale)
                replayStack()
            }
            MotionEvent.ACTION_UP -> {
                replayStack()
                undoStatusChangeListener?.undoStatusChanged(canUndo())
            }
        }
        return true
    }

    fun canUndo() = undoStack.isNotEmpty()

    fun canRedo() = redoStack.isNotEmpty()

    fun undo() {
        redoStack.push(undoStack.removeLast())
        replayStack()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    fun redo() {
        undoStack.addLast(redoStack.pop())
        replayStack()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    fun reset() {
        undoStack.clear()
        redoStack.clear()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
        clearImage()

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

    fun getResult() = bitmapCache

    private fun clearImage() {
        bitmapCache = image.copy()
        canvas = Canvas(bitmapCache)
        setImageBitmap(bitmapCache)
    }

    private fun replayStack() {
        clearImage()
        for (path in undoStack) {
            canvas.drawPath(path)
        }
        setImageBitmap(bitmapCache)
    }

    private fun Bitmap?.copy(isMutable: Boolean = true): Bitmap {
        return this?.copy(config, isMutable) ?: throw NullPointerException("Copying failed")
    }

    private fun Canvas.drawPath(path: DrawPath) {
        drawPath(path.path, path.paint)
    }
}