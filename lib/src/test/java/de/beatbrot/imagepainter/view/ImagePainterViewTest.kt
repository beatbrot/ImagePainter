package de.beatbrot.imagepainter.view

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.beatbrot.imagepainter.util.lift
import de.beatbrot.imagepainter.util.moveTo
import de.beatbrot.imagepainter.util.startAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImagePainterViewTest {

    private lateinit var imagePainter: ImagePainterView

    @Before
    fun loadView() {
        val context = getApplicationContext<Application>()
        imagePainter = ImagePainterView(context)
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
            setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565))

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
        imagePainter.setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565))

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
        imagePainter.setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565))

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

    @Test
    fun testExportImage() {
        val bm = Bitmap.createBitmap(100, 130, Bitmap.Config.RGB_565)

        imagePainter.apply {
            setImageBitmap(bm)

            startAt(25, 25)
            moveTo(50, 50)
            lift()
        }

        val result = imagePainter.exportImage(Bitmap.Config.RGB_565)

        assertEquals(bm.width, result.width)
        assertEquals(bm.height, result.height)
    }
}
