FROM node:26

WORKDIR /app

COPY package*.json ./

RUN npm ci --only=production

COPY src/ ./src/

EXPOSE 7001

CMD ["node", "src/app.js"]