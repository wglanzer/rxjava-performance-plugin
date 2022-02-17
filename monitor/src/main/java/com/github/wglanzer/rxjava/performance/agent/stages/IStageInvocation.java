package com.github.wglanzer.rxjava.performance.agent.stages;

import org.jetbrains.annotations.NotNull;

/**
 * This object describes a single invocation of a stage
 *
 * @author w.glanzer, 17.02.2022
 * @see IStage
 */
public interface IStageInvocation
{

  /**
   * @return the stage that this invocation belongs to
   */
  @NotNull
  IStage getStage();

  /**
   * @return the nanoseconds how long this invocation took
   */
  long getDurationNS();

}
