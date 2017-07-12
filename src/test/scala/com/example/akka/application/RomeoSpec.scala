package com.example.akka.application

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.example.akka.application.Romeo.Tragic
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}
import org.slf4j.LoggerFactory

import scala.language.postfixOps

/**
  * Unit test for Romeo Actor
  *
  * The TestKit contains an actor named 'testActor' which is the entry point for messages to be examined
  * with the various expectMsg() assertions.
  *
  * When mixing in the trait ImplicitSender this test actor is implicitly used as sender reference
  * when dispatching messages from the test procedure.
  *
  */
class RomeoSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FlatSpecLike with BeforeAndAfterAll with MustMatchers {

  val logger = LoggerFactory getLogger this.getClass.getSimpleName


  override def afterAll: Unit = {
    logger debug "Shutting down the actor system as all the tests have been completed " +
      "so that all actors—including the test actor—are stopped. ..."
    TestKit shutdownActorSystem system
  }

  "Romeo actor" must "stop oneself upon receiving Tragic message" in {
    import scala.concurrent.duration._

    val testProbe = TestProbe()
    val romeo = system.actorOf(Romeo.props, "romeo")
    testProbe watch romeo

    romeo ! Tragic

    within(10 microseconds, 5 seconds) {
      logger debug "Romeo must be terminated within 5 seconds ..."
      testProbe expectTerminated romeo
    }
  }

}
