package com.github.wglanzer.rxjava.performance.agent.operators;

import org.jetbrains.annotations.NotNull;

/**
 * Interceptor for .map()
 *
 * @author w.glanzer, 17.02.2022
 * @see io.reactivex.rxjava3.internal.operators.observable.ObservableMap
 */
public class ObservableMapOperatorInterceptor extends AbstractOperatorInterceptor
{

  @Override
  public void onOperatorCreated(@NotNull Object pOperator)
  {
    applyWrapperToFunction(pOperator, "function");
  }

  @NotNull
  @Override
  public String getOperatorClassName()
  {
    return "io.reactivex.rxjava3.internal.operators.observable.ObservableMap";
  }

}
