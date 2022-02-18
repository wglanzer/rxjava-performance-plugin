package com.github.wglanzer.rxjava.performance.agent.server;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * POJO for a list of invocation events
 *
 * @author w.glanzer, 18.02.2022
 */
public class StageInvocationEvents
{

  public List<StageInvocationEvent> events;

  public StageInvocationEvents(@NotNull List<StageInvocationEvent> pEvents)
  {
    events = pEvents;
  }

}
