-- SportHub Court Service Sample Data
-- Bu script'i Spring Boot uygulaması çalıştırıldıktan sonra çalıştırın
-- Tablolar JPA tarafından otomatik oluşturulacak

USE sporthub_courts;

-- Örnek sahalar ekle
INSERT INTO courts (name, description, court_type, price_per_hour, is_indoor, max_players, court_size, surface_type, lighting_available, parking_available, shower_available, rating, total_reviews, is_active, created_at, updated_at) VALUES
('Tennis Court 1', 'Professional tennis court with excellent surface quality and lighting', 'TENNIS', 150.00, false, 4, '23.77m x 10.97m', 'Hard Court', true, true, true, 4.5, 12, true, NOW(), NOW()),
('Tennis Court 2', 'Indoor tennis court with climate control', 'TENNIS', 180.00, true, 4, '23.77m x 10.97m', 'Carpet', true, true, true, 4.2, 8, true, NOW(), NOW()),
('Basketball Court 1', 'Indoor basketball court with professional flooring', 'BASKETBALL', 120.00, true, 10, '28m x 15m', 'Wooden Floor', true, true, true, 4.8, 15, true, NOW(), NOW()),
('Basketball Court 2', 'Outdoor basketball court with concrete surface', 'BASKETBALL', 80.00, false, 10, '28m x 15m', 'Concrete', false, true, false, 4.0, 6, true, NOW(), NOW()),
('Football Field 1', 'Professional football field with artificial grass', 'FOOTBALL', 200.00, false, 22, '105m x 68m', 'Artificial Grass', true, true, true, 4.6, 20, true, NOW(), NOW()),
('Football Field 2', 'Natural grass football field', 'FOOTBALL', 250.00, false, 22, '105m x 68m', 'Natural Grass', false, true, false, 4.7, 18, true, NOW(), NOW()),
('Squash Court 1', 'Professional squash court with glass walls', 'SQUASH', 80.00, true, 2, '9.75m x 6.4m', 'Wooden Floor', true, false, true, 4.3, 10, true, NOW(), NOW()),
('Squash Court 2', 'Standard squash court', 'SQUASH', 70.00, true, 2, '9.75m x 6.4m', 'Wooden Floor', true, false, true, 4.1, 7, true, NOW(), NOW()),
('Volleyball Court 1', 'Indoor volleyball court with sand surface', 'VOLLEYBALL', 100.00, true, 12, '18m x 9m', 'Sand', true, true, true, 4.4, 9, true, NOW(), NOW()),
('Badminton Court 1', 'Professional badminton court', 'BADMINTON', 60.00, true, 4, '13.4m x 6.1m', 'Wooden Floor', true, true, false, 4.2, 11, true, NOW(), NOW()),
('Table Tennis 1', 'Indoor table tennis facility', 'TABLE_TENNIS', 40.00, true, 4, '2.74m x 1.525m', 'Wooden Table', true, true, false, 4.0, 5, true, NOW(), NOW());

-- Örnek saha fotoğrafları ekle
INSERT INTO court_images (image_url, image_title, is_primary, image_order, created_at, court_id) VALUES
('https://example.com/tennis-court-1-main.jpg', 'Tennis Court 1 - Main View', true, 0, NOW(), 1),
('https://example.com/tennis-court-1-side.jpg', 'Tennis Court 1 - Side View', false, 1, NOW(), 1),
('https://example.com/tennis-court-1-night.jpg', 'Tennis Court 1 - Night View', false, 2, NOW(), 1),
('https://example.com/basketball-court-1-main.jpg', 'Basketball Court 1 - Main View', true, 0, NOW(), 3),
('https://example.com/basketball-court-1-court.jpg', 'Basketball Court 1 - Court View', false, 1, NOW(), 3),
('https://example.com/football-field-1-main.jpg', 'Football Field 1 - Main View', true, 0, NOW(), 5),
('https://example.com/football-field-1-aerial.jpg', 'Football Field 1 - Aerial View', false, 1, NOW(), 5),
('https://example.com/squash-court-1-main.jpg', 'Squash Court 1 - Main View', true, 0, NOW(), 7),
('https://example.com/volleyball-court-1-main.jpg', 'Volleyball Court 1 - Main View', true, 0, NOW(), 9),
('https://example.com/badminton-court-1-main.jpg', 'Badminton Court 1 - Main View', true, 0, NOW(), 10);

-- Örnek saha olanakları ekle
INSERT INTO court_amenities (amenity_name, amenity_description, is_available, amenity_icon, created_at, court_id) VALUES
('Ball Machine', 'Automatic ball machine for tennis practice', true, '🎾', NOW(), 1),
('Pro Shop', 'Professional sports equipment shop', true, '🛍️', NOW(), 1),
('Locker Room', 'Secure locker room with showers', true, '🚿', NOW(), 1),
('Scoreboard', 'Electronic scoreboard for basketball games', true, '📊', NOW(), 3),
('Basketball Racks', 'Basketball storage racks', true, '🏀', NOW(), 3),
('Water Fountains', 'Refreshing water fountains', true, '💧', NOW(), 3),
('Goal Posts', 'Professional football goal posts', true, '⚽', NOW(), 5),
('Corner Flags', 'Corner flags for football field', true, '🚩', NOW(), 5),
('Changing Rooms', 'Spacious changing rooms', true, '👕', NOW(), 5),
('Squash Rackets', 'Professional squash rackets available', true, '🏸', NOW(), 7),
('Squash Balls', 'High-quality squash balls', true, '⚫', NOW(), 7),
('Viewing Gallery', 'Spectator viewing gallery', true, '👥', NOW(), 7),
('Sand Surface', 'Professional sand surface for volleyball', true, '🏖️', NOW(), 9),
('Net System', 'Adjustable volleyball net system', true, '🏐', NOW(), 9),
('Badminton Nets', 'Professional badminton nets', true, '🏸', NOW(), 10),
('Shuttlecocks', 'High-quality shuttlecocks', true, '🪶', NOW(), 10),
('Table Tennis Tables', 'Professional table tennis tables', true, '🏓', NOW(), 11),
('Ping Pong Balls', 'High-quality ping pong balls', true, '⚪', NOW(), 11);

-- Veri ekleme tamamlandı mesajı
SELECT 'Sample data inserted successfully!' AS message;

-- Eklenen verileri kontrol et
SELECT 
    'Courts' as table_name,
    COUNT(*) as record_count
FROM courts
UNION ALL
SELECT 
    'Court Images' as table_name,
    COUNT(*) as record_count
FROM court_images
UNION ALL
SELECT 
    'Court Amenities' as table_name,
    COUNT(*) as record_count
FROM court_amenities;

-- Örnek saha detaylarını göster
SELECT 
    c.id,
    c.name,
    c.court_type,
    c.price_per_hour,
    c.rating,
    COUNT(ci.id) as image_count,
    COUNT(ca.id) as amenity_count
FROM courts c
LEFT JOIN court_images ci ON c.id = ci.court_id
LEFT JOIN court_amenities ca ON c.id = ca.court_id
GROUP BY c.id, c.name, c.court_type, c.price_per_hour, c.rating
ORDER BY c.id;
