#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SONAR_URL="http://localhost:9000"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.sonar.yml"
COMPOSE_PROJECT="lulo-sonar"
NETWORK="${COMPOSE_PROJECT}_sonar-net"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
M2_CACHE="$HOME/.m2"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# ── 1. Ajustar vm.max_map_count (requerido por Elasticsearch dentro de SonarQube) ──
info "Verificando parámetros del sistema..."
CURRENT_MAP=$(sysctl -n vm.max_map_count 2>/dev/null || echo "0")
if [ "$CURRENT_MAP" -lt 524288 ]; then
  warn "vm.max_map_count=$CURRENT_MAP es bajo. Ajustando a 524288 (requiere sudo)..."
  sudo sysctl -w vm.max_map_count=524288
fi

# ── 2. Iniciar SonarQube ──────────────────────────────────────────────────────
info "Iniciando SonarQube con Docker Compose..."
docker compose -f "$COMPOSE_FILE" -p "$COMPOSE_PROJECT" up -d
info "Contenedores iniciados. Esperando a que SonarQube esté listo (puede tardar 2-3 minutos)..."

# ── 3. Esperar a que SonarQube responda ───────────────────────────────────────
RETRIES=72  # 6 minutos máximo
while [ "$RETRIES" -gt 0 ]; do
  STATUS=$(curl -s "$SONAR_URL/api/system/status" 2>/dev/null \
    | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "")
  if [ "$STATUS" = "UP" ]; then
    echo ""
    info "SonarQube está listo en $SONAR_URL"
    break
  fi
  RETRIES=$((RETRIES - 1))
  if [ "$RETRIES" -eq 0 ]; then
    echo ""
    error "SonarQube no arrancó a tiempo. Revisa los logs:\n  docker compose -f $COMPOSE_FILE -p $COMPOSE_PROJECT logs sonarqube"
  fi
  printf "."
  sleep 5
done

# ── 4. Crear token de análisis via API ────────────────────────────────────────
info "Generando token de análisis..."

# Intentar con admin/admin primero
TOKEN_JSON=$(curl -s -u admin:admin -X POST \
  "$SONAR_URL/api/user_tokens/generate" \
  -d "name=lulo-scan-$(date +%s)" 2>/dev/null || echo "")
TOKEN=$(echo "$TOKEN_JSON" | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")

# Si falló, pedir credenciales reales
if [ -z "$TOKEN" ]; then
  warn "No se pudo autenticar con admin/admin (la contraseña fue cambiada)."
  printf "Ingresa tu contraseña de SonarQube (usuario admin): "
  read -rs SONAR_PASS
  echo ""
  TOKEN_JSON=$(curl -s -u "admin:${SONAR_PASS}" -X POST \
    "$SONAR_URL/api/user_tokens/generate" \
    -d "name=lulo-scan-$(date +%s)" 2>/dev/null || echo "")
  TOKEN=$(echo "$TOKEN_JSON" | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")
fi

# Si aún no hay token, pedir el token directamente
if [ -z "$TOKEN" ]; then
  warn "No se pudo generar el token automáticamente."
  echo "  1. Abre $SONAR_URL/account/security en el navegador"
  echo "  2. En 'Generate Tokens' ingresa un nombre, tipo 'Global Analysis Token' y haz clic en Generate"
  echo "  3. Copia el token generado"
  printf "Ingresa el token: "
  read -rs TOKEN
  echo ""
  [ -z "$TOKEN" ] && error "Se requiere un token para continuar."
fi

info "Token obtenido correctamente."

# ── 5. Análisis del backend (Java / Spring Boot) ──────────────────────────────
info "Analizando backend (Java 21 / Spring Boot)..."
docker run --rm \
  --network "$NETWORK" \
  -v "$BACKEND_DIR:/app" \
  -v "$M2_CACHE:/root/.m2" \
  -w /app \
  maven:3.9.6-eclipse-temurin-21-alpine \
  mvn -B verify sonar:sonar \
    -Dsonar.host.url=http://sonarqube:9000 \
    -Dsonar.token="$TOKEN" \
    -Dsonar.projectKey=lulo-backend \
    -Dsonar.projectName="LULO Backend" \
    -Dsonar.java.source=21 \
    -Dsonar.sources=src/main/java \
    -Dsonar.exclusions="**/generated/**" \
    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

info "Análisis del backend completado."

# ── 6. Análisis del frontend (Angular / TypeScript) ───────────────────────────
if [ -f "$FRONTEND_DIR/sonar-project.properties" ]; then
  info "Analizando frontend (Angular / TypeScript)..."
  docker run --rm \
    --network "$NETWORK" \
    -v "$FRONTEND_DIR:/usr/src" \
    sonarsource/sonar-scanner-cli:latest \
    sonar-scanner \
      -Dsonar.host.url=http://sonarqube:9000 \
      -Dsonar.token="$TOKEN"
  info "Análisis del frontend completado."
fi

# ── 7. Resultado ──────────────────────────────────────────────────────────────
echo ""
echo "════════════════════════════════════════════════════════"
info "Análisis finalizado. Abre el dashboard en tu navegador:"
echo ""
echo "  Backend:  $SONAR_URL/dashboard?id=lulo-backend"
echo "  Frontend: $SONAR_URL/dashboard?id=lulo-frontend"
echo ""
echo "  Credenciales por defecto: admin / admin"
echo "════════════════════════════════════════════════════════"
echo ""
info "Para detener SonarQube cuando termines:"
echo "  docker compose -f $COMPOSE_FILE -p $COMPOSE_PROJECT down"
