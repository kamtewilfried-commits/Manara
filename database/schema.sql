DROP DATABASE IF EXISTS manara;
-- CHARACTER SET utf8mb4 (utf8mb4 supporte tous les caractères "Léa" ou "محمد")
-- COLLATE unicode_ci si un parent cherche son email Marc.Dupont@email.com ou marc.dupont@email.com, les deux vont fonctionner.
CREATE DATABASE manara CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE manara;

-- ============================================================
--  Contient tous les types d'utilisateurs (parents, admins, animateurs)
--  Le rôle est un ENUM pour éviter les valeurs invalides
-- ============================================================
CREATE TABLE users (
                       id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name  VARCHAR(100) NOT NULL,
                       email      VARCHAR(150) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL,
                       phone      VARCHAR(20),
                       role       ENUM('PARENT', 'ADMIN', 'ANIMATEUR') NOT NULL DEFAULT 'PARENT',
                       active     BOOLEAN NOT NULL DEFAULT TRUE, -- désactiver sans supprimer le compte
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  Chaque enfant appartient à un parent (parent_id)
--  Si le parent est supprimé, ses enfants le sont aussi
-- ============================================================
CREATE TABLE children (
                          id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name    VARCHAR(100) NOT NULL,
                          last_name     VARCHAR(100) NOT NULL,
                          birth_date    DATE NOT NULL,
                          parent_id     BIGINT NOT NULL,
                          allergies     TEXT,
                          medical_notes TEXT,                       -- informations utiles pour les animateurs
                          CONSTRAINT fk_child_parent
                              FOREIGN KEY (parent_id) REFERENCES users(id)
                                  ON DELETE CASCADE
);

-- ============================================================
--  animateur_id : lien vers l'animateur responsable de l'activité
--  active : permet d'archiver une activité sans la supprimer
-- ============================================================
CREATE TABLE activities (
                            id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name         VARCHAR(150) NOT NULL,
                            description  TEXT,
                            category     VARCHAR(100),
                            age_range    VARCHAR(50),
                            animateur_id BIGINT,
                            active       BOOLEAN NOT NULL DEFAULT TRUE,
                            CONSTRAINT fk_activity_animateur
                                FOREIGN KEY (animateur_id) REFERENCES users(id)
                                    ON DELETE SET NULL                    -- si l'animateur est supprimé, l'activité reste
);

-- ============================================================
--  available_spots : mis à jour automatiquement par les triggers
--  Ne pas modifier cette colonne manuellement
-- ============================================================
CREATE TABLE sessions (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          activity_id     BIGINT NOT NULL,
                          start_date      DATETIME NOT NULL,
                          end_date        DATETIME NOT NULL,
                          location        VARCHAR(200),
                          capacity        INT NOT NULL,
                          available_spots INT NOT NULL,             -- géré par trigger, ne pas toucher manuellement
                          status          ENUM('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')
    NOT NULL DEFAULT 'PLANNED',
                          CONSTRAINT fk_session_activity
                              FOREIGN KEY (activity_id) REFERENCES activities(id)
                                  ON DELETE CASCADE
);

-- ============================================================
--  status : une inscription peut être en attente, confirmée ou annulée
--  waitlist : TRUE si la session est pleine, l'enfant est en liste d'attente
--  confirmed_by : l'admin qui a validé l'inscription (traçabilité)
-- ============================================================
CREATE TABLE registrations (
                               id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                               child_id          BIGINT NOT NULL,
                               session_id        BIGINT NOT NULL,
                               registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               status            ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                               waitlist          BOOLEAN NOT NULL DEFAULT FALSE,
                               confirmed_by      BIGINT,
                               CONSTRAINT fk_registration_child
                                   FOREIGN KEY (child_id) REFERENCES children(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_registration_session
                                   FOREIGN KEY (session_id) REFERENCES sessions(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_registration_confirmed_by
                                   FOREIGN KEY (confirmed_by) REFERENCES users(id)
                                       ON DELETE SET NULL,
                               CONSTRAINT unique_registration UNIQUE (child_id, session_id) -- un enfant ne peut pas s'inscrire 2x à la même session
);

/*
Un trigger c'est du code SQL qui s'exécute automatiquement quand quelque chose se passe dans la base de données.
Tu n'as pas besoin de l'appeler, il se déclenche tout seul. Dans notre cas, on utilise des triggers pour faire
en sorte que la colonne available_spots dans la table sessions soit toujours à jour. Par exemple, quand une session
 est créée, on veut que available_spots soit égal à capacity. Et quand une inscription est confirmée, on veut que
  available_spots diminue de 1. Si une inscription est annulée ou supprimée, on veut que available_spots augmente de 1.
 Sans lui tu devrais le faire manuellement à chaque fois.
*/

-- Trigger : quand une session est créée, available_spots = capacity
DELIMITER $$
CREATE TRIGGER trg_session_before_insert
    BEFORE INSERT ON sessions
    FOR EACH ROW
BEGIN
    SET NEW.available_spots = NEW.capacity;
END$$

    -- Trigger : quand une inscription confirmée est ajoutée, on retire une place
    CREATE TRIGGER trg_registration_after_insert
        AFTER INSERT ON registrations
        FOR EACH ROW
    BEGIN
        IF NEW.waitlist = FALSE AND NEW.status = 'CONFIRMED' THEN
        UPDATE sessions
        SET available_spots = available_spots - 1
        WHERE id = NEW.session_id;
    END IF;
    END$$

    -- Trigger : quand une inscription est annulée ou confirmée, on ajuste les places
    CREATE TRIGGER trg_registration_after_update
        AFTER UPDATE ON registrations
        FOR EACH ROW
    BEGIN
        IF OLD.status != 'CANCELLED' AND NEW.status = 'CANCELLED' AND NEW.waitlist = FALSE THEN
        UPDATE sessions SET available_spots = available_spots + 1 WHERE id = NEW.session_id;
    END IF;
    IF OLD.status = 'PENDING' AND NEW.status = 'CONFIRMED' AND NEW.waitlist = FALSE THEN
    UPDATE sessions SET available_spots = available_spots - 1 WHERE id = NEW.session_id;
END IF;
END$$

-- Trigger : si une inscription confirmée est supprimée, on libère la place
CREATE TRIGGER trg_registration_after_delete
    AFTER DELETE ON registrations
    FOR EACH ROW
BEGIN
    IF OLD.waitlist = FALSE AND OLD.status = 'CONFIRMED' THEN
    UPDATE sessions SET available_spots = available_spots + 1 WHERE id = OLD.session_id;
END IF;
END$$
DELIMITER ;

-- ============================================================
--  email_sent : permet de savoir si l'email a bien été envoyé
--  Si email_sent = FALSE, on peut relancer l'envoi facilement
-- ============================================================
CREATE TABLE notifications (
                               id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id       BIGINT NOT NULL,
                               subject       VARCHAR(255) NOT NULL,
                               message       TEXT NOT NULL,
                               type          ENUM('CONFIRMATION', 'REMINDER', 'CANCELLATION', 'RECEIPT') NOT NULL,
                               is_read       BOOLEAN NOT NULL DEFAULT FALSE,
                               email_sent    BOOLEAN NOT NULL DEFAULT FALSE,   -- FALSE = email pas encore envoyé
                               email_sent_at DATETIME,                         -- NULL si l'email n'a pas été envoyé
                               sent_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_notification_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE CASCADE
);

-- ============================================================
--  PAYMENTS
--  ON DELETE RESTRICT : on ne peut pas supprimer une inscription qui a un paiement
-- ============================================================
CREATE TABLE payments (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          registration_id BIGINT NOT NULL,
                          amount          DECIMAL(10, 2) NOT NULL,
                          currency        VARCHAR(3) NOT NULL DEFAULT 'CAD',
                          status          ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
                          payment_method  ENUM('CARD', 'CASH', 'TRANSFER') NOT NULL DEFAULT 'CARD',
                          transaction_id  VARCHAR(255) UNIQUE,
                          paid_at         DATETIME,
                          created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_payment_registration
                              FOREIGN KEY (registration_id) REFERENCES registrations(id)
                                  ON DELETE RESTRICT                          -- sécurité : ne jamais perdre un historique de paiement
);



INSERT INTO users (first_name, last_name, email, password, role) VALUES
    ('Admin', 'Manara', 'admin@manara.ca', '$2a$10$hashed_pe', 'ADMIN');

INSERT INTO users (first_name, last_name, email, password, role) VALUES
    ('Sophie', 'Tremblay', 'sophie@manara.ca', '$2a$10$hashd_here', 'ANIMATEUR');

INSERT INTO users (first_name, last_name, email, password, phone, role) VALUES
                                                                            ('Marc', 'Dupont', 'marc.dupont@email.com', '$2a$10$hashed_password_here', '514-555-0101', 'PARENT'),
                                                                            ('Fatima', 'Benali', 'fatima.benali@email.com', '$2a$10$hashed_password_here', '514-555-0202', 'PARENT');

INSERT INTO children (first_name, last_name, birth_date, parent_id, allergies) VALUES
                                                                                   ('Léa', 'Dupont', '2016-04-12', 3, NULL),
                                                                                   ('Tom', 'Dupont', '2018-09-03', 3, 'Arachides'),
                                                                                   ('Amir', 'Benali', '2015-11-25', 4, NULL);

INSERT INTO activities (name, description, category, age_range, animateur_id) VALUES
                                                                                  ('Éveil musical', 'Initiation aux instruments et au rythme', 'Musique', '4-7 ans', 2),
                                                                                  ('Arts plastiques', 'Peinture, dessin et bricolage créatif', 'Arts', '6-12 ans', 2);

-- available_spots sera automatiquement = capacity grâce au trigger
INSERT INTO sessions (activity_id, start_date, end_date, location, capacity) VALUES
                                                                                 (1, '2026-04-07 09:00:00', '2026-04-07 10:30:00', 'Salle A', 15),
                                                                                 (2, '2026-04-08 13:00:00', '2026-04-08 15:00:00', 'Salle B', 12);

INSERT INTO registrations (child_id, session_id, status, confirmed_by) VALUES
                                                                           (1, 1, 'CONFIRMED', 1),
                                                                           (3, 2, 'CONFIRMED', 1);

INSERT INTO payments (registration_id, amount, status, payment_method, paid_at) VALUES
                                                                                    (1, 25.00, 'COMPLETED', 'CARD', NOW()),
                                                                                    (2, 25.00, 'COMPLETED', 'CARD', NOW());

INSERT INTO notifications (user_id, subject, message, type, email_sent, email_sent_at) VALUES
                                                                                           (3, 'Inscription confirmée', 'Léa est inscrite à Éveil musical le 7 avril.', 'CONFIRMATION', TRUE, NOW()),
                                                                                           (4, 'Inscription confirmée', 'Amir est inscrit à Arts plastiques le 8 avril.', 'CONFIRMATION', TRUE, NOW());