package de.beatbrot.imagepainter

@FunctionalInterface
interface RedoStatusChangeListener {
    fun redoStatusChanged(canRedo: Boolean)
}