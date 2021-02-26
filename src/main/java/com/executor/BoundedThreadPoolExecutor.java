package com.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implemented using Semaphore
 * Threadpool to throttle the task submission when the task queue is full or
 * when you want only N number of tasks to be submitted to executor service at a time.
 * BOUND value can be kept same as queue size.
 */
public final class BoundedThreadPoolExecutor extends ThreadPoolExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoundedThreadPoolExecutor.class);

  private final Semaphore countingSemaphore;

  public BoundedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, final int BOUND, ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    countingSemaphore = new Semaphore(BOUND);
  }

  @Override
  public void execute(Runnable task) {

    boolean acquired = false;
    do {
      try {
        countingSemaphore.acquire();
        acquired = true;
      } catch (InterruptedException e) {
        LOGGER.warn("InterruptedException while acquiring semaphore.", e);
        Thread.currentThread().interrupt();
      }
    } while (!acquired);

    try {
      super.execute(task);
    } catch (RuntimeException e) {
      // specifically, handle RejectedExecutionException
      countingSemaphore.release();
      LOGGER.debug("Semaphore released, current available permits={}", countingSemaphore.availablePermits());
      throw e;
    }
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    countingSemaphore.release();
    LOGGER.debug("Semaphore released, current available permits={}", countingSemaphore.availablePermits());
  }

}
