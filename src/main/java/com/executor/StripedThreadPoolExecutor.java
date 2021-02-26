package com.executor;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * This class maintains multiple thread pools.
 * User can submit the task to the desired thread pool using thread-pool-id.
 * e.g: Thread Pool Id = subscriptionId%noOfThreadPools or (subscriptionId & (noOfThreadPools-1))
 */
public class StripedThreadPoolExecutor implements ExecutorService {

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
  public StripedThreadPoolExecutor(int noOfThreadPools, int corePoolSize, int maximumPoolSize, int queueBound,
      long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, UncaughtExceptionHandler eHnd) {
    execs = new ExecutorService[noOfThreadPools];

    for (int i = 0; i < noOfThreadPools; i++) {

      execs[i] = new BoundedThreadPoolExecutor(corePoolSize, corePoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(), queueBound, threadFactory);
    }
  }

  /**
   * Executes the given command at some time in the future. The command with the same {@code index}
   * will be executed in the same thread.
   *
   * @param task the runnable task
   * @param threadPoolId Striped index.
   * @throws RejectedExecutionException if this task cannot be
   * accepted for execution.
   * @throws NullPointerException If command is null
   */
  public void execute(Runnable task, int threadPoolId) {
    execs[threadId(threadPoolId)].execute(task);
  }

  /**
   * @param threadPoolId Index.
   * @return Stripped thread ID.
   */
  public int threadId(int threadPoolId) {
    return threadPoolId < execs.length ? threadPoolId : threadPoolId % execs.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown() {
    for (ExecutorService exec : execs) {
      exec.shutdown();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Runnable> shutdownNow() {
    if (execs.length == 0) {
      return Collections.emptyList();
    }

    List<Runnable> res = new ArrayList<>(execs.length);

    for (ExecutorService exec : execs) {
      for (Runnable r : exec.shutdownNow()) {
        res.add(r);
      }
    }

    return res;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isShutdown() {
    for (ExecutorService exec : execs) {
      if (!exec.isShutdown()) {
        return false;
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTerminated() {
    for (ExecutorService exec : execs) {
      if (!exec.isTerminated()) {
        return false;
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    boolean res = true;

    for (ExecutorService exec : execs) {
      res &= exec.awaitTermination(timeout, unit);
    }

    return res;
  }

  /**
   * {@inheritDoc}
   */
  
  public <T> Future<T> submit(Callable<T> task,int threadPoolId) {
    return execs[threadId(threadPoolId)].submit(task);
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public <T> Future<T> submit(Callable<T> task) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public <T> Future<T> submit(Runnable task, T res) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public Future<?> submit(Runnable task) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  
  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(Runnable cmd) {
    throw new UnsupportedOperationException();
  }


}
