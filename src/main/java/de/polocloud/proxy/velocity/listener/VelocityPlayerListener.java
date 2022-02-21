package de.polocloud.proxy.velocity.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.polocloud.proxy.velocity.VelocityBootstrap;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;

public record VelocityPlayerListener(VelocityBootstrap bootstrap) {

    @Subscribe
    public void handle(final LoginEvent event) {
        final var player = event.getPlayer();

        if (Wrapper.getInstance().thisService().getGroup().isMaintenance()) {
            if (!player.hasPermission("polocloud.maintenance")) {
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(this.bootstrap.getConfiguration().maintenance)));
            }
        }
    }

    @Subscribe
    public void handle(final ServerConnectedEvent event) {
        this.bootstrap.updateTabList(event.getPlayer(), event.getServer().getServerInfo());
    }

}
