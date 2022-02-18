package com.github.wglanzer.rxjava.performance.client.events;

import com.github.wglanzer.rxjava.performance.server.events.StageInvocationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Repository that contains all events received by a single client
 *
 * @author w.glanzer, 18.02.2022
 */
public interface IEventRepository
{

  /**
   * Adds a new listener to this repository
   *
   * @param pListener Listener to add
   */
  void addListener(@NotNull IRepositoryListener pListener);

  /**
   * Removes a listener from this repository
   *
   * @param pListener Listener to remove
   */
  void removeListener(@NotNull IRepositoryListener pListener);

  /**
   * Listener that gets triggered, if something inside the EventRepository changes
   */
  interface IRepositoryListener
  {
    /**
     * Gets called if events where received and added to this repository
     *
     * @param pEvents received events
     */
    void eventsReceived(@NotNull List<StageInvocationEvent> pEvents);
  }

}
