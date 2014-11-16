# future-util

Utilities for `scala.concurrent.Future`.

Add a following line to `build.sbt`.

	libraryDependencies += "com.github.tkawachi" %% "future-util" % "0.0.2"

## FutureScheduler

`FutureScheduler` schedules an execution after certain time.

Using `Thread.sleep()` blocks the running thread.
It's not recommended to waste a thread when working with `Future`.

	val futureSum: Future[Int] = Future {
		// BLOCKING the thread in ExecutionContext. Not recommended.
		Thread.sleep(1000)
		1 + 1
	}

With `FutureScheduler` you can write:

	import scala.concurrent.duration._
	import com.tkawachi.github.future.FutureScheduler
	val futureSum: Future[Int] = FutureScheduler.schedule(1.second) {
		1 + 1
	}

You can use `akka.pattern.after()` if you're using Akka.
Otherwise try `FutureScheduler`.

NOTE: `FutureScheduler` uses one daemon thread to schedule executions.
