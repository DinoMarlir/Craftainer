curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/docker-compose.yaml -o docker-compose.yaml
mkdir -p ./data/deployments
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/proxy.json -o ./data/deployments/proxy.json
curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/examples/lobby.json -o ./data/deployments/lobby.json

echo "You can start Craftainer with 'docker-compose up -d'."