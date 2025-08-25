curl https://raw.githubusercontent.com/DinoMarlir/Craftainer/refs/heads/master/docker-compose.yaml -o docker-compose.yaml
mkdir -p ./data/deployments
curl https://github.com/DinoMarlir/Craftainer/blob/master/examples/proxy.json -o ./data/deployments/proxy.json
curl https://github.com/DinoMarlir/Craftainer/blob/master/examples/lobby.json -o ./data/deployments/lobby.json

echo "You can start Craftainer with 'docker-compose up -d'."