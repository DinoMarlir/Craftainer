# 🐳 Craftainer
A tool to build Minecraft networks on top of Docker.

## 🚀 Get Started

### Debian/Ubuntu
Use our install script for a quick setup. Make sure you have sudo privileges.

> [!CAUTION]
> This script only works on Debian-based distributions and amd64 architecture.
```sh
curl -fsSL https://url.wolfjulian.de/install-craftainer | sudo bash
```

### Other Distributions

Manual installation steps:
```sh
mkdir craftainer
cd craftainer
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/docker-compose.yaml -o docker-compose.yaml
mkdir -p ./data/deployments
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/proxy.json -o ./data/deployments/proxy.json
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/lobby.json -o ./data/deployments/lobby.json
```
> [!CAUTION]
> You need to install Docker and Docker Compose yourself.

Install the Minecraft Docker images:
```sh
docker pull itzg/minecraft-server:latest
docker pull itzg/mc-proxy:latest
```
