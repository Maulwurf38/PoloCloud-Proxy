package de.polocloud.proxy.bungee.listener;

import de.polocloud.proxy.utils.Configuration;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public record BungeeProxyPingListener(Configuration configuration) implements Listener {

    @EventHandler
    public void handle(final ProxyPingEvent event) {
        final var serverPing = event.getResponse();

        if (Wrapper.getInstance().thisService().getGroup().isMaintenance()) {
            serverPing.setDescriptionComponent(new TextComponent(
                    TextComponent.fromLegacyText(this.configuration.maintenanceMotdLine1
                            + "\n" + this.configuration.maintenanceMotdLine2)));
            serverPing.setVersion(new ServerPing.Protocol(this.configuration.maintenanceVersion, -1));
        } else {
            serverPing.setDescriptionComponent(new TextComponent(
                    TextComponent.fromLegacyText(this.configuration.motdLine1
                            + "\n" + this.configuration.motdLine2)));
        }

        event.setResponse(serverPing);
    }

}
