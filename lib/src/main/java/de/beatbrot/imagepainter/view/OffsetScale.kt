package de.beatbrot.imagepainter.view

internal sealed class OffsetScale(val scale: Float, val offset: Float)

internal class XOffsetScale(scale: Float, offset: Float) : OffsetScale(scale, offset)
internal class YOffsetScale(scale: Float, offset: Float) : OffsetScale(scale, offset)
internal open class NoOffsetScale(scale: Float) : OffsetScale(scale, 0F)
internal object NoScale : NoOffsetScale(1F)
