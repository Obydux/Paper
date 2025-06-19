package io.papermc.paper.registry.data.dialog.input.type;

import java.util.List;
import net.kyori.adventure.text.Component;

record SingleOptionDialogInputConfigImpl(int width, List<SingleOptionDialogInputConfig.OptionEntry> entries, Component label, boolean labelVisible) implements SingleOptionDialogInputConfig {

    SingleOptionDialogInputConfigImpl {
        entries = List.copyOf(entries);
    }
}
