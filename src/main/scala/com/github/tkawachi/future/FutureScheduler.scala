package com.github.tkawachi.future

import java.util.{ Timer, TimerTask }

import scala.concurrent.duration.Duration
import scala.concurrent.{ ExecutionContext, Future, Promise }

class FutureScheduler(daemon: Boolean) {
  val timer = new Timer("FutureScheduler", daemon)

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

object FutureScheduler {
  /**
   * The global scheduler.
   *
   * It creates underlying a daemon thread.
   */
  lazy val global = new FutureScheduler(true)

  def after[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext) = global.after(duration)(f)(ec)

  def afterWith[A](duration: Duration)(f: => Future[A]): Future[A] = global.afterWith(duration)(f)
}
