package lectures.oop

import java.security.MessageDigest


/**
  * Данный класс содержит код, наспех написанный одним джуниор-разработчиком,
  * который плохо слушал лекции по эффективному программированию.
  *
  * Вам необходимо:
  * - отрефакторить данный класс, выделив уровни ответственности, необходимые
  *   интерфейсы и абстракции
  * - дописать тесты в FatUglyControllerTest и реализовать в них проверку на
  *   сохранение в БД, отправку сообщения в очередь и отправку email-а
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



case class File(name: String, body: String, extension: String){
  def this(file: String) = this(file.trim.takeWhile(_ != '\n'), file.trim.dropWhile( _ != '\n').drop(1), file.trim.takeWhile(_ != '\n').dropWhile(_ != '.').drop(1))
}

class FatUglyController {

  val MaxFileSize = 8388608

  def processRoute(route: String, requestBody: Option[Array[Byte]]): (Int, String) =
    (route, requestBody) match {
      case (rout, _) if rout != "/api/v1/uploadFile" => (404, "Route not found")
      case (_, reqBody) if reqBody.isEmpty => (400, "Can not upload empty file")
      case (_, reqBody) if reqBody.get.length > MaxFileSize => (400, s"File size should not be more than ${MaxFileSize / (1024 *1024)} MB")
      case _ => {
        val responseBuf = new StringBuilder()
        val databaseConnectionId = connectToPostgresDatabase()
        val mqConnectionId = connectToIbmMq()
        initializeLocalMailer()
        val files = getFiles(requestBody)
        if (hasBadExtention(files))
          (400, "Request contains forbidden extension")
        else {
          files.foreach { file =>
            val id = hash(file)
            // Emulate file saving to disk
            responseBuf.append(s"- saved file ${file.name} to " + id + "." + file.extension + s" (file size: ${file.body.length})" + "\n")

            executePostgresQuery(databaseConnectionId, s"insert into files (id, name, created_on) values ('$id', '${file.name}', current_timestamp)")
            sendMessageToIbmMq(mqConnectionId, s"""<Event name="FileUpload"><Origin>SCALA_FTK_TASK</Origin><FileName>${file.name}</FileName></Event>""")
            send("admin@admin.tinkoff.ru", "File has been uploaded", s"Hey, we have got new file: ${file.name}")
          }

          (200, "Response:\r\n" + responseBuf.dropRight(1))
        }
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

  def sendMessageToIbmMq(connectionId: Int, message: String): String = {
    // DO NOT TOUCH
    println(s"Sent MQ message via $connectionId: $message")
    s"Message sending result for $message"
  }

  def initializeLocalMailer(): Unit = {
    // DO NOT TOUCH
    println("Initialized local mailer")
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

  def getFiles(requestBody: Option[Array[Byte]]): Array[File] = {
    val stringBody = new String(requestBody.get.filter(_ != '\r'))
    val delimiter = stringBody.takeWhile(_ != '\n')
    stringBody.split(delimiter).drop(1).map(new File(_))
  }
}