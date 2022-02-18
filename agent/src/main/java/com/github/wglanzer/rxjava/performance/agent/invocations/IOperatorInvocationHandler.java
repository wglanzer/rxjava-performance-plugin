package com.github.wglanzer.rxjava.performance.agent.invocations;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * This handler cares about notifying others about a happened operator invocation
 *
 * @author w.glanzer, 17.02.2022
 * @see IOperator
 * @see IOperatorInvocation
 */
public interface IOperatorInvocationHandler
{

  /**
   * Gets called if an invocation of an operator started
   *
   * @param pInvocation the invocation
   */
  void handleInvocationStarted(@NotNull IOperatorInvocation pInvocation);

  /**
   * Gets called if an invocation of an operator finished
   *
   * @param pInvocation the invocation
   */
  void handleInvocationFinished(@NotNull IOperatorInvocation pInvocation);

  /**
   * Registry for all InvocationHandlers
   *
   * @see IOperatorInvocationHandler
   */
  interface IRegistry
  {
    /**
     * Default Instance
     */
    IRegistry INSTANCE = new AsyncOperatorInvocationHandlerRegistry();

    /**
     * Adds a new handler to this registry.
     * Does nothing if this registry already contained the handler
     *
     * @param pHandler handler to add
     */
    void addHandler(@NotNull IOperatorInvocationHandler pHandler);

    /**
     * Removes the given handler from the registry.
     * Does nothing if this handler was not added to the registry
     *
     * @param pHandler handler to remove
     */
    void removeHandler(@NotNull IOperatorInvocationHandler pHandler);

    /**
     * Fires events to all currently known invocation handlers asynchronously
     *
     * @param pHandlerConsumer consumer that gets all handlers, async
     */
    void fireAsync(@NotNull Consumer<IOperatorInvocationHandler> pHandlerConsumer);
  }

}
