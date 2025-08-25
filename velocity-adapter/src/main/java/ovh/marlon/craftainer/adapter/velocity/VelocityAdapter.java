package ovh.marlon.craftainer.adapter.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.slf4j.Logger;
import ovh.marlon.craftainer.sdk.impl.CraftainerClient;
import ovh.marlon.craftainer.sdk.resources.Container;

import java.net.InetSocketAddress;
import java.util.Objects;

@Plugin(
        id = "craftainer-velocity-adapter",
        name = "Craftainer Velocity Adapter",
        version = "1.0.0",
        description = "Velocity adapter for Craftainer",
        authors = {"Marlon"}
)
public class VelocityAdapter {

    public static final String LABEL_VELOCITY_REGISTER = "craftainer-velocity-register";
    public static final String LABEL_VELOCITY_FALLBACK = "craftainer-velocity-fallback";

    private final ProxyServer server;
    private final Logger logger;
    private final CraftainerClient client = CraftainerClient.Companion.createUsingSocket();

    @Inject
    public VelocityAdapter(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        server.getAllServers().forEach(s -> server.unregisterServer(s.getServerInfo()));
        logger.info("Removed all existing servers from Velocity.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Daemon thread to periodically check for new containers
        new Watcher(
                server,
                client,
                logger
        ).start();
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        event.setInitialServer(
                server.getServer(
                        client.getContainers().stream()
                                .filter(container -> container.getLabels().containsKey(LABEL_VELOCITY_FALLBACK) && container.getStatus() == Container.ContainerStatus.HEALTHY)
                                .findFirst()
                                .map(Container::getName)
                                .orElse(null)
                ).orElse(null)
        );
    }
}
