#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/home/dev/git/HackSync/client"
PORT=3000

export NODE_OPTIONS="--max-old-space-size=2048"
export GENERATE_SOURCEMAP=false

wait_for_cpu() {
  local THRESHOLD=1.5
  local INTERVAL=10
  while true; do
    local LOAD
    LOAD=$(awk '{print $1}' /proc/loadavg)
    if (( $(echo "$LOAD < $THRESHOLD" | bc -l) )); then
      break
    fi
    echo "Current load average is $LOAD; waiting for it to drop below $THRESHOLD..."
    sleep "$INTERVAL"
  done
}

build_app() {
  cd "$APP_DIR"
  npm ci --no-audit --progress=false
  npm run build -- --max-workers=1  # Limit to 1 worker to reduce CPU usage
}

serve_app() {
  cd "$APP_DIR"
  npx serve -s build -l "$PORT"
}

echo "🕒 Checking system load before starting the build..."
wait_for_cpu

echo "🔨 Building the React application..."
build_app

echo "🚀 Starting the server on port $PORT..."
serve_app