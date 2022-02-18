package com.github.wglanzer.rxjava.performance.agent.invocations;

import com.github.wglanzer.rxjava.performance.agent.util.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

/**
 * Asynchronous OperatorInvocationHandlerRegistry
 *
 * @author w.glanzer, 17.02.2022
 */
class AsyncOperatorInvocationHandlerRegistry implements IOperatorInvocationHandler.IRegistry
{
  private final Executor executor = new ThreadPoolExecutor(0, 4, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder()
      .setDaemon(true)
      .setNameFormat("tRxPerformanceAgent-InvHandler-%d")
      .setPriority(Thread.MIN_PRIORITY)
      .build());
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final List<IOperatorInvocationHandler> handlers = new ArrayList<>();

  @Override
  public void addHandler(@NotNull IOperatorInvocationHandler pHandler)
  {
    Lock wLock = lock.writeLock();
    try
    {
      wLock.lock();
      handlers.add(pHandler);
    }
    finally
    {
      wLock.unlock();
    }
  }

  @Override
  public void removeHandler(@NotNull IOperatorInvocationHandler pHandler)
  {
    Lock wLock = lock.writeLock();
    try
    {
      wLock.lock();
      handlers.remove(pHandler);
    }
    finally
    {
      wLock.unlock();
    }
  }

  @Override
  public void fireAsync(@NotNull Consumer<IOperatorInvocationHandler> pHandlerConsumer)
  {
    Lock rLock = lock.readLock();
    try
    {
      rLock.lock();
      handlers.forEach(pConsumer -> executor.execute(() -> pHandlerConsumer.accept(pConsumer)));
    }
    finally
    {
      rLock.unlock();
    }
  }
}
