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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}
