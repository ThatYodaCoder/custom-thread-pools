package com.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This purpose of this service is to distribute the tasks in round robin fashion.
 * This will help us to avoid customer starvation which can/may happen in case of single
 * thread pool.
 */
public class StripedExecutorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StripedExecutorService.class);

  private final ExecutorService[] execs;

  private AtomicInteger idx = new AtomicInteger();

  private int arrIdx = 0;

  public StripedExecutorService(int noOfThreadPools, int corePoolSize, int maximumPoolSize, long keepAliveTime) {

    execs = new ExecutorService[noOfThreadPools];

    final String poolName = "report-data-process-pool-%s-thread";

    for (int i = 0; i < noOfThreadPools; i++) {

      final String tpFormat = String.format(poolName, i);

      ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(false).setNameFormat(tpFormat + "-%d")
          .setPriority(Thread.NORM_PRIORITY).setUncaughtExceptionHandler(new UncaughtExceptionLogger()).build();


      execs[i] = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(), threadFactory);

      LOGGER.info("Thread Pool={}",((ThreadPoolExecutor)execs[i]).toString());
    }
  }


  public ExecutorService getExecutorService() {
    return execs[getIndex()];
  }

  /**
   * Non blocking implementation of accessing array in circular way.
   * Equivalent blocking implementation of this method is given below
   * in this class. Please check getIndex1() method.
   *
   * @return index
   */
  private int getIndex() {

    int val = idx.incrementAndGet();

    while (val >= execs.length) {
      if (idx.compareAndSet(val, 0)) {
        val = 0;
        break;
      }
      val = idx.get();

      if (val < execs.length) {
        val = idx.incrementAndGet();
      }
    }
    return val;
  }

  private synchronized int getIndex1() {
    if (arrIdx >= execs.length) {
      arrIdx = 0;
    }
    return arrIdx++;
  }
}
