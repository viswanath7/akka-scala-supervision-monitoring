package com.example.akka.actor

import akka.actor.{Actor, Props}
import com.example.akka.actor.NotificationMessageTransmitter.{SendMessage, TransmissionException}
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

object NotificationMessageTransmitter {

  val props = Props[NotificationMessageTransmitter]

  sealed trait RequestMessage
  case class SendMessage(message: String) extends RequestMessage

  case object TransmissionException extends Exception
}

class NotificationMessageTransmitter extends Actor {

  val logger = LoggerFactory getLogger NotificationMessageTransmitter.getClass

  override def receive: Receive = {
    case SendMessage(message) =>
      logger debug ("Received a message to Transmit: {}", message)

      if (StringUtils isBlank message) {
        logger error "Cannot transmit a blank message"
        throw TransmissionException
      }
      else logger debug ("Transmitted message: {}", message)
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    logger debug "NotificationMessageTransmitter: Resurrecting ..."
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    logger debug "NotificationMessageTransmitter: I'm alive again!"
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    logger debug "NotificationMessageTransmitter: I'm dying. So long folks!"
    super.postStop()
  }

}
