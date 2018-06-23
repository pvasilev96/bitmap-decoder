import okio.Okio
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File


fun decode(filePath: String): Bitmap {
    val source = Okio.buffer(Okio.source(File(filePath)))
    val signature = source.readUtf8(2)
    val size = source.readIntLe()
    val reserved = source.readIntLe()
    val dataOffset = source.readIntLe()
    val fileHeader = FileHeader(signature, size, reserved, dataOffset)
    val headerSize = source.readIntLe()
    val width = source.readIntLe()
    val height = source.readIntLe()
    val planes = source.readShortLe()
    val bitsPerPixel = source.readShortLe()
    val compression = source.readIntLe()
    val imageSize = source.readIntLe()
    val xPixelsPerMeter = source.readIntLe()
    val yPixelsPerMeter = source.readIntLe()
    val numUsedColors = source.readIntLe()
    val numImportantColors = source.readIntLe()
    val imageHeader = ImageHeader(headerSize, width, height, planes, bitsPerPixel, compression,
            imageSize, xPixelsPerMeter, yPixelsPerMeter, numUsedColors, numImportantColors)
    val colorTable = (0 until numUsedColors).map {
        val b = source.readByteString(1).hex().toInt(16)
        val g = source.readByteString(1).hex().toInt(16)
        val r = source.readByteString(1).hex().toInt(16)
        source.skip(1)
        Color(r, g, b)
    }
    val pixelData = (0 until imageSize).map {
        val colorIndex = source.readByteString(1).hex().toInt(16)
        colorTable[colorIndex]
    }
    return Bitmap(fileHeader, imageHeader, colorTable, pixelData)
}

fun Bitmap.asBufferedImage(): BufferedImage {
    val (_, header, _, pixelData) = this
    val (_, width, height) = header
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for (y in (0 until height)) {
        for (x in (0 until width)) {
            val color = pixelData[y * width + x].rgb
            bufferedImage.setRGB(x, y, color)
        }
    }
    val transform = AffineTransform.getRotateInstance(Math.toRadians(180.0), width / 2.0, height / 2.0)
    val op = AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR)
    return op.filter(bufferedImage, null)
}