package com.github.wglanzer.rxjava.performance.agent.operators;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An OperatorInterceptor represents an object that intercepts the creation of a rxjava operator
 *
 * @author w.glanzer, 17.02.2022
 */
public interface IOperatorInterceptor
{

  /**
   * Contains a list of all interceptors currently available
   */
  List<IOperatorInterceptor> OPERATORS = List.of(new ObservableMapOperatorInterceptor());

  /**
   * @return the method that should be called, if an operator was created
   */
  @NotNull
  default Method getOnOperatorCreatedMethod() throws NoSuchMethodException
  {
    Method onOperatorCreated = getClass().getDeclaredMethod("onOperatorCreated", Object.class);
    onOperatorCreated.setAccessible(true);
    return onOperatorCreated;
  }

  /**
   * This method gets called if an operator, identified by {@link this#getOperatorClassName()}, was created
   *
   * @param pOperator the operator instance
   */
  void onOperatorCreated(@NotNull Object pOperator);

  /**
   * @return FQDN of the operator class
   */
  @NotNull
  String getOperatorClassName();

}
