package com.github.tkawachi.future

import java.util.{ Timer, TimerTask }

import scala.concurrent.duration.Duration
import scala.concurrent.{ ExecutionContext, Future, Promise }

class FutureScheduler {
  val timer = new Timer()

  def after[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext): Future[A] =
    afterWith(duration)(Future(f))

  def afterWith[A](duration: Duration)(f: => Future[A]): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = promise.completeWith(f)
    }, duration.toMillis)
    promise.future
  }

  def cancel(): Unit = timer.cancel()
}
