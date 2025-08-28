# 🐳 Craftainer

Craftainer is a powerful tool designed to simplify the deployment and management of Minecraft networks using Docker.

---

## 🚀 Getting Started

### Prerequisites

- **Memory:** 1GB RAM or more recommended
- **Architecture:** amd64 (arm64 is experimental)
- **Operating System:** Linux-based
- **Permissions:** Root or sudo privileges

---

### Installation on Debian/Ubuntu

For a fast and straightforward setup, use our official installation script. Ensure you have `sudo` privileges.

> [!CAUTION]
> This script is only compatible with Debian-based distributions.

```sh
curl -fsSL https://url.wolfjulian.de/install-craftainer | sudo bash
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
