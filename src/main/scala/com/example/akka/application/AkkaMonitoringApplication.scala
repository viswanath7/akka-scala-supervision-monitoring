package com.example.akka.application

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.example.akka.application.Romeo.Tragic
import org.slf4j.LoggerFactory

object Juliet

class Juliet(romeo: ActorRef) extends Actor {

  val logger = LoggerFactory getLogger "Juliet"

  override def preStart() = {
    logger info "Juliet: Starting ..."
    super.preStart()
    logger info "Juliet: Watching Romeo"
    context.watch(romeo)
  }

  override def postStop() = {
    logger info "Juliet awakens to see her beloved Romeo dead!"
    logger info "Juliet takes Romeo's dagger and stabs herself through the heart."
    super.postStop()
  }

  override def receive = {
    case Terminated =>
      context stop self
  }
}

object Romeo {
  val props = Props[Romeo]
  sealed trait RequestMessage
  case object Tragic extends RequestMessage
}

class Romeo extends Actor {

  val logger = LoggerFactory getLogger Romeo.getClass

  override def receive = {
    case Tragic =>
      logger info "Romeo: Learns from Benvolio that Juliet is \"dead\". "
      logger info "Romeo, who in turn is so distraught with grieve, that he drinks poison!"
      context stop self
  }
}

object AkkaMonitoringApplication extends App {

  val logger = LoggerFactory getLogger AkkaMonitoringApplication.getClass

  logger info "Creating actor system Romeo & Juliet"
  val system = ActorSystem("RomeoAndJuliet")

  logger info "Creating Romeo ..."
  val romeo = system.actorOf(Props[Romeo], "romeo")

  logger info "Creating Juliet ..."
  val juliet = system.actorOf(Props(classOf[Juliet], romeo), "juliet")

  logger info "Sending tragic message to Romeo"
  romeo ! Tragic

  Thread sleep 1000

  logger info "For never was a story of more woe. Than this of Juliet and her Romeo."

  system terminate
}
