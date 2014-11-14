package com.github.tkawachi.future

import java.util.concurrent.{ ExecutorService, Executors, TimeoutException }

import org.scalatest.{ BeforeAndAfter, FunSpec, Matchers }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

class FutureSchedulerTest extends FunSpec with BeforeAndAfter with Matchers {

  var executor: ExecutorService = _
  implicit var ec: ExecutionContext = _

  before {
    // ExecutionContext executes in a single thread.
    executor = Executors.newSingleThreadExecutor()
    ec = ExecutionContext.fromExecutorService(executor)
  }

  after {
    executor.shutdownNow()
  }

  def delayedPrintBlock(d: Duration): Future[Unit] = Future {
    try {
      Thread.sleep(d.toMillis)
      println(s"${d.toMillis} msec has been passed in ${Thread.currentThread()}")
    } catch {
      case _: InterruptedException =>
    }
  }

  def delayedPrint(d: Duration): Future[Unit] = FutureScheduler.schedule(d) {
    println(s"${d.toMillis} msec has been passed in ${Thread.currentThread()}")
  }

  describe("after()") {
    it("should not timeout") {
      val f = Future.sequence((1 to 3).map(i => delayedPrint(i.seconds)))
      Await.ready(f, 4.seconds)
    }

    it("should timeout with blocked one") {
      val f = Future.sequence((1 to 3).map(i => delayedPrintBlock(i.seconds)))
      an[TimeoutException] should be thrownBy {
        Await.ready(f, 4.seconds)
      }
    }
  }

}
