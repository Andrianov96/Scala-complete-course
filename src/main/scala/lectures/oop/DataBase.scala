package lectures.oop

class DataBase {

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

  def writeFileToDB(id: String, fileName: String): String = {
    val connectionId = connectToPostgresDatabase()
    executePostgresQuery(connectionId, s"insert into files (id, name, created_on) values ('$id', '$fileName', current_timestamp)")
  }
}
