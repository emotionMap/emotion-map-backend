#!/bin/bash
set -e
KEY="/c/Users/tlsrl/emotionMap/emotionMap/dev-key2.pem"
HOST="ubuntu@43.201.18.125"

run() {
  echo "=== $1 ==="
  ssh -i "$KEY" "$HOST" "sudo mysql emotionMap < /home/ubuntu/migrations/$1"
  echo "--- $1 OK ---"
}

run "2026-09-15-fill-missing-columns.sql"
run "2026-09-13-remove-social-login.sql"
run "2026-09-13-anonymous-nickname.sql"
run "2026-09-13-split-posts-and-comments.sql"

echo "ALL_MIGRATIONS_OK"
