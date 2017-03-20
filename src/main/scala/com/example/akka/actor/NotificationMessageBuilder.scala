package com.example.akka.actor

import java.time.LocalDateTime

import akka.actor.{Actor, Props}
import NotificationMessageBuilder.{CreateMessage, MessageCreated, NotificationMessageBuilderException}
import akka.actor.Status.Failure
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

import scala.util.Random

object NotificationMessageBuilder {

  val props = Props[NotificationMessageBuilder]

  sealed trait RequestMessage
  case class CreateMessage(action: String) extends RequestMessage

  sealed trait ResponseMessage
  case class MessageCreated(message: String) extends ResponseMessage

  object NotificationMessageBuilderException extends Exception
}

class NotificationMessageBuilder extends Actor {

  val logger = LoggerFactory getLogger NotificationMessageBuilder.getClass

  override def receive = {
    case CreateMessage(action) =>
      logger debug ("Received 'CreateMessage' for action: '{}' ", action)

      /**
        * For actors that are doing request/response, you may actually want to handle (catch) specific exceptions and
        * return certain response types instead (or fail the upstream future) as opposed to letting them go unhandled.
        * When an unhandled exception happens, you basically lose the ability to respond to the sender with a description
        * of the issue and the sender will probably then get a TimeoutException instead, as their Future will never be completed.
        */
      if(Random.nextBoolean()) {
        logger warn "Simulating random failure ..."
        sender ! Failure(NotificationMessageBuilderException) // Signal failure to AskSupport
        throw NotificationMessageBuilderException // Trigger supervisor's one-for-one-strategy for this exception
      }

      if (StringUtils isBlank action) {
        logger debug "Sending MessageCreated message with blank action!"
        sender ! MessageCreated("")
      } else {
        val messageCreated = MessageCreated("Action: " + action + " Occurred: " + LocalDateTime.now)
        logger debug ("Sending notification message: {}", messageCreated)
        sender ! messageCreated
      }
    case _ =>
      logger error "Unknown message type"
      throw new Exception
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    logger debug "NotificationMessageBuilder: Resurrecting ..."
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    logger debug "NotificationMessageBuilder: I'm alive again!"
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    logger debug "NotificationMessageBuilder: I'm dying. So long folks!"
    super.postStop()
  }

}
