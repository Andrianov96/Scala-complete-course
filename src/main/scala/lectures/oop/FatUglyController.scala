package lectures.oop

import Utils._


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

class FatUglyController(val MaxFileByteSize: Int = 8388608, db: DataBase, emailService: EmailService, messageQueue: MessageQueue) {

  def processRoute(route: String, requestBody: Option[Array[Byte]]): (Int, String) =
    route match {
      case ("/api/v1/uploadFile") => goodWayHandler(requestBody)
      case _ => badWayHandler()
    }

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
     emailService.initializeLocalMailer()
      files.foreach { file =>
        val id = hash(file)
        // Emulate file saving to disk
        responseBuf.append(s"- saved file ${file.name} to $id.${file.extension} (file size: ${file.body.length})\n")

        db.writeFileToDB(id, file.name)
        messageQueue.sendMsgToIbmMq(file.name)
        emailService.emailToAdminAboutNewFile(file.name)
      }
      (200, "Response:\n" + responseBuf.dropRight(1))
    }
  }

}