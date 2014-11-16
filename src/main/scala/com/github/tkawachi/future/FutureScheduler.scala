package com.github.tkawachi.future

import java.util.{ Date, Timer, TimerTask }

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future, Promise }

/**
 * Scheduler for `scala.concurrent.Future`.
 *
 * @param daemon Mark an underlying thread daemon.
 */
class FutureScheduler(daemon: Boolean) {
  private[this] val timer = new Timer("FutureScheduler", daemon)

  private[this] def durationUntil(date: Date): Duration = {
    val now = System.currentTimeMillis()
    Math.max(date.getTime - now, 0).milliseconds
  }

  /**
   * Schedule execution of `f` at `date`.
   */
  def schedule[A](date: Date)(f: => A)(implicit ec: ExecutionContext): Future[A] =
    schedule(durationUntil(date))(f)(ec)

  /**
   * Schedule execution of `f` after `duration`.
   */
  def schedule[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext): Future[A] =
    scheduleWith(duration)(Future(f))

  /**
   * Schedule execution of `f` at `date`.
   */
  def scheduleWith[A](date: Date)(f: => Future[A]): Future[A] =
    scheduleWith(durationUntil(date))(f)

  /**
   * Schedule execution of `f` after `duration`.
   */
  def scheduleWith[A](duration: Duration)(f: => Future[A]): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = promise.completeWith(f)
    }, duration.toMillis)
    promise.future
  }

  /**
   * Cancel this scheduler.
   */
  def cancel(): Unit = timer.cancel()
}

object FutureScheduler {
  /**
   * The global scheduler.
   *
   * It creates an underlying daemon thread.
   */
  lazy val global = new FutureScheduler(true)

  /**
   * Schedule execution of `f` at `date`.
   */
  def schedule[A](date: Date)(f: => A)(implicit ec: ExecutionContext) = global.schedule(date)(f)(ec)

  /**
   * Schedule execution of `f` after `duration`.
   */
  def schedule[A](duration: Duration)(f: => A)(implicit ec: ExecutionContext) = global.schedule(duration)(f)(ec)

  /**
   * Schedule execution of `f` at `date`.
   */
  def scheduleWith[A](date: Date)(f: => Future[A]): Future[A] = global.scheduleWith(date)(f)

  /**
   * Schedule execution of `f` after `duration`.
   */
  def scheduleWith[A](duration: Duration)(f: => Future[A]): Future[A] = global.scheduleWith(duration)(f)
}
