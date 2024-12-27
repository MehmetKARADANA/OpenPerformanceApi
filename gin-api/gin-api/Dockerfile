FROM golang:1.23-alpine

# 2. Gerekli bağımlılıkları yükleyelim.
RUN apk add --no-cache git

# 3. Çalışma dizini oluşturuyoruz ve uygulama kodunu bu dizine kopyalıyoruz.
WORKDIR /app

# 4. Go modüllerini kullanarak bağımlılıkları indiriyoruz.
COPY go.mod go.sum ./
RUN go mod download

# 5. Tüm proje dosyalarını kopyalıyoruz.
COPY . .

# 6. Go uygulamasını derliyoruz.
RUN go build -o server .

# 7. Portu açıyoruz.
EXPOSE 8083

# 8. Uygulamayı başlatıyoruz.
CMD ["./server"]
