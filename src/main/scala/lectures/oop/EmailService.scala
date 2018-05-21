package lectures.oop

class EmailService{
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
}
