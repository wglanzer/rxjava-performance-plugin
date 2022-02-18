package com.github.wglanzer.rxjava.performance.client;

import com.github.wglanzer.rxjava.performance.client.events.IEventRepository;
import com.github.wglanzer.rxjava.performance.server.events.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.snf4j.core.*;
import org.snf4j.core.handler.AbstractStreamHandler;
import org.snf4j.core.session.*;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

/**
 * Client that connects to the agent server
 *
 * @author w.glanzer, 18.02.2022
 */
public class PerformanceClient
{
  private final String host;
  private final int port;
  private final _Repository eventRepository = new _Repository();

  public PerformanceClient(@NotNull String pHost, int pPort)
  {
    host = pHost;
    port = pPort;
  }

  /**
   * Connects to the performance agent server
   */
  public void connect() throws Exception
  {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    channel.connect(new InetSocketAddress(InetAddress.getByName(host), port));

    SelectorLoop loop = new SelectorLoop();
    loop.start();
    loop.register(channel, new _Handler());
  }

  /**
   * @return the event repository containing all received events
   */
  @NotNull
  public IEventRepository getEventRepository()
  {
    return eventRepository;
  }

  /**
   * Handler-Impl
   */
  private class _Handler extends AbstractStreamHandler
  {
    private final Gson gson = new Gson();
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void read(byte[] data)
    {
      buffer.append(new String(data));

      if (data.length < getConfig().getMaxInBufferCapacity())
      {
        try
        {
          StageInvocationEvents events = gson.fromJson(buffer.toString(), StageInvocationEvents.class);
          List<StageInvocationEvent> allEvents = events.events;
          if (allEvents != null)
            eventRepository._addEvents(allEvents);
        }
        catch (Exception e)
        {
          Logger.getLogger(PerformanceClient.class.getName()).log(Level.WARNING, "", e);
        }
        finally
        {
          buffer.setLength(0);
        }
      }
    }

    @Override
    public void read(Object msg)
    {
      // ignore
    }

    @Override
    public ISessionConfig getConfig()
    {
      return new DefaultSessionConfig().setEndingAction(EndingAction.STOP);
    }
  }

  /**
   * EventRepository-Impl
   */
  private static class _Repository implements IEventRepository
  {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<IRepositoryListener> listeners = new ArrayList<>();

    @Override
    public void addListener(@NotNull IRepositoryListener pListener)
    {
      Lock wLock = lock.writeLock();
      try
      {
        wLock.lock();
        listeners.add(pListener);
      }
      finally
      {
        wLock.unlock();
      }
    }

    @Override
    public void removeListener(@NotNull IRepositoryListener pListener)
    {
      Lock wLock = lock.writeLock();
      try
      {
        wLock.lock();
        listeners.remove(pListener);
      }
      finally
      {
        wLock.unlock();
      }
    }

    /**
     * Adds events to this repository
     *
     * @param pEvents events to add
     */
    private void _addEvents(@NotNull List<StageInvocationEvent> pEvents)
    {
      Lock wLock = lock.readLock();
      try
      {
        wLock.lock();
        listeners.forEach(pListener -> pListener.eventsReceived(pEvents));
      }
      finally
      {
        wLock.unlock();
      }
    }

  }

}
