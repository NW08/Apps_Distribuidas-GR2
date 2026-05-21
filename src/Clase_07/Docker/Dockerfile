FROM node:lts-alpine
WORKDIR /app
COPY . .
RUN npm install --production CMD ["node", "src/index.js"]
EXPOSE 7000