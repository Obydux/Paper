package io.papermc.paper.dialog;

import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.data.InlinedRegistryBuilderProvider;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import java.util.function.Consumer;
import org.bukkit.Keyed;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a dialog. Can be created during normal server operation via {@link #create(Consumer)}.
 * Can also be created during bootstrap via {@link io.papermc.paper.registry.event.RegistryEvents#DIALOG}.
 */
@ApiStatus.NonExtendable
public interface Dialog extends Keyed {

    /**
     * Creates a new dialog using the provided builder.
     *
     * @param value the builder to use for creating the dialog
     * @return a new dialog instance
     */
    @ApiStatus.Experimental
    static Dialog create(final Consumer<RegistryBuilderFactory<Dialog, ? extends DialogRegistryEntry.Builder>> value) {
        return InlinedRegistryBuilderProvider.instance().createDialog(value);
    }
}
