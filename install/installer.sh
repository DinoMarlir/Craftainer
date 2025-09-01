#!/bin/bash

# Colors
GREEN="\e[32m"
RED="\e[31m"
YELLOW="\e[33m"
CYAN="\e[36m"
RESET="\e[0m"

# --- Clear Terminal ---
clear

# --- ASCII Art Logo ---
echo -e "${CYAN}"
cat << "EOF"
 ██████╗██████╗  █████╗ ███████╗████████╗ █████╗ ██╗███╗   ██╗███████╗██████╗
██╔════╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝██╔══██╗██║████╗  ██║██╔════╝██╔══██╗
██║     ██████╔╝███████║█████╗     ██║   ███████║██║██╔██╗ ██║█████╗  ██████╔╝
██║     ██╔══██╗██╔══██║██╔══╝     ██║   ██╔══██║██║██║╚██╗██║██╔══╝  ██╔══██╗
╚██████╗██║  ██║██║  ██║██║        ██║   ██║  ██║██║██║ ╚████║███████╗██║  ██║
 ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝
EOF
echo -e "${RESET}"
echo -e "${GREEN}=== Craftainer Installer ===${RESET}"

echo -e "${GREEN}Installing Dependencies${RESET}"
sudo apt update -y
sudo apt install docker.io curl -y

# --- Install Docker Compose v2 ---
DOCKER_COMPOSE_PATH="/usr/local/bin/docker-compose"
if ! command -v docker-compose &> /dev/null; then
    echo -e "${GREEN}Downloading Docker Compose v2${RESET}"
    sudo curl -sSL "https://github.com/docker/compose/releases/download/v2.23.1/docker-compose-linux-x86_64" -o $DOCKER_COMPOSE_PATH
    sudo chmod +x $DOCKER_COMPOSE_PATH
fi

# --- Checks if Docker Compose is running ---
if docker-compose version &>/dev/null; then
    COMPOSE_CMD="docker-compose"
else
    echo -e "${RED}Docker Compose installation failed${RESET}"
    exit 1
fi
# --- Downloads Craftainer ---
echo -e "${GREEN}Downloading Craftainer${RESET}"
mkdir -p craftainer
cd craftainer || exit 1
curl -sSL https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/docker-compose.yaml -o docker-compose.yaml
mkdir -p ./data/deployments
curl -sSL https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/proxy.json -o ./data/deployments/proxy.json
curl -sSL https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/lobby.json -o ./data/deployments/lobby.json

# ---Downloading required images ---
echo -e "${GREEN}Downloading required images${RESET}"
sudo docker pull itzg/minecraft-server
sudo docker pull itzg/mc-proxy

# ---Starting Craftainer ---
echo -e "${GREEN}Starting Craftainer${RESET}"
sudo $COMPOSE_CMD up -d

echo -e "${GREEN}Craftainer started 🚀${RESET}"
