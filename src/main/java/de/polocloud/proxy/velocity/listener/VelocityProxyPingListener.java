package de.polocloud.proxy.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.polocloud.proxy.utils.Configuration;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;

public record VelocityProxyPingListener(Configuration configuration) {

    @Subscribe
    public void handle(final ProxyPingEvent event) {
        final var serverPing = event.getPing().asBuilder();

        if (Wrapper.getInstance().thisService().getGroup().isMaintenance()) {
            serverPing
                    .description(Component.text(this.configuration.maintenanceMotdLine1 + "\n" + this.configuration.maintenanceMotdLine2))
                    .version(new ServerPing.Version(-1, this.configuration.maintenanceVersion));
        } else {
            serverPing.description(Component.text(this.configuration.motdLine1 + "\n" + this.configuration.motdLine2));
        }

        event.setPing(serverPing.build());
    }

}
