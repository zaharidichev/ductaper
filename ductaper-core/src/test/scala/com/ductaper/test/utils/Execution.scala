package com.ductaper.test.utils

import scala.concurrent.ExecutionContext

/**
  * Created by zahari on 07/03/2017.
  */
object Execution {
  implicit val currentThreadExecutionContext = ExecutionContext.fromExecutor((runnable: Runnable) => runnable.run())
}
