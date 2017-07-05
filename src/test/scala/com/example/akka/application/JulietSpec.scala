package com.example.akka.application

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.example.akka.application.Romeo.Tragic
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}


class JulietSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FlatSpecLike with BeforeAndAfterAll with MustMatchers {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
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
