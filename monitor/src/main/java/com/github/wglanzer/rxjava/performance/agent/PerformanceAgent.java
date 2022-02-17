package com.github.wglanzer.rxjava.performance.agent;

import com.github.wglanzer.rxjava.performance.agent.operators.IOperatorInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
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
                  .defineField(IOperatorInterceptor.OPERATOR_CREATOR_FIELDNAME, Throwable.class)
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
          })

          //onStageCreated
          .type(ElementMatchers.named(interceptor.getStageClassName()))
          .transform((builder, typeDescription, classLoader, module) -> {
            try
            {
              return builder
                  .constructor(ElementMatchers.any())
                  .intercept(SuperMethodCall.INSTANCE.andThen(MethodCall.invoke(interceptor.getOnStageCreatedMethod())
                                                                  .on(interceptor)
                                                                  .withThis()));
            }
            catch (Exception e)
            {
              _LOGGER.log(Level.WARNING, "Failed to apply stage interceptor " + interceptor.getClass().getName(), e);
              return builder;
            }
          });

      _LOGGER.info("Added RxJava operator interceptor: " + interceptor.getClass().getName());
    }

    agentBuilder.installOn(inst);
  }

}
