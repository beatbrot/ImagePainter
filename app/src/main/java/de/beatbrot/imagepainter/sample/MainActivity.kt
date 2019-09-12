package de.beatbrot.imagepainter.sample

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import de.beatbrot.imagepainter.sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var v: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)
        initButtons()

        v.imagePainter.setRedoStatusChangeListener { status -> v.redoButton.isEnabled = status }
        v.imagePainter.setUndoStatusChangeListener { status -> v.undoButton.isEnabled = status }
    }

    private fun initButtons() {
        v.undoButton.setOnClickListener {
            v.imagePainter.undo()
        }

        v.redoButton.setOnClickListener {
            v.imagePainter.redo()
        }

        initColorButton(v.blackButton, Color.BLACK)
        initColorButton(v.redButton, Color.RED)
        initColorButton(v.blueButton, Color.BLUE)
        v.resetButton.setOnClickListener { v.imagePainter.setImageBitmap(v.imagePainter.exportImage()) }

        v.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                v.imagePainter.strokeWidth = progress.toFloat()
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
        view.setOnClickListener { v.imagePainter.strokeColor = color }
        view.visibility = View.VISIBLE
    }
}
