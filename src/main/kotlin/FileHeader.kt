data class FileHeader(
        val signature: String,
        val size: Int,
        val reserved: Int,
        val dataOffset: Int
)