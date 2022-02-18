package com.github.wglanzer.rxjava.performance.agent.stages;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * This handler cares about notifying others about a happened stage invocation
 *
 * @author w.glanzer, 17.02.2022
 * @see IStage
 * @see IStageInvocation
 */
public interface IStageInvocationHandler
{

  /**
   * Gets called if an invocation of a stage started
   *
   * @param pInvocation the invocation
   */
  void handleInvocationStarted(@NotNull IStageInvocation pInvocation);

  /**
   * Gets called if an invocation of a stage finished
   *
   * @param pInvocation the invocation
   */
  void handleInvocationFinished(@NotNull IStageInvocation pInvocation);

  /**
   * Registry for all InvocationHandlers
   *
   * @see IStageInvocationHandler
   */
  interface IRegistry
  {
    /**
     * Default Instance
     */
    IRegistry INSTANCE = new AsyncStageInvocationHandlerRegistry();

    /**
     * Adds a new handler to this registry.
     * Does nothing if this registry already contained the handler
     *
     * @param pHandler handler to add
     */
    void addHandler(@NotNull IStageInvocationHandler pHandler);

    /**
     * Removes the given handler from the registry.
     * Does nothing if this handler was not added to the registry
     *
     * @param pHandler handler to remove
     */
    void removeHandler(@NotNull IStageInvocationHandler pHandler);

    /**
     * Fires events to all currently known invocation handlers asynchronously
     *
     * @param pHandlerConsumer consumer that gets all handlers, async
     */
    void fireAsync(@NotNull Consumer<IStageInvocationHandler> pHandlerConsumer);
  }

}
