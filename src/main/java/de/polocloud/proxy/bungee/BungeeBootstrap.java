package de.polocloud.proxy.bungee;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerLoginEvent;
import de.polocloud.proxy.ProxyBootstrap;
import de.polocloud.proxy.bungee.commands.BungeeHubCommand;
import de.polocloud.proxy.bungee.listener.BungeePlayerListener;
import de.polocloud.proxy.bungee.listener.BungeeProxyPingListener;
import de.polocloud.proxy.utils.Configuration;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeBootstrap extends Plugin implements ProxyBootstrap {

    private Configuration configuration;

    @Override
    public void onEnable() {

        this.configuration = this.read();

        this.registerCommands();
        this.registerEvents();

    }

    private void registerCommands() {
        this.getProxy().getPluginManager().registerCommand(this, new BungeeHubCommand(this));
    }

    private void registerEvents() {
        final var pluginManager = this.getProxy().getPluginManager();

        pluginManager.registerListener(this, new BungeeProxyPingListener(this.configuration));
        pluginManager.registerListener(this, new BungeePlayerListener(this));

        final var eventHandler = CloudAPI.getInstance().getEventHandler();

        eventHandler.registerEvent(CloudPlayerLoginEvent.class, event -> this.updateTabList());
        eventHandler.registerEvent(CloudPlayerDisconnectEvent.class, event -> this.updateTabList());
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void updateTabList() {
        this.getProxy().getPlayers().forEach(this::updateTabList);
    }

    public void updateTabList(final ProxiedPlayer player) {
        var header = new StringBuilder();

        for (int i = 0; i < this.configuration.tabListHeader.length; i++) {
            header.append(this.replacePlaceholders(player, this.configuration.tabListHeader[i]));
            if (this.configuration.tabListHeader.length > i + 1) header.append("\n");
        }

        var footer = new StringBuilder();

        for (int i = 0; i < this.configuration.tabListFooter.length; i++) {
            footer.append(this.replacePlaceholders(player, this.configuration.tabListFooter[i]));
            if (this.configuration.tabListFooter.length > i + 1) footer.append("\n");
        }

        player.setTabHeader(TextComponent.fromLegacyText(header.toString()), TextComponent.fromLegacyText(footer.toString()));
    }

    private String replacePlaceholders(final ProxiedPlayer player, final String s) {
        return s
                .replace("%online%", String.valueOf(CloudAPI.getInstance().getPlayerManager().getOnlineCount()))
                .replace("%max%", String.valueOf(Wrapper.getInstance().thisService().getMaxPlayers()))
                .replace("%server%", player.getServer() != null ? player.getServer().getInfo().getName() : "");
    }

}
