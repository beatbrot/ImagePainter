package de.beatbrot.imagepainter

import android.app.Application
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.beatbrot.imagepainter.util.lift
import de.beatbrot.imagepainter.util.moveTo
import de.beatbrot.imagepainter.util.startAt
import de.beatbrot.imagepainter.view.ImagePainterView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawStackTest {
    private lateinit var imagePainter: ImagePainterView

    @Before
    fun loadView() {
        val context = getApplicationContext<Application>()
        imagePainter = ImagePainterView(context)
        imagePainter.setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565))

    }

    @Test
    fun testEmptyDrawStack() {
        val stack = imagePainter.drawStack
        assert(stack.undoStack.isEmpty())
        assert(stack.undoStack.isEmpty())
    }

    @Test
    fun testStackAfterDrawing() {
        draw(imagePainter)
        val stack = imagePainter.drawStack
        draw(imagePainter)
        imagePainter.undo()
        val stack2 = imagePainter.drawStack
        imagePainter.undo()
        val stack3 = imagePainter.drawStack

        assert(stack.undoStack.isNotEmpty())
        assert(stack.redoStack.isEmpty())

        assert(stack2.undoStack.isNotEmpty())
        assert(stack2.redoStack.isNotEmpty())

        assert(stack3.undoStack.isEmpty())
        assert(stack3.redoStack.isNotEmpty())
    }

    @Test
    fun testEqualsAndHashCode() {
        val firstStack = imagePainter.drawStack
        val firstFirstStack = imagePainter.drawStack

        assert(firstStack == firstFirstStack)
        assert(firstStack.hashCode() == firstFirstStack.hashCode())

        draw(imagePainter)
        val secondStack = imagePainter.drawStack

        assert(firstStack != secondStack)
        assert(firstStack.hashCode() != secondStack.hashCode())
    }

    private fun draw(painter: ImagePainterView) {
        painter.apply {
            startAt(10, 20)
            moveTo(20, 50)
            lift()
        }
    }
}
