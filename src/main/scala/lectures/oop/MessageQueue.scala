package lectures.oop

class MessageQueue{

  def connectToIbmMq(): Int = {
    // DO NOT TOUCH
    println("Connected to IBM WebSphere super-duper MQ Manager")
    13 // chosen by fair dice roll
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
}
