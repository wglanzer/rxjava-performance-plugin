package com.github.wglanzer.rxjava.performance.server.events;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * POJO for a list of invocation events
 *
 * @author w.glanzer, 18.02.2022
 */
public class InvocationEvents
{

  public List<InvocationEvent> events;

  public InvocationEvents(@NotNull List<InvocationEvent> pEvents)
  {
    events = pEvents;
  }

}
