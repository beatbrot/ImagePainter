package de.beatbrot.imagepainter

@FunctionalInterface
interface UndoStatusChangeListener {
    fun undoStatusChanged(canUndo: Boolean)
}