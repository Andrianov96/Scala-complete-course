package lectures.oop

import java.security.MessageDigest


/**
  * Данный класс содержит код, наспех написанный одним джуниор-разработчиком,
  * который плохо слушал лекции по эффективному программированию.
  *
  * Вам необходимо:
  * - отрефакторить данный класс, выделив уровни ответственности, необходимые
  * интерфейсы и абстракции
  * - дописать тесты в FatUglyControllerTest и реализовать в них проверку на
  * сохранение в БД, отправку сообщения в очередь и отправку email-а
  * - исправить очевидные костыли в коде
  *
  * Код внутри методов, помеченный как DO NOT TOUCH, трогать нельзя (сами методы
  * при этом можно выносить куда и как угодно)
  *
  * Интерфейс метода processRoute менять можно и нужно!
  * Передаваемые данные при этом должны оставаться неизменными.
  *
  * Удачи!
  */


class FatUglyController(val MaxFileByteSize: Int = 8388608) {

  def processRoute(route: String, requestBody: Option[Array[Byte]]): (Int, String) =
    route match {
      case ("/api/v1/uploadFile") => goodWayHandler(requestBody)
      case _ => badWayHandler()
    }

  case class File(name: String, body: String, extension: String)

  case class UploadFileResponse(code: Int, msg: String)

  def badWayHandler(): (Int, String) = (404, "Route not found")

  def goodWayHandler(requestBody: Option[Array[Byte]]): (Int, String) =
    requestBody match {
      case None => emptyFileHandler()
      case Some(body) if body.length > MaxFileByteSize => bigFileHandler()
      case _ => remainigHandler(requestBody)
    }

  def emptyFileHandler(): (Int, String) = (400, "Can not upload empty file")

  def bigFileHandler(): (Int, String) = (400, s"File size should not be more than ${MaxFileByteSize / (1024 * 1024)} MB")

  def remainigHandler(requestBody: Option[Array[Byte]]): (Int, String) = {
    val files = getFiles(requestBody)
    if (hasBadExtention(files))
      (400, "Request contains forbidden extension")
    else {
      val responseBuf = new StringBuilder()
      initializeLocalMailer()
      files.foreach { file =>
        val id = hash(file)
        // Emulate file saving to disk
        responseBuf.append(s"- saved file ${file.name} to $id.${file.extension} (file size: ${file.body.length})\n")

        writeFileToDB(id, file.name)
        sendMsgToIbmMq(file.name)
        emailToAdminAboutNewFile(file.name)
      }
      (200, "Response:\n" + responseBuf.dropRight(1))
    }
  }

  def connectToPostgresDatabase(): Int = {
    // DO NOT TOUCH
    println("Connected to PostgerSQL database")
    42 // pretty unique connection id
  }

  def executePostgresQuery(connectionId: Int, sql: String): String = {
    // DO NOT TOUCH
    println(s"Executed SQL statement on connection $connectionId: $sql")
    s"Result of $sql"
  }

  def connectToIbmMq(): Int = {
    // DO NOT TOUCH
    println("Connected to IBM WebSphere super-duper MQ Manager")
    13 // chosen by fair dice roll
  }

  def writeFileToDB(id: String, fileName: String): String = {
    val connectionId = connectToPostgresDatabase()
    executePostgresQuery(connectionId, s"insert into files (id, name, created_on) values ('$id', '$fileName', current_timestamp)")
  }


  def sendMsgToIbmMq(fileName: String): String = {
    val connectionId = connectToIbmMq()
    sendMessageToIbmMq(connectionId, s"""<Event name="FileUpload"><Origin>SCALA_FTK_TASK</Origin><FileName>${fileName}</FileName></Event>""")
  }


  def sendMessageToIbmMq(connectionId: Int, message: String): String = {
    // DO NOT TOUCH
    println(s"Sent MQ message via $connectionId: $message")
    s"Message sending result for $message"
  }

  def initializeLocalMailer(): Unit = {
    // DO NOT TOUCH
    println("Initialized local mailer")
  }

  def emailToAdminAboutNewFile(fileName: String): String = {
    send("admin@admin.tinkoff.ru", "File has been uploaded", s"Hey, we have got new file: $fileName")
    s"""Send email to admin@admin.tinkoff.ru: Theme "File has been uploaded" body "Hey, we have got new file: $fileName""""
  }

  def send(email: String, subject: String, body: String): Unit = {
    // DO NOT TOUCH
    println(s"Sent email to $email with subject '$subject'")
  }

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