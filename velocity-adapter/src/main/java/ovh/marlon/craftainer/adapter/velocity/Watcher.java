package ovh.marlon.craftainer.adapter.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.slf4j.Logger;
import ovh.marlon.craftainer.sdk.impl.CraftainerClient;

import java.net.InetSocketAddress;
import java.util.Objects;

public class Watcher extends Thread {

    private ProxyServer server;
    private CraftainerClient client;
    private Logger logger;

    public Watcher(ProxyServer server, CraftainerClient client, Logger logger) {
        this.server = server;
        this.client = client;
        this.logger = logger;
        setDaemon(true);
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            client.getContainers().stream().filter(containerImageContainer -> containerImageContainer.getLabels().containsKey(VelocityAdapter.LABEL_VELOCITY_REGISTER)/* && containerImageContainer.getStatus()  == Container.ContainerStatus.HEALTHY*/).forEach(containerImageContainer -> {
                String serverName = containerImageContainer.getLabels().getOrDefault("craftainer-velocity-server-name", Objects.requireNonNull(containerImageContainer.getName()));
                if (server.getServer(serverName).isEmpty()) {
                    String address = client.getClient().inspectContainerCmd(containerImageContainer.getId()).exec().getNetworkSettings().getIpAddress();
                    server.registerServer(new ServerInfo(serverName, InetSocketAddress.createUnresolved(address, 25565)));
                    logger.info("Registered server {} at {}:{}", serverName, address, 25565);
                }
            });
        }
    }
}
