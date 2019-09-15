package de.beatbrot.imagepainter

import java.util.*

class DrawStack internal constructor(
    internal val undoStack: Deque<DrawPath>,
    internal val redoStack: Deque<DrawPath>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrawStack

        if (undoStack != other.undoStack) return false
        if (redoStack != other.redoStack) return false

        return true
    }

    override fun hashCode(): Int {
        var result = undoStack.hashCode()
        result = 31 * result + redoStack.hashCode()
        return result
    }
}
