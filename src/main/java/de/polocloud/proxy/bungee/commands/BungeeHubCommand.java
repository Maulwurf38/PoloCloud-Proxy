package de.polocloud.proxy.bungee.commands;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import de.polocloud.proxy.bungee.BungeeBootstrap;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Comparator;

public final class BungeeHubCommand extends Command {

    private final BungeeBootstrap bootstrap;

    public BungeeHubCommand(final BungeeBootstrap bootstrap) {
        super("hub", null, "lobby", "l");
        this.bootstrap = bootstrap;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer player) {
            CloudAPI.getInstance().getServiceManager().getService(player.getServer().getInfo().getName())
                    .ifPresent(cloudService -> {
                        if (cloudService.getGroup().isFallbackGroup()) {
                            player.sendMessage(new TextComponent(this.bootstrap.getConfiguration().alreadyOnFallback));
                            return;
                        }

                        this.bootstrap.getFallback()
                                .filter(service -> (player.getServer() == null || !player.getServer().getInfo().getName().equals(service.getName())))
                                .min(Comparator.comparing(CloudService::getOnlineCount))
                                .map(service -> ProxyServer.getInstance().getServerInfo(service.getName()))
                                .ifPresentOrElse(
                                        player::connect,
                                        () -> commandSender.sendMessage(new TextComponent(this.bootstrap.getConfiguration().fallbackNotFound)));
                    });
        }
    }

}
