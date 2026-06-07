# Stage 1: Build the React application
FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve static files with Nginx
FROM nginx:1.27-alpine
# Copy built assets
COPY --from=builder /app/dist /usr/share/nginx/html
# Copy custom Nginx configuration (gzip, cache headers, SPA fallback)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
