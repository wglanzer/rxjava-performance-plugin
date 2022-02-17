package com.github.wglanzer.rxjava.performance.agent.operators;

import com.github.wglanzer.rxjava.performance.agent.stages.*;
import io.reactivex.rxjava3.functions.Function;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.*;

/**
 * @author w.glanzer, 17.02.2022
 */
abstract class AbstractOperatorInterceptor implements IOperatorInterceptor
{

  @Override
  public void onOperatorCreated(@NotNull Object pOperator)
  {
    try
    {
      Field field = pOperator.getClass().getDeclaredField(OPERATOR_CREATOR_FIELDNAME);
      field.setAccessible(true);
      field.set(pOperator, new Exception());
    }
    catch (Throwable e)
    {
      Logger.getLogger(getClass().getName()).log(Level.WARNING, "", e);
    }
  }

  /**
   * This method can be used, if pObject contains a field pFieldName of type Function.
   * It wraps this function into a new one, that tracks its calls into StageInvocations
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
      _Stage stage = new _Stage();
      //noinspection unchecked
      Function<Object, Object> original = (Function<Object, Object>) field.get(pObject);
      field.set(pObject, (Function<Object, Object>) pO -> {
        _StageInvocation inv = new _StageInvocation(stage);

        try
        {
          inv.begin();
          return (original).apply(pO);
        }
        finally
        {
          inv.end();
          //todo do something with this invocation
        }
      });
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Stage-Impl
   */
  private static class _Stage implements IStage
  {
    private final String id;

    public _Stage()
    {
      id = UUID.randomUUID().toString();
    }

    @NotNull
    @Override
    public String getID()
    {
      return id;
    }
  }

  /**
   * StageInvocation-Impl
   */
  private static class _StageInvocation implements IStageInvocation
  {
    private final _Stage stage;
    private long start;
    private long end;

    public _StageInvocation(@NotNull _Stage pStage)
    {
      stage = pStage;
    }

    @NotNull
    @Override
    public IStage getStage()
    {
      return stage;
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
