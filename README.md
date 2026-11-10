# CRM Manara

Plateforme web de gestion pour l'organisme Manara, avec des espaces dédiés aux administrateurs, aux animateurs et aux parents. Projet réalisé en équipe au Cégep Rosemont, avec la méthode Scrum.

## Fonctionnalités

- Authentification sécurisée (JWT, connexion avec Google, code de vérification OTP)
- Profils parent et animateur
- Communication entre l'administration et les parents, avec courriels transactionnels
- Messagerie et appel vidéo intégrés
- Chatbot relié à la base de données
- Tableau de bord administrateur avec mode sombre

## Technologies

- Backend : Java, Spring Boot (API REST), Spring Security, JWT, OAuth 2.0
- Frontend : Angular, TypeScript
- Services externes : Google OAuth, Twilio, Stripe, OpenAI, Stream
- Gestion de projet : Scrum, GitLab

## Structure du projet

- `manara/` : application Spring Boot
- `client-manara/client-manara/` : application Angular
- `Documentation/` : diagrammes

## Installation

1. Copier `manara/src/main/resources/application.properties.example` en `application.properties` et remplacer les valeurs `A_REMPLIR` par vos propres identifiants (base de données, courriel, clés des services externes).
2. Définir la variable d'environnement `STREAM_API_SECRET`.
3. Lancer le backend depuis `manara/` avec `./mvnw spring-boot:run`.
4. Lancer le frontend depuis `client-manara/client-manara/` avec `npm install`, puis `ng serve`.
