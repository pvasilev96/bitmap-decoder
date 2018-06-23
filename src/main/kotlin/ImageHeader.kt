data class ImageHeader(
        val headerSize: Int,
        val width: Int,
        val height: Int,
        val planes: Short,
        val bitsPerPixel: Short,
        val compression: Int,
        val imageSize: Int,
        val xPixelsPerMeter: Int,
        val yPixelsPerMeter: Int,
        val numUsedColors: Int,
        val numImportantColors: Int
)