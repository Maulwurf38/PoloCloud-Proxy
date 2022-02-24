package de.polocloud.proxy;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.json.Document;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceState;
import de.polocloud.proxy.utils.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public interface ProxyBootstrap {

    default Stream<CloudService> getFallback() {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
                .filter(service -> service.getState().equals(ServiceState.ONLINE))
                .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
                .filter(service -> service.getGroup().isFallbackGroup());
    }

    default Configuration read() {
        final var file = new File("plugins/PoloCloud-Proxy/config.json");
        final var document = new Document();
        if (file.exists()) {
                return document.read(file).get(Configuration.class);
        }
        final var configuration = new Configuration();
        try {
            if (file.getParentFile().mkdirs()) {
                if (file.createNewFile()) document.setJsonObject(configuration).write(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

}
