package part2_serialization

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/*
  Kryo:
  - blazing fast
  - memory-efficient
  - highly configurable
  - can serialize almost anything out of the box
  - poorly maintained since Akka 2.4
 */

case class Book(title: String, year: Int)

object KryoSerialization_Local extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin)
    .withFallback(ConfigFactory.load("kryoSerialization.conf"))

  val system = ActorSystem("LocalSystem", config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

  actorSelection ! Book("The Rock the JVM Experience", 2019)
}

object KryoSerialization_Remote extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin)
    .withFallback(ConfigFactory.load("kryoSerialization.conf"))

  val system = ActorSystem("RemoteSystem", config)
  val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
}

object KryoSerialization_Persistence extends App {
  val config = ConfigFactory.load("persistentStores.conf").getConfig("postgresStore")
    .withFallback(ConfigFactory.load("kryoSerialization.conf"))

  val system = ActorSystem("PersistenceSystem", config)
  val simplePersistentActor = system.actorOf(SimplePersistentActor.props("kryo-actor"), "kryoBookActor")

//  simplePersistentActor ! Book("The Rock the JVM Experience", 2019)
}