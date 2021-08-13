package core.Services

class FirebaseFilePathBuilder {

    companion object {
        val instance = FirebaseFilePathBuilder()
        var path = ""
        fun init(basePath: String): FirebaseFilePathBuilder {
            path = basePath;
            return instance
        }

        fun sub(directory: String): FirebaseFilePathBuilder {
            path += "/$directory"
            return instance
        }

        fun build(fileName:String): String {
            path += "/$fileName"
            return path
        }
    }
}