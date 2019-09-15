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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.beatbrot.imagepainter.sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var v: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        setContentView(v.root)
        initButtons()

        viewModel.drawStack.observe(this, Observer { newValue ->
            if (newValue != null) {
                v.imagePainter.drawStack = newValue
            }
        })
        v.imagePainter.setRedoStatusChangeListener { status -> v.redoButton.isEnabled = status }
        v.imagePainter.setUndoStatusChangeListener { status -> v.undoButton.isEnabled = status }
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            viewModel.drawStack.value = v.imagePainter.drawStack
        }
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
        v.resetButton.setOnClickListener { v.imagePainter.reset() }

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
