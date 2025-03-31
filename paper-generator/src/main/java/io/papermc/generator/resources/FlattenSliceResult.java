package io.papermc.generator.resources;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FlattenSliceResult<A, R>(@Nullable A added, @Nullable R removed) {
}
