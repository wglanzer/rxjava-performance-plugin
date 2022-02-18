package com.github.wglanzer.rxjava.performance.agent.server;

import org.jetbrains.annotations.NotNull;

/**
 * POJO for a single event that occurred
 *
 * @author w.glanzer, 18.02.2022
 */
public class StageInvocationEvent
{

  public String stageID;
  public long duration;

  public StageInvocationEvent(@NotNull String pStageID, long pDuration)
  {
    stageID = pStageID;
    duration = pDuration;
  }

}
