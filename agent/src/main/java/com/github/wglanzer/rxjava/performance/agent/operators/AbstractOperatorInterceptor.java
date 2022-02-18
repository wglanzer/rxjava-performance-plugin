package com.github.wglanzer.rxjava.performance.agent.operators;

import com.github.wglanzer.rxjava.performance.agent.invocations.*;
import io.reactivex.rxjava3.functions.Function;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author w.glanzer, 17.02.2022
 */
abstract class AbstractOperatorInterceptor implements IOperatorInterceptor
{

  /**
   * This method can be used, if pObject contains a field pFieldName of type Function.
   * It wraps this function into a new one, that tracks its calls into Invocations
   *
   * @param pObject    Object that contains a function
   * @param pFieldName Name of the function variable
   */
  protected void applyWrapperToFunction(@NotNull Object pObject, @NotNull String pFieldName)
  {
    try
    {
      Field field = pObject.getClass().getDeclaredField(pFieldName);
      field.setAccessible(true);
      _Operator operator = new _Operator(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                             .walk(pStream -> pStream
                                                 .skip(4)
                                                 .map(Object::toString)
                                                 .findFirst()
                                                 .orElseGet(() -> UUID.randomUUID().toString())));
      //noinspection unchecked
      Function<Object, Object> original = (Function<Object, Object>) field.get(pObject);
      field.set(pObject, (Function<Object, Object>) pO -> {
        _OperatorInvocation inv = new _OperatorInvocation(operator);

        try
        {
          IOperatorInvocationHandler.IRegistry.INSTANCE.fireAsync(pHandler -> pHandler.handleInvocationStarted(inv));
          inv.begin();
          return (original).apply(pO);
        }
        finally
        {
          inv.end();
          IOperatorInvocationHandler.IRegistry.INSTANCE.fireAsync(pHandler -> pHandler.handleInvocationFinished(inv));
        }
      });
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Operator-Impl
   */
  private static class _Operator implements IOperator
  {
    private final String id;
    private final String name;

    public _Operator(@NotNull String pName)
    {
      id = UUID.randomUUID().toString();
      name = pName;
    }

    @NotNull
    @Override
    public String getID()
    {
      return id;
    }

    @NotNull
    @Override
    public String getName()
    {
      return name;
    }
  }

  /**
   * OperatorInvocation-Impl
   */
  private static class _OperatorInvocation implements IOperatorInvocation
  {
    private final _Operator operator;
    private long start;
    private long end;

    public _OperatorInvocation(@NotNull _Operator pOperator)
    {
      operator = pOperator;
    }

    @NotNull
    @Override
    public IOperator getOperator()
    {
      return operator;
    }

    @Override
    public long getDurationNS()
    {
      if (start == 0 || end == 0)
        return -1;
      return end - start;
    }

    /**
     * Marks this invocation as started
     */
    public void begin()
    {
      if (start == 0 && end == 0)
        start = System.nanoTime();
    }

    /**
     * Marks this invocation as finished
     */
    public void end()
    {
      if (start > 0 && end == 0)
        end = System.nanoTime();
    }
  }

}
