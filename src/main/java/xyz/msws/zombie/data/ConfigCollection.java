package xyz.msws.zombie.data;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a {@link Collection}, with the added usage of getType
 * This supports grabbing the type that this collection contains
 *
 * @param <E> The type contained
 */
public record ConfigCollection<E>(Collection<E> col, Class<E> type) implements Collection<E> {

    @Override
    public int size() {
        return col.size();
    }

    @Override
    public boolean isEmpty() {
        return col.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return col.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return col.iterator();
    }

    @Override
    public Object[] toArray() {
        return col.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return col.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return col.add(e);
    }

    public boolean addObject(Object e) {
        return col.add((E) e);
    }

    @Override
    public boolean remove(Object o) {
        return col.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return col.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return col.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return col.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return col.retainAll(c);
    }

    @Override
    public void clear() {
        col.clear();
    }
}
