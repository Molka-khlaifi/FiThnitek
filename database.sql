CREATE DATABASE IF NOT EXISTS couvoiturage_db;
USE couvoiturage_db;

CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    cin VARCHAR(20) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('PASSAGER', 'CONDUCTEUR', 'ADMIN') NOT NULL,
    banned BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS conducteur_info (
    utilisateur_id    INT PRIMARY KEY,
    carte_gris_path   VARCHAR(500),
    permis_path       VARCHAR(500),
    statut_validation ENUM('EN_ATTENTE', 'APPROUVE', 'REJETE') DEFAULT 'EN_ATTENTE',
    montant_gagne     DOUBLE DEFAULT 0.0,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS passager_info (
    utilisateur_id INT PRIMARY KEY,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);
