package lectures.di.macwire

import java.sql.{Connection, DriverManager}

import lectures.functions.{LPUser, User}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.collection.mutable
import lectures.di.UserServiceTestSuite._

class MacWireProgramTestDependencies() extends MacWireProgramDependency() {
  override val configurationMap = mutable.Map.empty
}

class MacWireProgramTest extends WordSpec with BeforeAndAfterAll with Matchers {

  Class.forName("org.sqlite.JDBC")
  val deps = new MacWireProgramTestDependencies()

  //connection.setAutoCommit(false)

  override def afterAll(): Unit = {
  }

  "Reader program" should {
    "should not find any user" in {
      val deps = new MacWireProgramTestDependencies()
      val r = new MacWireProgram(deps)
      val res: Option[User] = r.run
      res shouldBe None
    }

    "find correct user" in {
      val deps = new MacWireProgramDependency()
      val r = new MacWireProgram(deps)
      val Some(user: LPUser) = r.run
      user.credentials.login shouldBe "Frosya"
      user.id shouldBe 22
    }
  }
}