package de.polocloud.proxy.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import de.polocloud.proxy.velocity.VelocityBootstrap;
import net.kyori.adventure.text.Component;

import java.util.Comparator;

public record VelocityHubCommand(VelocityBootstrap bootstrap) implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            CloudAPI.getInstance().getServiceManager().getService(player.getCurrentServer().get().getServerInfo().getName())
                    .ifPresent(cloudService -> {
                        if (cloudService.getGroup().isFallbackGroup()) {
                            player.sendMessage(Component.text(this.bootstrap.getConfiguration().alreadyOnFallback));
                            return;
                        }

                        this.bootstrap.getFallback()
                                .filter(service -> (player.getCurrentServer().isEmpty() ||
                                        !player.getCurrentServer().get().getServerInfo().getName().equals(service.getName())))
                                .min(Comparator.comparing(CloudService::getOnlineCount))
                                .flatMap(service -> this.bootstrap.getProxyServer().getServer(service.getName()))
                                .ifPresentOrElse(
                                        registeredServer -> player.createConnectionRequest(registeredServer).fireAndForget(),
                                        () -> player.sendMessage(Component.text(this.bootstrap.getConfiguration().fallbackNotFound)));
                    });
        }
    }

}
