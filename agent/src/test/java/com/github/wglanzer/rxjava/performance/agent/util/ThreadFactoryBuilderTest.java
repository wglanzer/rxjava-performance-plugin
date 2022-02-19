package com.github.wglanzer.rxjava.performance.agent.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author w.glanzer, 19.02.2022
 */
class ThreadFactoryBuilderTest
{

  @Test
  void shouldSetName()
  {
    String name = "tTestThread";
    ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                                    .setNameFormat(name)
                                                                    .build());
    service.submit(() -> assertEquals(name, Thread.currentThread().getName()));
    service.shutdown();
  }

  @Test
  void shouldSetDaemon()
  {
    ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                                    .setDaemon(true)
                                                                    .build());
    service.submit(() -> assertTrue(Thread.currentThread().isDaemon()));
    service.shutdown();

    service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                    .setDaemon(false)
                                                    .build());
    service.submit(() -> assertFalse(Thread.currentThread().isDaemon()));
    service.shutdown();
  }

  @Test
  void shouldSetPriority()
  {
    ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                                    .setPriority(Thread.MIN_PRIORITY)
                                                                    .build());
    service.submit(() -> assertEquals(Thread.MIN_PRIORITY, Thread.currentThread().getPriority()));
    service.shutdown();

    service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                    .setPriority(Thread.NORM_PRIORITY)
                                                    .build());
    service.submit(() -> assertEquals(Thread.NORM_PRIORITY, Thread.currentThread().getPriority()));
    service.shutdown();

    service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                    .setPriority(Thread.MAX_PRIORITY)
                                                    .build());
    service.submit(() -> assertEquals(Thread.MAX_PRIORITY, Thread.currentThread().getPriority()));
    service.shutdown();
  }

  @Test
  void shouldSetUncaughtExceptionHandler()
  {
    Thread.UncaughtExceptionHandler handler = (t, e) -> {
    };
    ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                                                                    .setUncaughtExceptionHandler(handler)
                                                                    .build());
    service.submit(() -> assertEquals(handler, Thread.currentThread().getUncaughtExceptionHandler()));
    service.shutdown();
  }
}