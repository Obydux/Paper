package io.papermc.paper.registry.data.dialog.input.type;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A configuration for a number range dialog input.
 */
public sealed interface NumberRangeDialogInputConfig extends DialogInputConfig permits NumberRangeDialogInputConfigImpl {

    /**
     * The width of the input.
     *
     * @return the width
     */
    @Contract(pure = true)
    int width();

    /**
     * The label for the input.
     *
     * @return the label component
     */
    @Contract(pure = true)
    Component label();

    /**
     * The format for the label, which can be a translation key or a format string.
     * <p>Example: {@code "%s: %s"} or {@code "options.generic_value"}</p>
     *
     * @return the label format
     */
    @Contract(pure = true)
    String labelFormat();

    /**
     * The start of the range.
     *
     * @return the start value
     */
    @Contract(pure = true)
    float start();

    /**
     * The end of the range.
     *
     * @return the end value
     */
    @Contract(pure = true)
    float end();

    /**
     * The initial value of the input, or null if not set.
     *
     * @return the initial value, or null
     */
    @Contract(pure = true)
    @Nullable Float initial();

    /**
     * The step size for the input, or null if not set.
     *
     * @return the step size, or null
     */
    @Contract(pure = true)
    @Nullable Float step();
}
