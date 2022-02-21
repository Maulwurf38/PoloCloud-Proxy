package de.polocloud.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerLoginEvent;
import de.polocloud.proxy.ProxyBootstrap;
import de.polocloud.proxy.utils.Configuration;
import de.polocloud.proxy.velocity.commands.VelocityHubCommand;
import de.polocloud.proxy.velocity.listener.VelocityPlayerListener;
import de.polocloud.proxy.velocity.listener.VelocityProxyPingListener;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;

import java.util.concurrent.atomic.AtomicReference;

@Plugin(id = "polocloud-proxy", name = "PoloCloud-Proxy", version = "1.0.0", authors = "BauHD")
public final class VelocityBootstrap implements ProxyBootstrap {

    private final ProxyServer proxyServer;

    private Configuration configuration;

    @Inject
    public VelocityBootstrap(final ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void handle(final ProxyInitializeEvent initializeEvent) {

        this.configuration = this.read();

        this.registerCommands();
        this.registerEvents();

    }

    private void registerCommands() {
        this.proxyServer.getCommandManager().register("hub", new VelocityHubCommand(this), "lobby", "l");
    }

    private void registerEvents() {
        final var eventManager = this.proxyServer.getEventManager();

        eventManager.register(this, new VelocityProxyPingListener(this.configuration));
        eventManager.register(this, new VelocityPlayerListener(this));

        final var eventHandler = CloudAPI.getInstance().getEventHandler();

        eventHandler.registerEvent(CloudPlayerLoginEvent.class, event -> this.updateTabList());
        eventHandler.registerEvent(CloudPlayerDisconnectEvent.class, event -> this.updateTabList());
    }

    public ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void updateTabList() {
        this.proxyServer.getAllPlayers().forEach(this::updateTabList);
    }

    public void updateTabList(final Player player) {
        player.getCurrentServer().ifPresent(serverConnection -> this.updateTabList(player, serverConnection.getServerInfo()));
    }

    public void updateTabList(final Player player, final ServerInfo serverInfo) {
        var header = new StringBuilder();

        for (int i = 0; i < this.configuration.tabListHeader.length; i++) {
            header.append(this.replacePlaceholders(serverInfo, this.configuration.tabListHeader[i]));
            if (this.configuration.tabListHeader.length > i + 1) header.append("\n");
        }

        var footer = new StringBuilder();

        for (int i = 0; i < this.configuration.tabListFooter.length; i++) {
            footer.append(this.replacePlaceholders(serverInfo, this.configuration.tabListFooter[i]));
            if (this.configuration.tabListFooter.length > i + 1) footer.append("\n");
        }

        player.sendPlayerListHeaderAndFooter(Component.text(header.toString()), Component.text(footer.toString()));
    }

    private String replacePlaceholders(final ServerInfo serverInfo, final String s) {
        return s
                .replace("%online%", String.valueOf(CloudAPI.getInstance().getPlayerManager().getOnlineCount()))
                .replace("%max%", String.valueOf(Wrapper.getInstance().thisService().getMaxPlayers()))
                .replace("%server%", serverInfo != null ? serverInfo.getName() : "");
    }

}
