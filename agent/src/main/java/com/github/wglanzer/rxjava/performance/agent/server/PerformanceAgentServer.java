package com.github.wglanzer.rxjava.performance.agent.server;

import com.github.wglanzer.rxjava.performance.agent.stages.*;
import com.github.wglanzer.rxjava.performance.agent.util.ThreadFactoryBuilder;
import com.github.wglanzer.rxjava.performance.server.events.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.*;
import org.snf4j.core.SelectorLoop;
import org.snf4j.core.factory.AbstractSessionFactory;
import org.snf4j.core.handler.*;
import org.snf4j.core.session.IStreamSession;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * Server that provides all invocations
 *
 * @author w.glanzer, 18.02.2022
 */
public class PerformanceAgentServer
{

  private final int port;

  public PerformanceAgentServer(int pPort)
  {
    port = pPort;
  }

  /**
   * Starts the server asynchronously
   */
  public void start() throws Exception
  {
    ServerSocketChannel channel = ServerSocketChannel.open();
    channel.configureBlocking(false);
    channel.socket().bind(new InetSocketAddress(port));

    SelectorLoop loop = new SelectorLoop();
    loop.setThreadFactory(new ThreadFactoryBuilder()
                              .setNameFormat("tRxPerformanceAgent-Client-%d")
                              .setPriority(Thread.MIN_PRIORITY)
                              .setDaemon(true)
                              .build());
    loop.start();
    loop.register(channel, new AbstractSessionFactory()
    {
      @Override
      protected IStreamHandler createHandler(SocketChannel channel)
      {
        return new _StreamHandler();
      }
    });
  }

  /**
   * StreamHandler-Impl providing the invocations
   */
  private static class _StreamHandler extends AbstractStreamHandler implements IStageInvocationHandler
  {
    private static final int _SENDING_MS = 1000;
    private static final Gson _GSON = new Gson();
    private final ScheduledExecutorService sendingExecutor = Executors.newSingleThreadScheduledExecutor();
    private final List<StageInvocationEvent> queue = new LinkedList<>();
    private ScheduledFuture<?> sendingFuture;

    @Override
    public void event(SessionEvent pEvent)
    {
      switch (pEvent)
      {
        case READY:
          IRegistry.INSTANCE.addHandler(this);
          sendingFuture = sendingExecutor.scheduleAtFixedRate(this::_pollQueueAndSend, _SENDING_MS, _SENDING_MS, TimeUnit.MILLISECONDS);
          break;

        case CLOSED:
        case ENDING:
          IRegistry.INSTANCE.removeHandler(this);
          if (sendingFuture != null)
          {
            sendingFuture.cancel(false);
            sendingFuture = null;
          }
          break;
      }
    }

    @Override
    public void read(@Nullable Object pMessage)
    {
      // nothing
    }

    @Override
    public void handleInvocationStarted(@NotNull IStageInvocation pInvocation)
    {
      // nothing
    }

    @Override
    public void handleInvocationFinished(@NotNull IStageInvocation pInvocation)
    {
      synchronized (queue)
      {
        queue.add(new StageInvocationEvent(pInvocation.getStage().getID(), pInvocation.getDurationNS()));
      }
    }

    /**
     * Gets called if the handler should transfer all currently
     * received events to this client
     */
    private void _pollQueueAndSend()
    {
      IStreamSession session = getSession();
      if (session != null && session.isOpen())
      {
        List<StageInvocationEvent> eventsToSend;

        synchronized (queue)
        {
          eventsToSend = new ArrayList<>(queue);
          queue.clear();
        }

        if (!eventsToSend.isEmpty())
          session.writenf(_GSON.toJson(new StageInvocationEvents(eventsToSend)).getBytes(StandardCharsets.UTF_8));
      }
    }
  }

}
