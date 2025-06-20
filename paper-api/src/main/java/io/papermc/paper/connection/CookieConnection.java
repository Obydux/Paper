package io.papermc.paper.connection;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import java.util.concurrent.CompletableFuture;

@NullMarked
public interface CookieConnection extends PlayerConnection {

    /**
     * Retrieves a cookie from this connection.
     *
     * @param key the key identifying the cookie
     * @return a {@link CompletableFuture} that will be completed when the
     * Cookie response is received or otherwise available. If the cookie is not
     * set in the client, the {@link CompletableFuture} will complete with a
     * null value.
     */
    CompletableFuture<byte[]> retrieveCookie(NamespacedKey key);

    /**
     * Stores a cookie in this player's client.
     *
     * @param key the key identifying the cookie
     * @param value the data to store in the cookie
     * @throws IllegalStateException if a cookie cannot be stored at this time
     */
    void storeCookie(NamespacedKey key, byte[] value);
}
