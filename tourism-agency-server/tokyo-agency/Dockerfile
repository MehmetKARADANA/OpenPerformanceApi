# Node.js'in son sürümünü baz al
FROM node:18

# Uygulamanın çalışacağı dizini belirle
WORKDIR /usr/src/app

# package.json ve package-lock.json dosyalarını Docker container'ına kopyala
COPY package*.json ./

# Bağımlılıkları yükle
RUN npm install

# Uygulamanın tüm dosyalarını container'a kopyala
COPY . .

# Uygulamanın çalışacağı portu belirle
EXPOSE 3003

# Uygulamayı başlat
CMD [ "node", "tokyo-agency.js" ]
