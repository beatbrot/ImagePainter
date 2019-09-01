package de.beatbrot.imagepainter.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
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

    init {
        scaleType = ScaleType.FIT_CENTER
    }

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

    @Suppress("UNNECESSARY_SAFE_CALL")
    fun reset() {
        undoStack?.clear()
        redoStack?.clear()
        invalidate()
        undoStatusChangeListener?.undoStatusChanged(canUndo())
        redoStatusChangeListener?.redoStatusChanged(canRedo())
    }

    @JvmOverloads
    fun exportImage(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        val result = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
        val scaleX: Float = drawable.intrinsicWidth / width.toFloat()
        val scaleY: Float = drawable.intrinsicHeight / height.toFloat()
        val canvas = Canvas(result)
        drawable.draw(canvas)

        drawOverlay(canvas, calculateScale())
        return result
    }

    fun setUndoStatusChangeListener(undoStatusChangeListener: UndoStatusChangeListener) {
        this.undoStatusChangeListener = undoStatusChangeListener
        this.undoStatusChangeListener?.undoStatusChanged(canUndo())
    }

    @JvmSynthetic
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

    @JvmSynthetic
    fun setRedoStatusChangeListener(redoStatusChangeListener: (Boolean) -> Unit) {
        setRedoStatusChangeListener(object : RedoStatusChangeListener {
            override fun redoStatusChanged(canRedo: Boolean) {
                redoStatusChangeListener(canRedo)
            }
        })
    }

    private fun normalizeX(xCord: Float): Float {
        return when (val scale = calculateScale()) {
            is XOffsetScale -> {
                when {
                    xCord < scale.offset -> scale.offset
                    xCord > (width - scale.offset) -> width - scale.offset
                    else -> xCord
                }
            }
            else -> xCord
        }
    }

    private fun normalizeY(yCord: Float): Float {
        return when (val scale = calculateScale()) {
            is YOffsetScale -> {
                when {
                    yCord < scale.offset -> scale.offset
                    yCord > (height - scale.offset) -> height - scale.offset
                    else -> yCord
                }
            }
            else -> yCord
        }
    }

    private fun calculateScale(): OffsetScale {
        val drawableHeight = drawable.intrinsicHeight.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()

        val scaledX: Float = width / drawableWidth
        val scaledY: Float = height / drawableHeight

        return when {
            scaledX == scaledY -> NoOffsetScale((scaledX))
            scaledX > scaledY -> {
                val offset: Float = ((drawableWidth * scaledX) - (drawableWidth * scaledY)) / 2
                XOffsetScale(1 / scaledY, offset)
            }
            else -> {
                val offset: Float = ((drawableHeight * scaledY) - (drawableHeight * scaledX)) / 2
                YOffsetScale(1 / scaledX, offset)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawOverlay(canvas)
    }

    private fun drawOverlay(canvas: Canvas, scale: OffsetScale = NoScale) {
        for (path in undoStack) {
            canvas.drawPath(path.scale(scale))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                redoStack.clear()
                redoStatusChangeListener?.redoStatusChanged(canRedo())
                val path = DrawPath(Paint(paint))
                path.addDot(normalizeX(event.x), normalizeY(event.y))
                undoStack.addLast(path)
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                undoStack.last.addDot(normalizeX(event.x), normalizeY(event.y))
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

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        reset()
    }

    private fun Canvas.drawPath(path: DrawPath) {
        drawPath(path.path, path.paint)
    }
}
