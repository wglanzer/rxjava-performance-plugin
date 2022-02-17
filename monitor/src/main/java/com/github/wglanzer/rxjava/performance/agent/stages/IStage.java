package com.github.wglanzer.rxjava.performance.agent.stages;

import org.jetbrains.annotations.NotNull;

/**
 * A stage is an object that gets created if an
 * observer was subscribed to its observable
 *
 * @author w.glanzer, 17.02.2022
 */
public interface IStage
{

  /**
   * @return unique identifier
   */
  @NotNull
  String getID();

}
