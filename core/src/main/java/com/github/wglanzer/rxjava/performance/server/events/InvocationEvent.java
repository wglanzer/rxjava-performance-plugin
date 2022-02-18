package com.github.wglanzer.rxjava.performance.server.events;

import org.jetbrains.annotations.NotNull;

/**
 * POJO for a single event that occurred
 *
 * @author w.glanzer, 18.02.2022
 */
public class InvocationEvent
{

  public String operatorID;
  public long duration;

  public InvocationEvent(@NotNull String pOperatorID, long pDuration)
  {
    operatorID = pOperatorID;
    duration = pDuration;
  }

}
