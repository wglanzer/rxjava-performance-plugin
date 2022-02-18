package com.github.wglanzer.rxjava.performance.agent.invocations;

import org.jetbrains.annotations.NotNull;

/**
 * An operator is an object that gets created during
 * the build phase of an observable
 *
 * @author w.glanzer, 17.02.2022
 */
public interface IOperator
{

  /**
   * @return unique identifier
   */
  @NotNull
  String getID();

}
