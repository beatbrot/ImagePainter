package de.beatbrot.imagepainter

import android.app.Application
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.beatbrot.imagepainter.util.lift
import de.beatbrot.imagepainter.util.moveTo
import de.beatbrot.imagepainter.util.startAt
import de.beatbrot.imagepainter.view.ImagePainterView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RedoStatusChangeListenerTest {
    private lateinit var imagePainter: ImagePainterView

    @Before
    fun loadView() {
        val context = getApplicationContext<Application>()
        imagePainter = ImagePainterView(context)
        imagePainter.setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565))
    }

    @Test
    fun testListenerGetsTriggered() {
        val testListener = TestListener()
        imagePainter.setRedoStatusChangeListener(testListener)
        assertEquals(false, testListener.canRedo)

        imagePainter.apply {
            startAt(10, 10)
            lift()
        }

        imagePainter.undo()
        assertEquals(true, testListener.canRedo)

        imagePainter.apply {
            startAt(10, 10)
        }

        assertEquals(false, testListener.canRedo)
        imagePainter.reset()
        assertEquals(false, testListener.canRedo)
    }

    @Test
    fun testNoRedoChangeListenerSet() {
        imagePainter.apply {
            startAt(25, 25)
            moveTo(50, 50)
            lift()
        }

        imagePainter.undo()
        imagePainter.redo()
    }

    class TestListener : RedoStatusChangeListener {
        var canRedo: Boolean = false
            private set

        override fun redoStatusChanged(canRedo: Boolean) {
            this.canRedo = canRedo
        }
    }
}
