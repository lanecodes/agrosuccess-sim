package me.ajlane.reporting;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Base for developing {@code *Reporter} classes.
 *
 * <p>
 * Once the {@code *Reporter} has <emph>reported</emph> an object, e.g. by iterating over it, the
 * reporter 'forgets' about the object and responsibility for handling the returned data falls to
 * the client code. This has the advantage of allowing the {@code *Reporter} to encapsulate the
 * process of recording data and provide a convenient interface for another class to retrieve it
 * without client code needing to remove references to the returned objects.
 * </p>
 *
 * @implSpec The intended way for subclasses to use features of this implementation is for them to
 *           add methods to add and retrieve individual objects to and from the
 *           {@code internalQueue}. The names of these methods and types of the objects will be
 *           specific to the problem domain. Specifically these methods should call {@code add} and
 *           {@code poll} on the {@code internalQueue} instance variable.
 * @author Andrew Lane
 * @param <T>
 */
public abstract class AbstractReporter<T> implements Iterable<T> {

  protected final Queue<T> internalQueue;

  public AbstractReporter() {
    this.internalQueue = new LinkedList<>();
  }

  @Override
  public final Iterator<T> iterator() {
    return new ConsumingIterator();
  }

  /**
   * Provides an iterator that consumes the internal queue.
   *
   * Iterating over a plain {@link Queue} using a for-each loop or
   * {@link Iterable#forEach(java.util.function.Consumer)} leaves the queue intact. This iterator
   * removes items from the top of the queue and returns them during the course of iteration.
   */
  private final class ConsumingIterator implements Iterator<T> {

    @Override
    public boolean hasNext() {
      if (internalQueue.size() > 0) {
        return true;
      } else {
        return false;
      }
    }

    @Override
    public T next() {
      return internalQueue.poll();
    }

  }
}
