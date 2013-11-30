package org.wikivector.common

import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.LinkedBlockingQueue

object OtherExecutors {
	def createFixedThreadsBlockingExecutor(threads: Int, queueSize: Int): ExecutorService = {
	  return new ThreadPoolExecutor(
	      threads, 
	      threads, 
	      0, 
	      TimeUnit.MILLISECONDS, 
	      new LinkedBlockingQueue[Runnable](queueSize),
	      new ThreadPoolExecutor.CallerRunsPolicy)
	}
}