package io.papermc.generator.resources;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Transmuter<V, A, R> {

    SliceResult<A, R> examine(V original);

    interface Mutable<V, A, R> extends Transmuter<V, A, R> {

        V apply(V original, SliceResult<A, R> result);
    }
}
