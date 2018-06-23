import javax.swing.JFrame

class MainFrame(filePath: String) : JFrame() {
    init {
        val resource = javaClass.classLoader.getResource(filePath)
        val bitmap = decode(resource.file)
        val bufferedImage = bitmap.asBufferedImage()
        setSize(bufferedImage.width, bufferedImage.height)
        isVisible = true
        graphics.drawImage(bufferedImage, 0, 0, null)
    }
}

fun main(args: Array<String>) {
    MainFrame("sample.bmp")
}
