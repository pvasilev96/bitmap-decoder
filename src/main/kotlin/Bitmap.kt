import java.awt.Color

data class Bitmap(
        val fileHeader: FileHeader,
        val imageHeader: ImageHeader,
        val colorTable: List<Color>,
        val pixelData: List<Color>
)