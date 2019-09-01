package de.beatbrot.imagepainter.view

internal sealed class OffsetScale {
    abstract val scale: Float
    abstract val offset: Float

    val scaledOffset: Float
        get() = scale * offset
}

internal open class NoOffsetScale(override val scale: Float) : OffsetScale() {

    override val offset = 0F
}

internal object NoScale : NoOffsetScale(1F)

internal class XOffsetScale(override val scale: Float, override val offset: Float) : OffsetScale()
internal class YOffsetScale(override val scale: Float, override val offset: Float) : OffsetScale()