package lectures.oop

import java.security.MessageDigest

object Utils{
  case class File(name: String, body: String, extension: String)

  def hash(file: File): String = {
    val s = file.name + '\n' + file.body
    MessageDigest.getInstance("SHA-1").digest(s.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

  def badExtentionCheck(file: File): Boolean = {
    Seq("exe", "bat", "com", "sh").contains(file.extension)
  }

  def hasBadExtention(files: Array[File]): Boolean = {
    files.foldLeft(false)((acc, file) => acc || badExtentionCheck(file))
  }

  def getFileFromString(file: String): File = {
    val name = file.trim.takeWhile(_ != '\n')
    val body = file.trim.dropWhile(_ != '\n').drop(1)
    val extension = file.trim.takeWhile(_ != '\n').dropWhile(_ != '.').drop(1)
    File(name, body, extension)
  }

  def getFiles(requestBody: Option[Array[Byte]]): Array[File] = {
    val stringBody = new String(requestBody.get.filter(_ != '\r'))
    val delimiter = stringBody.takeWhile(_ != '\n')
    stringBody.split(delimiter).drop(1).map(getFileFromString)
  }
}
