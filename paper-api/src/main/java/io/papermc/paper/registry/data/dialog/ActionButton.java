package io.papermc.paper.registry.data.dialog;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents an action button in a dialog, which can be used to trigger actions or navigate within the dialog.
 * Action buttons can have labels, tooltips, and associated actions.
 */
public sealed interface ActionButton permits ActionButtonImpl {

    /**
     * Creates a new action button with the specified label, tooltip, width, and action.
     *
     * @param label   the label of the button
     * @param tooltip the tooltip to display when hovering over the button, or null if no tooltip is needed
     * @param width   the width of the button
     * @param action  the action to perform when the button is clicked, or null if no action is associated
     * @return a new ActionButton instance
     */
    static ActionButton create(final Component label, final @Nullable Component tooltip, final int width, final @Nullable DialogAction action) {
        return new ActionButtonImpl(label, tooltip, width, action);
    }

    /**
     * Returns the label of the action button.
     *
     * @return the label of the button
     */
    @Contract(pure = true)
    Component label();

    /**
     * Returns the tooltip of the action button, or null if no tooltip is set.
     *
     * @return the tooltip of the button, or null
     */
    @Contract(pure = true)
    @Nullable Component tooltip();

    /**
     * Returns the width of the action button.
     *
     * @return the width of the button
     */
    @Contract(pure = true)
    int width();

    /**
     * Returns the action associated with this button, or null if no action is associated.
     *
     * @return the action to perform when the button is clicked, or null
     */
    @Contract(pure = true)
    @Nullable DialogAction action();
}
