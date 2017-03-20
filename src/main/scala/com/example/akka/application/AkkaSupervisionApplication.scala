package com.example.akka.application

import akka.actor.ActorSystem
import com.example.akka.actor.MutationNotifier
import com.example.akka.actor.MutationNotifier.Notify
import org.slf4j.LoggerFactory


object AkkaSupervisionApplication extends App {

  val logger = LoggerFactory getLogger AkkaSupervisionApplication.getClass

  logger debug "Creating actor system ..."
  val system = ActorSystem("akka-scala-actor-system")

  logger debug "Creating Mutation Notifier Actor at top-level ..."
  val mutationNotifier = system.actorOf(MutationNotifier.props, "mutation-notifier")

  mutationNotifier ! Notify("")
  Thread sleep 1000
  mutationNotifier ! Notify("JohnDoe - password changed")
  Thread sleep 1000

  system.terminate()
}