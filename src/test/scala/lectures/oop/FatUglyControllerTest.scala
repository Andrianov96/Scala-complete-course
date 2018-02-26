package lectures.oop

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class FatUglyControllerTest extends FlatSpec with Matchers {

  behavior of "FatUglyController"

  it should "successfully process single file" in {
    val requestBody =
      """DELIMITER
        |file1.txt
        |This is body of file1
      """.stripMargin
    val (status, body) = controller.processRoute("/api/v1/uploadFile", Some(requestBody.getBytes))

    status shouldBe 200
    body shouldBe "Response:\n- saved file file1.txt to 063f83f94e59aac2edd719fab1d179f86084887a.txt (file size: 21)"
        .stripMargin
  }

  it should "successfully process two files" in {
    val requestBody =
      """DELIMITER22
        |file1.txt
        |This is body of file1
        |DELIMITER22
        |file2.txt
        |This is body of file2!!
      """.stripMargin
    val (status, body) = controller.processRoute("/api/v1/uploadFile", Some(requestBody.getBytes))

    status shouldBe 200
    body shouldBe "Response:\n- saved file file1.txt to 063f83f94e59aac2edd719fab1d179f86084887a.txt (file size: 21)\n- saved file file2.txt to 7387fa41a69d93b59b67bd46ab18a72c81edb767.txt (file size: 23)"
        .stripMargin
  }

  it should "return 404 for unknown route" in {
    val (status, body) = controller.processRoute("/api", None)

    status shouldBe 404
    body shouldBe "Route not found"
  }

  it should "return 400 for empty body" in {
    val (status, body) = controller.processRoute("/api/v1/uploadFile", None)

    status shouldBe 400
    body shouldBe "Can not upload empty file"
  }

  it should "return 400 for forbidden extension" in {
    val requestBody =
      """DELIMITER
        |file1.exe
        |This is body of file1
      """.stripMargin
    val (status, body) = controller.processRoute("/api/v1/uploadFile", Some(requestBody.getBytes))

    status shouldBe 400
    body shouldBe "Request contains forbidden extension"
  }

  it should "return 400 for file greater than 8 MB" in {
    var testFile = "a" * (1024 * 10250)
    val requestBody = "DELIMITER\r\nfile1.txt\r\n" + testFile
    val (status, body) = controller.processRoute("/api/v1/uploadFile", Some(requestBody.getBytes))

    status shouldBe 400
    body shouldBe s"File size should not be more than ${controller.MaxFileByteSize / (1024 *1024)} MB"
  }

  "writeToDB" should "write to DB" in {
    val id = Random.nextString(10)
    val fileName = Random.nextString(10)
    controller.writeFileToDB(id, fileName) shouldBe s"Result of insert into files (id, name, created_on) values ('$id', '$fileName', current_timestamp)"
  }

  "sendMsgToIbmMq" should "send message to IbmMq" in {
    val fileName = Random.nextString(10)
    controller.sendMsgToIbmMq(fileName) shouldBe s"""Message sending result for <Event name="FileUpload"><Origin>SCALA_FTK_TASK</Origin><FileName>${fileName}</FileName></Event>"""
  }

  "send" should "send email" in {
    val fileName = Random.nextString(10)
    controller.emailToAdminAboutNewFile(fileName) shouldBe s"""Send email to admin@admin.tinkoff.ru: Theme "File has been uploaded" body "Hey, we have got new file: $fileName""""
  }

  private val controller = new FatUglyController()


}