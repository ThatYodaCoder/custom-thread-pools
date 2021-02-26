package com.executor;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class StripedExecutorService {

  /** */
  private final ExecutorService[] execs;

  /**
   *
   * @param noOfThreadPools
   * @param corePoolSize
   * @param maximumPoolSize
   * @param keepAliveTime
   * @param unit
   * @param threadFactory
   * @param eHnd
   */
  public StripedExecutorService(int noOfThreadPools, int corePoolSize, int maximumPoolSize, int queueBound,
      long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, UncaughtExceptionHandler eHnd) {
    execs = new ExecutorService[noOfThreadPools];

    for (int i = 0; i < noOfThreadPools; i++) {

      execs[i] = new com.executor.BoundedThreadPoolExecutor(corePoolSize, corePoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(), queueBound, threadFactory);
    }
  }

  public ExecutorService getExecutorService(long subscriptionId){
    final int threadPoolId = (int)subscriptionId & (execs.length - 1);
    //final int threadPoolId = (int)(subscriptionId % (execs.length - 1));
    return execs[threadPoolId];
  }
}
