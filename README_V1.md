# DevHire - V1 Monolithe REST

DevHire est une plateforme de recrutement et de gestion des stages/PFE. Cette première version fournit un backend REST monolithique permettant de créer des comptes, gérer des profils candidat et recruteur, publier des offres, déposer des CV PDF et postuler.

## Objectif de la V1

Construire une base backend robuste avant d'ajouter la sécurité JWT, les microservices et le frontend Angular.

Le périmètre V1 couvre :

- Spring Boot, PostgreSQL, JPA/Hibernate et API REST ;
- architecture `model -> repo -> service -> controller` ;
- utilisateurs, profils, offres, CV et candidatures ;
- validation, règles métier et gestion centralisée des erreurs ;
- recherche, filtres et pagination des offres.

## Stack technique

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Validation
- BCrypt (`spring-security-crypto`)
- Lombok
- Maven Wrapper

## Architecture

```text
src/main/java/com/example/devhire/
├── config/        # Configuration technique
├── controller/    # Endpoints REST
├── dto/           # Requêtes et réponses de l'API
├── exception/     # Gestion centralisée des erreurs
├── model/         # Entités JPA et enums métier
├── repo/          # Repositories Spring Data JPA
└── service/       # Règles métier
```

Le principe appliqué est le suivant :

```text
Controller -> Service -> Repository -> PostgreSQL
```

Les contrôleurs reçoivent les requêtes HTTP. Les services appliquent les règles métier. Les repositories accèdent aux données. Les DTOs évitent d'exposer directement des données sensibles comme `passwordHash`.

## Modèle métier

```text
User
 ├── CandidateProfile
 │    ├── Resume (plusieurs versions de CV)
 │    └── JobApplication
 └── RecruiterProfile
      └── JobOffer
           └── JobApplication
```

### Rôles

| Rôle | Responsabilité dans V1 |
|---|---|
| `CANDIDATE` | Crée son profil, dépose des CV et postule aux offres. |
| `RECRUITER` | Crée son profil entreprise et gère ses offres. |
| `ADMIN` | Prévu dans le modèle, non accessible par inscription publique. |

### Statuts de candidature

```text
PENDING -> REVIEWING -> INTERVIEW -> ACCEPTED / REJECTED
```

La candidature est créée avec le statut `PENDING`. Les transitions de suivi seront sécurisées par JWT dans V2.

## Fonctionnalités V1

### Comptes et profils

- Création d'un compte candidat ou recruteur.
- Chiffrement BCrypt des mots de passe.
- Lecture et modification des utilisateurs.
- Désactivation d'un compte sans suppression destructive des données.
- Création, lecture et modification d'un profil candidat.
- Création, lecture et modification d'un profil recruteur.

### Offres

- Création, lecture, modification et suppression d'une offre.
- Vérification qu'un recruteur ne modifie ou ne supprime que ses propres offres.
- Recherche par mot-clé sur le titre et l'entreprise.
- Filtres par localisation, mode de travail et type d'offre.
- Pagination et tri décroissant par date de création.

### CV

- Upload de fichiers PDF uniquement.
- Limite de taille : 5 Mo.
- Stockage du fichier sur disque et métadonnées dans PostgreSQL.
- Versionnement : un candidat peut déposer plusieurs CV.
- Consultation des versions, téléchargement et suppression d'un CV non utilisé.
- Protection contre la suppression d'un CV lié à une candidature.

### Candidatures

- Création d'une candidature avec un CV appartenant au candidat.
- Interdiction de postuler deux fois à la même offre.
- Consultation des candidatures côté candidat et recruteur.
- Modification de la lettre de motivation et du CV uniquement lorsque le statut est `PENDING`.
- Suppression autorisée uniquement au candidat propriétaire et tant que le statut est `PENDING`.

### Validation et erreurs

- Validation des DTOs avec `@Valid`.
- Réponses d'erreur centralisées grâce à `GlobalExceptionHandler`.
- Codes HTTP utilisés :

| Situation | Code HTTP |
|---|---|
| Ressource introuvable | `404 Not Found` |
| Requête ou règle métier invalide | `400 Bad Request` |
| Création réussie | `201 Created` |
| Suppression réussie | `204 No Content` |

## Installation locale

### Prérequis

- Java 21
- PostgreSQL
- Git
- Un IDE Java (IntelliJ IDEA ou VS Code avec extensions Java)

Maven n'a pas besoin d'être installé globalement : le projet utilise le Maven Wrapper.

### 1. Cloner le projet

```bash
git clone <URL_DU_DEPOT>
cd devhire
```

### 2. Créer la base de données

Dans PostgreSQL :

```sql
CREATE DATABASE devhire;
```

### 3. Créer la configuration locale

Créez le fichier `src/main/resources/application.properties` :

```properties
spring.application.name=devhire
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/devhire
spring.datasource.username=postgres
spring.datasource.password=your-password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

app.storage.resume-directory=uploads/resumes
```

Ne publiez jamais ce fichier : il contient des informations locales. Le fichier `application-example.properties` sert de modèle partageable.

### 4. Lancer l'application

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Ou lancez `DevhireApplication` depuis votre IDE.

L'API est disponible à l'adresse :

```text
http://localhost:8080
```

## Endpoints REST

### Utilisateurs

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Créer un compte candidat ou recruteur |
| `GET` | `/api/users` | Lister les utilisateurs |
| `GET` | `/api/users/{id}` | Consulter un utilisateur |
| `PUT` | `/api/users/{id}` | Modifier un utilisateur |
| `PATCH` | `/api/users/{id}/deactivate` | Désactiver un compte |

### Profils

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidate-profiles` | Créer un profil candidat |
| `GET` | `/api/candidate-profiles/{id}` | Consulter un profil candidat |
| `PUT` | `/api/candidate-profiles/{id}` | Modifier un profil candidat |
| `POST` | `/api/recruiter-profiles` | Créer un profil recruteur |
| `GET` | `/api/recruiter-profiles/{id}` | Consulter un profil recruteur |
| `PUT` | `/api/recruiter-profiles/{id}` | Modifier un profil recruteur |

### Offres

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/job-offers` | Lister toutes les offres |
| `GET` | `/api/job-offers/{id}` | Consulter une offre |
| `POST` | `/api/job-offers?recruiterProfileId={id}` | Créer une offre |
| `PUT` | `/api/job-offers/{id}?recruiterProfileId={id}` | Modifier une offre |
| `DELETE` | `/api/job-offers/{id}?recruiterProfileId={id}` | Supprimer une offre |
| `GET` | `/api/job-offers/search` | Rechercher, filtrer et paginer |

Exemple de recherche :

```text
GET /api/job-offers/search?keyword=java&location=Casablanca&workMode=HYBRID&offerType=PFE&page=0&size=10
```

### CV

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidate-profiles/{candidateId}/resume` | Uploader un PDF (`multipart/form-data`) |
| `GET` | `/api/candidate-profiles/{candidateId}/resumes` | Lister les versions de CV |
| `GET` | `/api/candidate-profiles/{candidateId}/resumes/{resumeId}` | Consulter les métadonnées |
| `GET` | `/api/candidate-profiles/{candidateId}/resumes/{resumeId}/download` | Télécharger le PDF |
| `DELETE` | `/api/candidate-profiles/{candidateId}/resumes/{resumeId}` | Supprimer un CV non utilisé |

### Candidatures

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/job-applications` | Créer une candidature |
| `GET` | `/api/job-applications/{id}` | Consulter une candidature |
| `GET` | `/api/job-applications/candidate/{id}` | Lister les candidatures d'un candidat |
| `GET` | `/api/job-applications/recruiter/{id}` | Lister les candidatures reçues par un recruteur |
| `PUT` | `/api/job-applications/{id}?candidateProfileId={id}` | Modifier une candidature `PENDING` |
| `DELETE` | `/api/job-applications/{id}?candidateProfileId={id}` | Supprimer une candidature `PENDING` |

## Exemple de flux de test

1. Créer un utilisateur `RECRUITER`.
2. Créer son `RecruiterProfile`.
3. Créer une `JobOffer` avec son `recruiterProfileId`.
4. Créer un utilisateur `CANDIDATE`.
5. Créer son `CandidateProfile`.
6. Uploader un CV PDF.
7. Créer une `JobApplication` avec `candidateProfileId`, `jobOfferId`, `resumeId` et une lettre de motivation.
8. Vérifier les candidatures côté candidat et recruteur.

## Limites connues de V1

V1 est volontairement un monolithe et n'inclut pas encore :

- connexion utilisateur ;
- JWT et protection effective des routes ;
- autorisations issues du compte connecté ;
- historique de statut ;
- entretiens, notifications et email ;
- compétences et offres sauvegardées ;
- Swagger/OpenAPI ;
- tests métier automatisés complets ;
- Docker, microservices, API Gateway, RabbitMQ/Kafka et Angular.

Certains endpoints reçoivent donc temporairement `candidateProfileId` ou `recruiterProfileId`. En V2, ces informations seront extraites du JWT et ne viendront plus de la requête cliente.

## Feuille de route

- [x] V1 - Monolithe REST, PostgreSQL et CRUD métier principal.
- [ ] V2 - Spring Security, connexion, JWT et rôles.
- [ ] V3 - Validation avancée, Swagger, tests et recherche enrichie.
- [ ] V4 - Microservices et API Gateway.
- [ ] V5 - Docker et Docker Compose.
- [ ] V6 - Messagerie asynchrone et notifications.
- [ ] V7 - Frontend Angular et dashboards par rôle.

## Auteur

Alaa eddin Benslimane.
