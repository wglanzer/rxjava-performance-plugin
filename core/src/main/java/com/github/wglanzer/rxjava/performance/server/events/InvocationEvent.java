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
  public String operatorName;
  public long duration;

  public InvocationEvent(@NotNull String pOperatorID, @NotNull String pOperatorName, long pDuration)
  {
    operatorID = pOperatorID;
    operatorName = pOperatorName;
    duration = pDuration;
  }

}
