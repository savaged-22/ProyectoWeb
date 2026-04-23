# Stage 1: Build Angular app
FROM node:20-alpine AS build
WORKDIR /app

# Instalar dependencias
COPY package.json package-lock.json ./
RUN npm ci

# Copiar el código fuente y compilar
COPY . .
RUN npm run build

# Stage 2: Serve with Node.js (SSR)
FROM node:20-alpine
WORKDIR /app

# Copiar el build compilado y package.json para ejecutar el script
COPY --from=build /app/dist/frontend_final ./dist/frontend_final
COPY --from=build /app/package.json ./package.json

# Exponer el puerto por defecto del server SSR de Angular (4000)
EXPOSE 4000

# Ejecutar el servidor Node
CMD ["npm", "run", "serve:ssr:frontend_final"]
