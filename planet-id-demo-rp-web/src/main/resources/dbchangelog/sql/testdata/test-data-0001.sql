-- Test users for RP
DELETE FROM "user" WHERE email = 'test@test';

INSERT INTO "user"(id, email, password) VALUES(1, 'test@test', '$2a$10$MyMxwPOChYDxUdaIV1jdROnP3CzUoqPp.6..RasjUXYkkIgFaFTji');
