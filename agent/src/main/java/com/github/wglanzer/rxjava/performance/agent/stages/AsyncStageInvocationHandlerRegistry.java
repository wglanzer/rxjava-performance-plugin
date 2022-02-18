package com.github.wglanzer.rxjava.performance.agent.stages;

import com.github.wglanzer.rxjava.performance.agent.util.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

/**
 * Asynchronous StageInvocationHandlerRegistry
 *
 * @author w.glanzer, 17.02.2022
 */
class AsyncStageInvocationHandlerRegistry implements IStageInvocationHandler.IRegistry
{
  private final Executor executor = new ThreadPoolExecutor(0, 4, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder()
      .setDaemon(true)
      .setNameFormat("tRxPerformanceAgent-InvHandler-%d")
      .setPriority(Thread.MIN_PRIORITY)
      .build());
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final List<IStageInvocationHandler> handlers = new ArrayList<>();

  @Override
  public void addHandler(@NotNull IStageInvocationHandler pHandler)
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
  public void removeHandler(@NotNull IStageInvocationHandler pHandler)
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
  public void fireAsync(@NotNull Consumer<IStageInvocationHandler> pHandlerConsumer)
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
