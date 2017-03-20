package com.example.akka.actor

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.akka.actor.MutationNotifier.Notify
import com.example.akka.actor.NotificationMessageBuilder.{CreateMessage, MessageCreated, NotificationMessageBuilderException}
import com.example.akka.actor.NotificationMessageTransmitter.{SendMessage, TransmissionException}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

object MutationNotifier {

  val props = Props[MutationNotifier]

  sealed trait RequestMessage
  case class Notify(action: String) extends RequestMessage
}

class MutationNotifier extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  val logger = LoggerFactory getLogger MutationNotifier.getClass

  var messageBuilder: ActorRef = _
  var messageTransmitter: ActorRef = _

  override def receive = {

    case Notify(action) =>
      logger debug ("Received message to notify action: {}", action)
      val future = messageBuilder ? CreateMessage(action)

      future onFailure {
        case NotificationMessageBuilderException =>
          logger debug "Ask operation resulted in a failure!"
      }

      future onSuccess {
        case MessageCreated(notificationMessage) =>
          logger debug ("Created notification message: {}", notificationMessage)
          messageTransmitter ! SendMessage(notificationMessage)
        case _ =>
          logger debug "Unknown response"
      }

  }

  override def preStart() = {
    logger debug "MutationNotifier: Starting ..."
    logger debug "MutationNotifier: Creating child actor 'NotificationMessageBuilder' ..."
    messageBuilder_=(context.actorOf(NotificationMessageBuilder.props, "notification-message-builder"))
    logger debug "MutationNotifier: Creating child actor 'notification-message-transmitter' ..."
    messageTransmitter = context.actorOf(NotificationMessageTransmitter.props, "NotificationMessageTransmitter")
    super.preStart()
  }

  /**
    * When you override the default supervisor strategy, you gain the ability to change how certain types of
    * "unhandled" exceptions in the child actor are handled in regards to what to do with that failed child actor.
    */
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
      case TransmissionException =>
        logger warn "Received transmission exception from child but resuming action"
        Resume
      case NotificationMessageBuilderException  =>
        logger warn "Cannot build notification message so restarting the child in an effort to recover"
        Restart
      case e: Exception   =>
        logger warn ("Oopsie! uncaught exception",e)
        Escalate
    }

}
