package io.papermc.testplugin;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.ChatEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.specialty.DialogSpecialty;
import java.net.URI;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.event.connection.common.PlayerConnectionValidateLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;

public final class TestPlugin extends JavaPlugin implements Listener {

    private String clear;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        final TextComponent display = text("Test Link", NamedTextColor.GOLD);
        final URI link = URI.create("https://www.google.com");
        this.getServer().getServerLinks().addLink(display, link);

        // io.papermc.testplugin.brigtests.Registration.registerViaOnEnable(this);
    }

    @EventHandler
    public void onChat(final ChatEvent event) {
        final Dialog dialog = Dialog.create(factory -> factory.empty()
            .dialogBase(DialogBase
                            .builder(text("Test Dialog"))
                            .body(List.of(DialogBody.plainMessage(text("Teleport 10 blocks up"), 100)))
                            .build()
            ).dialogSpecialty(DialogSpecialty.confirmation(
                ActionButton.create(
                    text("TELEPORT", NamedTextColor.GREEN),
                    null,
                    100,
                    DialogAction.staticAction(ClickEvent.runCommand("tp @s ~ ~10 ~"))),
                ActionButton.create(text("CANCEL", NamedTextColor.RED), null, 100, null)
            )));
        event.getPlayer().tempShowDialog(dialog);
    }
       registerCommand("clearchat", new BasicCommand() {
           @Override
           public void execute(final CommandSourceStack commandSourceStack, final String[] args) {
               if (commandSourceStack.getSender() instanceof Player player) {
                   clear = args[0];
                   player.getConnection().enterConfiguration();
               }
           }
       });
    }


    // // First: Check if on login they have any cookies or not. If they do kick them, else let them in.
    // @EventHandler
    // public void onPlayerPreConfigurate(AsyncPlayerPreLoginEvent event) {
    //     byte[] noCookies = event.getPlayerLoginConnection().retrieveCookie(NamespacedKey.fromString("paper:has_cookies")).join();
    //     boolean doYouHaveCookies = noCookies != null;
    //     System.out.println("Does player have cookies? " + doYouHaveCookies);
    //
    //     if (doYouHaveCookies) {
    //         event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("No more cookies for you!"));
    //     }
    // }
    //
    // // On initial configuration, store the cookie
    // @EventHandler
    // public void onPlayerPreConfigurate(PlayerConnectionInitialConfigureEvent event) {
    //     System.out.println("Giving " + event.getConfigurationConnection().getProfile().getName() + " a million cookies!");
    //     event.getConfigurationConnection().storeCookie(NamespacedKey.fromString("paper:has_cookies"), new byte[0]);
    // }
    //
    // // Now during config task, get their cookie state.
    // @EventHandler
    // public void asyncConfigurate(AsyncPlayerConnectionConfigureEvent event) throws InterruptedException {
    //     Thread.sleep(5000); // wait 5 seconds cause i hate u
    //     event.getConfigurationConnection().transfer("127.0.0.1", 25565); // Transfer them to a server, now with our special cookie.
    // }
    //
    // @EventHandler
    // public void loginEvent(PlayerConnectionReconfigurateEvent event) {
    //     if (Boolean.valueOf(clear)) {
    //         event.getConfigurationConnection().clearChat();
    //     }
    //     event.getConfigurationConnection().completeConfiguration();
    // }

    @EventHandler
    public void onPostConnection(PlayerConnectionValidateLoginEvent event) {
        //event.disallow(PostPlayerConnectionLoginEvent.Result.KICK_FULL, Component.text("I HATE U!"));
    }

}
