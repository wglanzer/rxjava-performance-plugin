package com.github.wglanzer.rxjava.performance.agent.operators;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An OperatorInterceptor represents an object that intercepts the
 * creation of a rxjava operator and its corresponding stage that gets created on subscribe.
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
   * Name of the field that gets dynamically created inside operator to persist
   * the original "creator", that created the operator. This makes anything much more easy for debugging..
   */
  String OPERATOR_CREATOR_FIELDNAME = "$$$gen$$$creator";

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
   * @return the method that should be called, if a stage was created during subscribe
   */
  @NotNull
  default Method getOnStageCreatedMethod() throws NoSuchMethodException
  {
    Method onStageCreated = getClass().getDeclaredMethod("onStageCreated", Object.class);
    onStageCreated.setAccessible(true);
    return onStageCreated;
  }

  /**
   * This method gets called if a stage, identified by {@link this#getStageClassName()}, was created.
   * This happens mainly during subscribe phase of an observable.
   *
   * @param pStage the stage instance
   */
  void onStageCreated(@NotNull Object pStage);

  /**
   * @return FQDN of the operator class
   */
  @NotNull
  String getOperatorClassName();

  /**
   * @return FQDN of the stage class
   */
  @NotNull
  String getStageClassName();

}
