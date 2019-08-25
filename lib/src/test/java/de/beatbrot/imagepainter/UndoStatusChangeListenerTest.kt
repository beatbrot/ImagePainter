package de.beatbrot.imagepainter

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Build.VERSION_CODES.P
import de.beatbrot.imagepainter.util.lift
import de.beatbrot.imagepainter.util.moveTo
import de.beatbrot.imagepainter.util.startAt
import de.beatbrot.imagepainter.view.ImagePainterView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [JELLY_BEAN, P])
class UndoStatusChangeListenerTest {
    private lateinit var imagePainter: ImagePainterView

    @Before
    fun loadView() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        imagePainter = ImagePainterView(activity)
        imagePainter.image = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
    }

    @Test
    fun testListenerGetsTriggered() {
        val testListener = TestListener()
        imagePainter.setUndoStatusChangeListener(testListener)
        assertEquals(false, testListener.canUndo)

        imagePainter.apply {
            startAt(10, 10)
            lift()
        }

        assertEquals(true, testListener.canUndo)
        imagePainter.undo()
        assertEquals(false, testListener.canUndo)

        imagePainter.apply {
            startAt(10, 10)
            lift()
        }
        assertEquals(true, testListener.canUndo)
        imagePainter.reset()
        assertEquals(false, testListener.canUndo)
    }

    @Test
    fun testNoUndoChangeListenerSet() {
        imagePainter.apply {
            startAt(25, 25)
            moveTo(50, 50)
            lift()
        }

        imagePainter.undo()
    }

    class TestListener : UndoStatusChangeListener {
        var canUndo: Boolean = false
            private set

        override fun undoStatusChanged(canUndo: Boolean) {
            this.canUndo = canUndo
        }
    }
}