# SportHub Court Service

Court Service, SportHub mikroservis mimarisinde spor sahalarının yönetimini sağlayan servistir.

## 🏗️ Mimari

- **Port**: 8083
- **Framework**: Spring Boot 3.5.5
- **Database**: MySQL (sporthub_courts)
- **Cache**: Redis
- **Service Discovery**: Eureka Client
- **Java Version**: 17

## 🚀 Özellikler

### ✅ Saha Yönetimi
- Saha ekleme/güncelleme/silme
- Saha detayları (fiyat, özellikler, fotoğraflar)
- Saha durumu (aktif/pasif)

### ✅ Saha Türleri
- Tenis
- Basketbol
- Futbol
- Squash
- Voleybol
- Badminton
- Masa Tenisi

### ✅ Arama ve Filtreleme
- İsme göre arama
- Tür, fiyat, özellikler ile filtreleme
- Sayfalama ve sıralama
- Amenity bazlı filtreleme

### ✅ Cache Sistemi
- Redis ile hızlı erişim
- Popüler sahaları cache'leme
- Saha detaylarını cache'leme
- 1 saat TTL

### ✅ İstatistikler
- Saha türüne göre sayım
- Ortalama puanlama
- Puan güncelleme

## 🗄️ Veritabanı Yapısı

### Tables
- `courts` - Saha bilgileri
- `court_images` - Saha fotoğrafları
- `court_amenities` - Saha olanakları

### Örnek Veri
```sql
-- Saha oluşturma
INSERT INTO courts (name, description, court_type, price_per_hour, is_indoor, max_players, court_size, surface_type, lighting_available, parking_available, shower_available, rating, total_reviews, is_active, created_at, updated_at) 
VALUES ('Tennis Court 1', 'Professional tennis court', 'TENNIS', 150.00, false, 4, '23.77m x 10.97m', 'Hard Court', true, true, true, 0.0, 0, true, NOW(), NOW());
```

## 🔌 API Endpoints

### Saha Yönetimi
```
POST   /api/courts                    # Saha ekle
GET    /api/courts                    # Tüm sahalar
GET    /api/courts/{id}               # Saha detayı
PUT    /api/courts/{id}               # Saha güncelle
DELETE /api/courts/{id}               # Saha sil
POST   /api/courts/{id}/deactivate    # Saha deaktif et
POST   /api/courts/{id}/activate      # Saha aktif et
```

### Saha Listeleme
```
GET    /api/courts/type/{courtType}   # Tür bazlı sahalar
GET    /api/courts/active             # Aktif sahalar
GET    /api/courts/popular            # Popüler sahalar
```

### Arama ve Filtreleme
```
POST   /api/courts/search             # Gelişmiş arama
GET    /api/courts/search             # Terim bazlı arama
GET    /api/courts/search/price       # Fiyat bazlı arama
GET    /api/courts/search/amenities   # Özellik bazlı arama
```

### İstatistikler
```
GET    /api/courts/stats/count/{type} # Tür bazlı sayım
GET    /api/courts/stats/rating/{type}# Tür bazlı ortalama puan
POST   /api/courts/{id}/rating        # Puan güncelle
```

## 🛠️ Kurulum ve Çalıştırma

### Gereksinimler
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 1. Veritabanı Kurulumu
```bash
# MySQL'e bağlan
mysql -u root -p

# Veritabanı oluştur
CREATE DATABASE sporthub_courts;

# Kullanıcı oluştur (gerekirse)
CREATE USER 'sporthub'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON sporthub_courts.* TO 'sporthub'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Redis Kurulumu
```bash
# macOS
brew install redis
brew services start redis

# Ubuntu/Debian
sudo apt-get install redis-server
sudo systemctl start redis-server

# Test
redis-cli ping
# PONG yanıtı almalısın
```

### 3. Servisi Başlat
```bash
# Proje dizininde
cd court-service

# Maven ile çalıştır
./mvnw spring-boot:run

# Veya tüm servisleri başlat
cd ..
./start-services.sh
```

## 📊 Test Senaryoları

### 1. Temel CRUD İşlemleri
```bash
# 1. Saha oluştur
curl -X POST http://localhost:8083/api/courts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Court",
    "description": "Test Description",
    "courtType": "TENNIS",
    "pricePerHour": 100.00
  }'

# 2. Sahaları listele
curl http://localhost:8083/api/courts

# 3. Saha detayını getir
curl http://localhost:8083/api/courts/1

# 4. Saha güncelle
curl -X PUT http://localhost:8083/api/courts/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated Court"}'
```

### 2. Arama ve Filtreleme
```bash
# Fiyat bazlı arama
curl "http://localhost:8083/api/courts/search/price?minPrice=50&maxPrice=150"

# Özellik bazlı arama
curl "http://localhost:8083/api/courts/search/amenities?lighting=true&parking=true"

# Gelişmiş arama
curl -X POST http://localhost:8083/api/courts/search \
  -H "Content-Type: application/json" \
  -d '{
    "courtType": "TENNIS",
    "minPrice": 100.00,
    "maxPrice": 200.00,
    "lightingAvailable": true
  }'
```

### 3. Cache Test
```bash
# İlk istek (veritabanından)
curl http://localhost:8083/api/courts/1

# İkinci istek (cache'den)
curl http://localhost:8083/api/courts/1

# Redis'te cache'i kontrol et
redis-cli
127.0.0.1:6379> keys *
127.0.0.1:6379> get courts::1
```

## 🔍 Redis Monitoring

### Redis CLI Komutları
```bash
# Bağlan
redis-cli

# Tüm key'leri listele
keys *

# Cache key'lerini bul
keys courts::*

# Key detayını getir
get courts::1

# TTL kontrol et
ttl courts::1

# Memory kullanımı
info memory

# Connected clients
info clients
```

### RedisInsight (GUI)
```bash
# Docker ile RedisInsight başlat
docker run -d --name redisinsight -p 8001:8001 redislabs/redisinsight:latest

# Tarayıcıda aç
http://localhost:8001
```

## 📝 Loglar

### Log Dosyaları
- `logs/court-service.log` - Ana servis logları
- `logs/eureka-server.log` - Service discovery logları

### Log Seviyeleri
```properties
logging.level.com.sporthub=DEBUG
logging.level.org.springframework.cache=DEBUG
```

## 🚨 Hata Ayıklama

### Yaygın Hatalar
1. **Port 8083 kullanımda**: `lsof -ti:8083` ile kontrol et
2. **MySQL bağlantı hatası**: Veritabanı ve kullanıcı bilgilerini kontrol et
3. **Redis bağlantı hatası**: Redis servisinin çalıştığını kontrol et

### Debug Komutları
```bash
# Port kontrolü
lsof -i :8083

# Servis durumu
ps aux | grep court-service

# Log takibi
tail -f logs/court-service.log

# Veritabanı bağlantısı
mysql -u root -p -e "USE sporthub_courts; SHOW TABLES;"
```

## 🔧 Konfigürasyon

### application.properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/sporthub_courts
spring.datasource.username=root
spring.datasource.password=137127117

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Cache
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8762/eureka/
```

## 📚 Ek Kaynaklar

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Redis Documentation](https://redis.io/documentation)

## 🤝 Katkıda Bulunma

1. Fork yap
2. Feature branch oluştur (`git checkout -b feature/amazing-feature`)
3. Commit yap (`git commit -m 'Add amazing feature'`)
4. Push yap (`git push origin feature/amazing-feature`)
5. Pull Request oluştur

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.
