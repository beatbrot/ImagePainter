package de.beatbrot.imagepainter.view

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Build.VERSION_CODES.P
import de.beatbrot.imagepainter.util.lift
import de.beatbrot.imagepainter.util.moveTo
import de.beatbrot.imagepainter.util.startAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [JELLY_BEAN, P])
class ImagePainterViewTest {

    private lateinit var imagePainter: ImagePainterView

    @Before
    fun loadView() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        imagePainter = ImagePainterView(activity)
    }

    @Test
    fun testInstantiate() {
        assertEquals(Color.BLACK, imagePainter.strokeColor)
        assertEquals(5F, imagePainter.strokeWidth)
    }

    @Test
    fun testAccessors() {
        imagePainter.strokeColor = Color.BLUE
        imagePainter.strokeWidth = 100F
        imagePainter.strokeCap = Paint.Cap.BUTT

        assertEquals(Color.BLUE, imagePainter.strokeColor)
        assertEquals(100F, imagePainter.strokeWidth)
        assertEquals(Paint.Cap.BUTT, imagePainter.strokeCap)
    }

    @Test
    fun testDrawing() {
        imagePainter.apply {
            image = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

            startAt(10, 20)
            moveTo(20, 50)
            lift()
        }

        assertEquals(1, imagePainter.undoStack.size)
        assertEquals(imagePainter.undoStack.first, imagePainter.undoStack.last)

        val path = imagePainter.undoStack.last
        assertEquals(Color.BLACK, path.paint.color)
        assertEquals(false, path.path.isEmpty)
    }

    @Test
    fun testUndo() {
        imagePainter.image = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

        imagePainter.apply {
            startAt(10, 20)
            moveTo(20, 50)
            lift()

            startAt(30, 30)
            moveTo(40, 40)
            lift()
        }

        assertEquals(2, imagePainter.undoStack.size)
        assertNotEquals(imagePainter.undoStack.first, imagePainter.undoStack.last)

        val first = imagePainter.undoStack.first

        imagePainter.undo()

        assertEquals(1, imagePainter.undoStack.size)
        assertEquals(first, imagePainter.undoStack.first)

        imagePainter.undo()
        assertEquals(0, imagePainter.undoStack.size)
    }

    @Test
    fun testRedo() {
        imagePainter.image = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

        imagePainter.apply {
            startAt(10, 20)
            moveTo(20, 50)
            lift()

            startAt(30, 30)
            moveTo(40, 40)
            lift()
        }

        assertEquals(2, imagePainter.undoStack.size)
        assertNotEquals(imagePainter.undoStack.first, imagePainter.undoStack.last)

        val first = imagePainter.undoStack.first
        val last = imagePainter.undoStack.last

        imagePainter.undo()
        assertEquals(1, imagePainter.undoStack.size)
        assertEquals(first, imagePainter.undoStack.first)

        imagePainter.redo()
        assertEquals(2, imagePainter.undoStack.size)
        assertEquals(first, imagePainter.undoStack.first)
        assertEquals(last, imagePainter.undoStack.last)
    }
}
