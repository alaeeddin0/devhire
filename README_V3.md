# DevHire — V3 : fonctionnalités métier avancées

DevHire est une API REST de recrutement construite avec Spring Boot, PostgreSQL et Spring Security. Cette version enrichit le socle V1 (CRUD) et V2 (JWT) avec des fonctionnalités métier prévues par le cahier des charges : offres sauvegardées, entretiens et historique des candidatures.

## État de la version

Les fonctionnalités V3 sont implémentées. Les parcours fonctionnels ont été vérifiés dans Postman.

La dernière compilation locale n'a pas pu écrire dans `target/classes` car le fichier `application-example.properties` est verrouillé, probablement par une instance de l'application encore ouverte. Arrêter l'application puis lancer la compilation résout ce verrou.

```powershell
.\mvnw.cmd -DskipTests compile
```

## Technologies

- Java 21
- Spring Boot
- Spring Web, Spring Data JPA, Bean Validation
- Spring Security avec JWT stateless
- PostgreSQL
- Lombok
- Maven Wrapper

## Architecture des packages

```text
com.example.devhire
├── config
├── controller
│   ├── auth
│   ├── candidate
│   ├── interview
│   ├── jobApplication
│   ├── jobOffer
│   ├── recruiter
│   ├── resume
│   └── user
├── dto
├── exception
├── model
│   ├── candidate
│   ├── interview
│   ├── jobApplication
│   ├── jobOffer
│   ├── recruiter
│   ├── resume
│   └── user
├── repo
├── security
└── service
```

## Fonctionnalités déjà réalisées

### V1 — socle métier

- Création et gestion des utilisateurs.
- Profils candidat et recruteur.
- CRUD sécurisé des offres d'emploi.
- Recherche, filtres et pagination des offres.
- Dépôt, consultation, téléchargement et suppression des CV PDF.
- Création, modification et suppression des candidatures.
- Transitions de statut contrôlées : `PENDING`, `REVIEWING`, `INTERVIEW`, `ACCEPTED`, `REJECTED`.
- Gestion centralisée des erreurs et validation des entrées.

### V2 — sécurité

- Inscription avec rôle `CANDIDATE` ou `RECRUITER`.
- Hachage BCrypt des mots de passe.
- Connexion avec JWT Bearer.
- Sessions stateless.
- Autorisations par rôle dans `SecurityConfig`.
- Vérification métier du propriétaire d'une offre, candidature, sauvegarde, CV ou entretien.

### V3 — offres sauvegardées

Un candidat peut sauvegarder une offre une seule fois.

| Méthode | Route | Rôle |
|---|---|---|
| POST | `/api/saved-job-offers/{jobOfferId}` | CANDIDATE |
| GET | `/api/saved-job-offers` | CANDIDATE |
| DELETE | `/api/saved-job-offers/{savedJobOfferId}` | CANDIDATE |

L'entité `SavedJobOffer` contient la date de sauvegarde et une contrainte unique sur le couple candidat/offre.

### V3 — entretiens

Un recruteur propriétaire de l'offre peut planifier, modifier, annuler ou terminer un entretien lié à une candidature au statut `INTERVIEW`.

- Types : `ONLINE`, `ON_SITE`, `PHONE`.
- États : `SCHEDULED`, `COMPLETED`, `CANCELLED`.
- Un entretien `ONLINE` exige `meetingLink`.
- Un entretien `ON_SITE` exige `location`.
- Le candidat concerné peut consulter ses entretiens.

| Méthode | Route | Rôle |
|---|---|---|
| POST | `/api/interviews` | RECRUITER |
| GET | `/api/interviews/candidate/me` | CANDIDATE |
| GET | `/api/interviews/recruiter/me` | RECRUITER |
| PUT | `/api/interviews/{interviewId}` | RECRUITER |
| PATCH | `/api/interviews/{interviewId}/cancel` | RECRUITER |
| PATCH | `/api/interviews/{interviewId}/complete` | RECRUITER |

### V3 — historique des statuts

Chaque changement valide de statut crée une entrée `ApplicationStatusHistory` avec :

- le statut précédent ;
- le nouveau statut ;
- le recruteur ayant réalisé la transition ;
- la date du changement.

Route de consultation :

```text
GET /api/job-applications/{applicationId}/status-history
```

Elle est accessible au candidat concerné ou au recruteur propriétaire de l'offre. Le service vérifie systématiquement cette propriété, au-delà du contrôle de rôle JWT.

## Configuration locale

Créer `src/main/resources/application.properties` à partir de `application-example.properties` et ne jamais versionner les secrets.

Exemple :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devhire
spring.datasource.username=postgres
spring.datasource.password=VOTRE_MOT_DE_PASSE

app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration-ms=3600000
app.storage.resume-directory=uploads/resumes
```

Sous PowerShell, définir le secret pour la session :

```powershell
$env:JWT_SECRET = "votre-secret-base64"
```

## Authentification

Connexion :

```text
POST /api/auth/login
```

Le JWT reçu est envoyé sur les routes protégées :

```text
Authorization: Bearer <token>
```

## Suite du projet

La prochaine version est la V4 : découpage progressif du monolithe vers les microservices prévus dans le cahier des charges.

1. Définir les limites des services : Auth, Job, Application, Interview et Notification.
2. Préparer la configuration centralisée et l'API Gateway.
3. Extraire un premier service à faible risque, puis introduire la communication inter-services.
4. Ajouter Docker Compose, puis les notifications asynchrones.

