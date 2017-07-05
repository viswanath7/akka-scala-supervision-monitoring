package com.example.akka.application

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.example.akka.application.Romeo.Tragic
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}


class RomeoSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FlatSpecLike with BeforeAndAfterAll with MustMatchers {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Romeo actor" must "stop oneself upon receiving Tragic message" in {

    val testProbe = TestProbe()
    val romeo = system.actorOf(Romeo.props, "romeo")
    testProbe watch romeo

    romeo ! Tragic

    testProbe expectTerminated romeo
  }

}
