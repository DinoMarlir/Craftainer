# 🐳 Craftainer

Craftainer is a powerful tool designed to simplify the deployment and management of Minecraft networks using Docker.

---

## 🚀 Getting Started

### Prerequisites

- **Memory:** 4GB RAM or more recommended
- **Architecture:** amd64 (arm64 is experimental)
- **Operating System:** Linux-based
- **Permissions:** Root privileges to install the dependencies (Only when you use the Installer)

---

### Installation on Debian/Ubuntu

For a fast and straightforward setup, use our official installation script. Ensure you have `sudo` privileges.

> [!CAUTION]
> This script is only compatible with Debian-based distributions.

```sh
curl -fsSL https://gist.githubusercontent.com/getSono/e7fa4cb742a4a28ccd4e0ceb333f0d65/raw/d1fc974656c2cccc0dc75e02e8dcb7a1f3c68c58/install_craftainer.sh | sudo bash
```

---

### Installation on Other Linux Distributions

Follow these manual setup steps:

```sh
mkdir craftainer
cd craftainer
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/docker-compose.yaml -o docker-compose.yaml
mkdir -p ./data/deployments
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/proxy.json -o ./data/deployments/proxy.json
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/lobby.json -o ./data/deployments/lobby.json
```

> [!CAUTION]
> You are responsible for installing Docker and Docker Compose.

Download the required Minecraft Docker images:

```sh
docker pull itzg/minecraft-server:latest
docker pull itzg/mc-proxy:latest
```

Start Craftainer with:

```sh
docker compose up -d
```

> [!CAUTION]
> On older systems, use `docker-compose up -d` instead.
---
