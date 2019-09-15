package de.beatbrot.imagepainter.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.beatbrot.imagepainter.DrawStack

class MainActivityViewModel : ViewModel() {
    val drawStack = MutableLiveData<DrawStack>()
}