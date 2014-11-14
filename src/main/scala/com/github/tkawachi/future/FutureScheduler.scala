package com.github.tkawachi.future

import java.util.{ TimerTask, Timer }

import scala.concurrent.{ Promise, Future }
import scala.concurrent.duration.Duration

class FutureScheduler {
  val timer = new Timer()

  def after[A](duration: Duration)(f: => Future[A]): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = promise.completeWith(f)
    }, duration.toMillis)
    promise.future
  }

  def cancel(): Unit = timer.cancel()
}
