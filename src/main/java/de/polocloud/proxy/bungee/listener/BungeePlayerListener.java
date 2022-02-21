package de.polocloud.proxy.bungee.listener;

import de.polocloud.proxy.bungee.BungeeBootstrap;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public record BungeePlayerListener(BungeeBootstrap bootstrap) implements Listener {

    @EventHandler
    public void handle(final PostLoginEvent event) {
        final var player = event.getPlayer();

        if (Wrapper.getInstance().thisService().getGroup().isMaintenance()) {
            if (!player.hasPermission("polocloud.maintenance")) {
                player.disconnect(new TextComponent(this.bootstrap.getConfiguration().maintenance));
            }
        }

    }

    @EventHandler
    public void handle(final ServerSwitchEvent event) {
        this.bootstrap.updateTabList(event.getPlayer());
    }

}
