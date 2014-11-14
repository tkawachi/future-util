package com.github.tkawachi.future

import java.util.{ Timer, TimerTask }

import scala.concurrent.duration.Duration
import scala.concurrent.{ ExecutionContext, Future, Promise }

class FutureScheduler(daemon: Boolean) {
  private[this] val timer = new Timer("FutureScheduler", daemon)

  def schedule[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext): Future[A] =
    scheduleWith(duration)(Future(f))

  def scheduleWith[A](duration: Duration)(f: => Future[A]): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = promise.completeWith(f)
    }, duration.toMillis)
    promise.future
  }

  def cancel(): Unit = timer.cancel()
}

object FutureScheduler {
  /**
   * The global scheduler.
   *
   * It creates an underlying daemon thread.
   */
  lazy val global = new FutureScheduler(true)

  def schedule[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext) = global.schedule(duration)(f)(ec)

  def scheduleWith[A](duration: Duration)(f: => Future[A]): Future[A] = global.scheduleWith(duration)(f)
}
