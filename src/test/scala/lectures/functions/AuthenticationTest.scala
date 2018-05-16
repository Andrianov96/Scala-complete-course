package lectures.functions

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}
import lectures.functions.AuthenticationData._

/**
  * Авторизация - это очень важно, поэтому нам необходимо покрыть тестами ответсвенный за нее код
  * (lectures.functions.Authentication)
  *
  * Для этого
  * * * * уберите extends App у Authentication
  * * * * замените AuthenticationData.testUsers соответствующими генераторами
  * * * * напишите
  * * * * * 2 теста на authByCard
  * * * * * 2 теста на authByLP
  * * * * * 1 тест на их композицию
  *
  */
class AuthenticationTest extends WordSpec with Matchers with PropertyChecks {
  val cardCredentialsGen = Gen.choose(0, 10000).map(i => CardCredentials(i))
  val cardUserGen = Gen.zip(Gen.choose(0, 10000), cardCredentialsGen).map(a => CardUser(a._1, a._2))

  "authByCard" should {
    "accept all registered credentials" in {
      val testCredentials = (1 to 1000).flatMap(_ => cardCredentialsGen.sample).toSet
      val existingCredentials = testCredentials.intersect(registeredCards)
      val testCardUsers = testCredentials.map(card => CardUser(0, card))
      val goodUsers = existingCredentials.map(card => Some(CardUser(0, card)))
      testCardUsers.map(Authentication.authByCard.lift).filter(_.nonEmpty) shouldBe goodUsers
    }
  }

  "authByCard" should {
    "accept user only if he has good credentials" in {
      forAll(cardUserGen) { cardUser =>
        Authentication.authByCard.lift(cardUser).nonEmpty shouldBe registeredCards.contains(cardUser.credentials)
      }
    }
  }

  val LPCredentialsGen = Gen.zip(Gen.alphaStr, Gen.alphaStr).map(s => LPCredentials(s._1, s._2))
  val LPUserGen = Gen.zip(Gen.choose(0, 10000), LPCredentialsGen).map(a => LPUser(a._1, a._2))

  "authByLP" should {
    "accept all registered LPs" in {
      val testLPCredentials = (1 to 1000).flatMap(_ => LPCredentialsGen.sample).toSet
      val existingLP = testLPCredentials.intersect(registeredLoginAndPassword)
      val testLPUsers = testLPCredentials.map(credenrials => LPUser(0, credenrials))
      val goodUsers = existingLP.map(credenrials => LPUser(0, credenrials))
      testLPUsers.map(Authentication.authByLP.lift).filter(_.nonEmpty) shouldBe goodUsers
    }
  }

  "authByLP" should {
    "accept user with right LP" in {
      forAll(LPUserGen) { user =>
        Authentication.authByLP.lift(user).nonEmpty shouldBe registeredLoginAndPassword.contains(user.credentials)
      }
    }
  }

  "authByLP and authByCard" should {
    "work good" in {
      val allGen = Gen.frequency(
        (1, cardUserGen),
        (1, LPUserGen),
        (1, Gen.const(AnonymousUser()))
      )

      val testUsers = (1 to 1000).flatMap(_ => allGen.sample).toSet
      val existingUsers = testUsers.collect{
        case user@CardUser(_, credentials) if registeredCards.contains(credentials) => user
        case user@LPUser(_, lPCredentials) if registeredLoginAndPassword.contains(lPCredentials) => user
      }

      testUsers.map(Authentication.authByLP.lift).filter(_.nonEmpty) shouldBe existingUsers
    }
  }

}
