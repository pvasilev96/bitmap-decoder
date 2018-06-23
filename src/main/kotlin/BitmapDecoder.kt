import okio.Okio
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File


fun decode(filePath: String): Bitmap {
    val source = Okio.buffer(Okio.source(File(filePath)))
    return source.use {
        val signature = readUtf8(2)
        val size = readIntLe()
        val reserved = readIntLe()
        val dataOffset = readIntLe()
        val fileHeader = FileHeader(signature, size, reserved, dataOffset)
        val headerSize = readIntLe()
        val width = readIntLe()
        val height = readIntLe()
        val planes = readShortLe()
        val bitsPerPixel = readShortLe()
        val compression = readIntLe()
        val imageSize = readIntLe()
        val xPixelsPerMeter = readIntLe()
        val yPixelsPerMeter = readIntLe()
        val numUsedColors = readIntLe()
        val numImportantColors = readIntLe()
        val imageHeader = ImageHeader(headerSize, width, height, planes, bitsPerPixel, compression,
                imageSize, xPixelsPerMeter, yPixelsPerMeter, numUsedColors, numImportantColors)
        val colorTable = (0 until numUsedColors).map {
            val b = readByteString(1).hex().toInt(16)
            val g = readByteString(1).hex().toInt(16)
            val r = readByteString(1).hex().toInt(16)
            skip(1)
            Color(r, g, b)
        }
        val pixelData = (0 until imageSize).map {
            val colorIndex = readByteString(1).hex().toInt(16)
            colorTable[colorIndex]
        }
        Bitmap(fileHeader, imageHeader, colorTable, pixelData)
    }
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

fun <T : AutoCloseable, R> T.use(block: T.() -> R): R {
    val result = block()
    close()
    return result
}