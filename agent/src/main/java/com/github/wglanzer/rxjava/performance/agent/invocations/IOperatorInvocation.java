package com.github.wglanzer.rxjava.performance.agent.invocations;

import org.jetbrains.annotations.NotNull;

/**
 * This object describes a single invocation of an operator
 *
 * @author w.glanzer, 17.02.2022
 * @see IOperator
 */
public interface IOperatorInvocation
{

  /**
   * @return the operator that this invocation belongs to
   */
  @NotNull
  IOperator getOperator();

  /**
   * @return the nanoseconds how long this invocation took
   */
  long getDurationNS();

}
