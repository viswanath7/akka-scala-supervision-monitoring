package com.example.akka.application

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.example.akka.application.Romeo.Tragic
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}
import org.slf4j.LoggerFactory


/**
  * Unit test for Juliet Actor
  *
  * The TestKit contains an actor named 'testActor' which is the entry point for messages to be examined
  * with the various expectMsg() assertions.
  *
  * When mixing in the trait ImplicitSender this test actor is implicitly used as sender reference
  * when dispatching messages from the test procedure.
  *
  */
class JulietSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FlatSpecLike with BeforeAndAfterAll with MustMatchers {

  val logger = LoggerFactory getLogger this.getClass.getSimpleName

  override def afterAll: Unit = {
    logger debug "Shutting down the actor system once all the tests have been completed " +
      "so that all actors—including the test actor—are stopped. ..."
    TestKit shutdownActorSystem system
  }

  "Juliet watching Romeo" should "kill herself when Romeo is terminated" in {

    val probe = TestProbe()
    val romeo = system.actorOf(Romeo.props, "romeo")
    val juliet = system.actorOf(Props(new Juliet(romeo)), "juliet")

    probe watch juliet
    romeo ! Tragic
    probe expectTerminated juliet
  }

}
