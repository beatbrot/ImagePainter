package de.beatbrot.imagepainter.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bitmap = drawableToBitmap(baseContext.getDrawable(R.drawable.ic_android_black_24dp)!!)
        imagePainter.image = bitmap

        initButtons()

        imagePainter.setRedoStatusChangeListener { status -> redoButton.isEnabled = status }
        imagePainter.setUndoStatusChangeListener { status -> undoButton.isEnabled = status }
    }

    private fun initButtons() {
        undoButton.setOnClickListener {
            imagePainter.undo()
        }

        redoButton.setOnClickListener {
            imagePainter.redo()
        }

        initColorButton(blackButton, Color.BLACK)
        initColorButton(redButton, Color.RED)
        initColorButton(blueButton, Color.BLUE)
        resetButton.setOnClickListener { imagePainter.reset() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                imagePainter.strokeWidth = progress.toFloat()
            }
        })

    }

    private fun initColorButton(view: View, @ColorInt color: Int) {
        view.background = ShapeDrawable(OvalShape()).apply {
            intrinsicHeight = 20
            intrinsicWidth = 20
            bounds = Rect(0, 0, 20, 20)
            paint.color = color
        }
        view.setOnClickListener { imagePainter.strokeColor = color }
        view.visibility = View.VISIBLE
    }


    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}
