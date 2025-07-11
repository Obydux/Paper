package org.bukkit.plugin.messaging;

import io.papermc.paper.connection.PlayerCommonConnection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A listener for a specific Plugin Channel, which will receive notifications
 * of messages sent from a client.
 */
public interface PluginMessageListener {

    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player Source of the message.
     * @param message The raw message that was sent.
     */
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message);

    /**
     * A method that will be invoked when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param connection Source of the message.
     * @param message The raw message that was sent.
     */
    default void onPluginMessageReceived(@NotNull String channel, @NotNull PlayerCommonConnection connection, byte @NotNull [] message) {

    }
}
