package com.github.wglanzer.rxjava.performance.agent;

import com.github.wglanzer.rxjava.performance.agent.operators.IOperatorInterceptor;
import com.github.wglanzer.rxjava.performance.agent.server.PerformanceAgentServer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.jetbrains.annotations.*;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.logging.*;

/**
 * This class represents the main entry point for the agent
 *
 * @author w.glanzer, 13.02.2022
 */
public class PerformanceAgent
{

  private static final Logger _LOGGER = Logger.getLogger(PerformanceAgent.class.getName());

  /**
   * Gets called automatically, if this jar is defined as javaagent
   *
   * @param agentArgs arguments to this agent
   * @param inst      current instrumentation instance
   */
  public static void premain(String agentArgs, Instrumentation inst)
  {
    _addInterceptors(inst);
    _startServer(agentArgs);
  }

  /**
   * Adds all interceptors into the given instrumentation
   *
   * @param pInstrumentation Object to add interceptors to
   */
  private static void _addInterceptors(@NotNull Instrumentation pInstrumentation)
  {
    AgentBuilder agentBuilder = new AgentBuilder.Default();

    // Apply interceptors
    for (IOperatorInterceptor interceptor : IOperatorInterceptor.OPERATORS)
    {
      agentBuilder = agentBuilder
          // onOperatorCreated
          .type(ElementMatchers.named(interceptor.getOperatorClassName()))
          .transform((builder, typeDescription, classLoader, module) -> {
            try
            {
              return builder
                  .constructor(ElementMatchers.any())
                  .intercept(SuperMethodCall.INSTANCE.andThen(MethodCall.invoke(interceptor.getOnOperatorCreatedMethod())
                                                                  .on(interceptor)
                                                                  .withThis()));
            }
            catch (Exception e)
            {
              _LOGGER.log(Level.WARNING, "Failed to apply operator interceptor " + interceptor.getClass().getName(), e);
              return builder;
            }
          });

      _LOGGER.info("Added RxJava operator interceptor: " + interceptor.getClass().getName());
    }

    agentBuilder.installOn(pInstrumentation);
  }

  /**
   * Starts the performance agent server and accept connections from outside
   *
   * @param pAgentArgs arguments to this agent
   */
  private static void _startServer(@Nullable String pAgentArgs)
  {
    try
    {
      if (pAgentArgs == null || pAgentArgs.isBlank())
      {
        _LOGGER.info("Port information is missing, performance agent server not started");
        return;
      }

      // Start Server
      new PerformanceAgentServer(Integer.parseInt(Objects.requireNonNull(pAgentArgs))).start();
    }
    catch (Exception e)
    {
      _LOGGER.log(Level.WARNING, "Failed to start performance agent server", e);
    }
  }

}
