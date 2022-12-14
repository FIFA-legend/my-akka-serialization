package part2_serialization

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import part2_serialization.Datamodel.OnlineStoreUser

/*
  Protocol Buffers (Protobuf)
  - very fast
  - very memory-efficient
  - "schema-based" via proto files (IDL)
  - best for schema evolution
  - maintained by Google, used by Google
  - already implemented Akka Protobuf serializer
  - needs dedicated code generator
  - generates Java files only
 */

/*
  How to Protobuf:
  - install protoc
  - write a proto file with your data structures
  - generate the Java file(s)
  - set the protobuf serializer in the configuration

  Use protobuf:
  - refer to the generated Java type
  - use the builder pattern to instantiate objects
  - safely send them across the wire
 */

object ProtobufSerialization_Local extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin)
    .withFallback(ConfigFactory.load("protobufSerialization.conf"))

  val system = ActorSystem("LocalSystem", config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

  val onlineStoreUser: OnlineStoreUser = OnlineStoreUser.newBuilder()
    .setId(45621)
    .setUserName("daniel-rockthejvm")
    .setUserEmail("daniel@rockthejvm.com")
    .build()

  actorSelection ! onlineStoreUser
}

object ProtobufSerialization_Remote extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin)
    .withFallback(ConfigFactory.load("protobufSerialization.conf"))

  val system = ActorSystem("RemoteSystem", config)
  val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
}

object ProtobufSerialization_Persistence extends App {
  val config = ConfigFactory.load("persistentStores.conf").getConfig("postgresStore")
    .withFallback(ConfigFactory.load("protobufSerialization.conf"))

  val system = ActorSystem("PersistenceSystem", config)
  val simplePersistentActor = system.actorOf(SimplePersistentActor.props("protobuf-actor"), "protobufActor")

  val onlineStoreUser: OnlineStoreUser = OnlineStoreUser.newBuilder()
    .setId(47281)
    .setUserName("martin-rockthejvm")
    .setUserEmail("martin@rockthejvm.com")
    .setUserPhone("84293572123")
    .build()

//  simplePersistentActor ! onlineStoreUser
}