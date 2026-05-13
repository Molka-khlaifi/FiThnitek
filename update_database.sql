-- Add banned column to utilisateur table
ALTER TABLE utilisateur ADD COLUMN banned BOOLEAN DEFAULT FALSE;
