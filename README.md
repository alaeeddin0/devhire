# DevHire

Plateforme de recrutement et de gestion des stages/PFE. DevHire met en relation des candidats et des recruteurs : publication d'offres, dépôt de CV PDF, candidatures et suivi du processus de recrutement.

> Projet portfolio en cours de développement. La première version est un monolithe Spring Boot connecté à PostgreSQL ; l'architecture évoluera progressivement vers les microservices.

## Fonctionnalités actuelles

- Création de comptes `CANDIDATE` et `RECRUITER` avec mot de passe chiffré par BCrypt.
- Création et modification des profils candidat et recruteur.
- Gestion des offres : création, consultation, recherche, filtres, pagination, modification et suppression contrôlée par propriétaire.
- Dépôt de CV au format PDF (taille maximale : 5 Mo).
- Versionnement des CV : un candidat peut posséder plusieurs versions de son CV.
- Création d'une candidature avec un CV sélectionné.
- Statut initial de candidature : `PENDING`.
- Prévention d'une double candidature du même candidat à la même offre.
- Gestion centralisée des erreurs et validation des entrées API.

## Flux métier

```text
Recruteur crée son compte -> complète son profil -> publie une offre

Candidat crée son compte -> complète son profil -> dépose son CV
        -> sélectionne un CV -> postule à une offre
```

Le flux final visé est :

```text
CANDIDATE -> PENDING -> REVIEWING -> INTERVIEW -> ACCEPTED / REJECTED
```

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

Le backend applique une organisation en couches :

```text
src/main/java/com/example/devhire/
├── config/        # Configuration technique
├── controller/    # Endpoints REST
├── dto/           # Objets d'entrée et de sortie de l'API
├── exception/     # Exceptions et réponses d'erreur centralisées
├── model/         # Entités JPA et enums métier
├── repo/          # Accès aux données avec Spring Data JPA
└── service/       # Règles métier
```

## Modèle métier actuel

```text
User
 ├── CandidateProfile
 │    ├── Resume (plusieurs versions possibles)
 │    └── JobApplication
 └── RecruiterProfile
      └── JobOffer
           └── JobApplication
```

## Prérequis

- Java 21
- PostgreSQL
- Un IDE Java, par exemple IntelliJ IDEA ou VS Code avec les extensions Java

Maven n'a pas besoin d'être installé globalement : le projet utilise le Maven Wrapper.

## Installation locale

### 1. Créer la base PostgreSQL

```sql
CREATE DATABASE devhire;
```

### 2. Créer la configuration locale

Copiez le fichier d'exemple :

```text
src/main/resources/application-example.properties
```

vers :

```text
src/main/resources/application.properties
```

Puis adaptez les variables de connexion PostgreSQL :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devhire
spring.datasource.username=postgres
spring.datasource.password=your-password
```

`application.properties` est volontairement ignoré par Git afin de ne jamais publier de mot de passe.

### 3. Lancer l'application

Sur Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Ou depuis votre IDE, lancez la classe `DevhireApplication`.

L'API sera disponible sur :

```text
http://localhost:8080
```

## Endpoints principaux

| Domaine | Méthode | Endpoint | Description |
|---|---|---|---|
| Utilisateurs | `POST` | `/api/users` | Créer un compte candidat ou recruteur |
| Utilisateurs | `GET` | `/api/users` | Lister les utilisateurs |
| Utilisateurs | `GET` | `/api/users/{id}` | Consulter un utilisateur |
| Utilisateurs | `PUT` | `/api/users/{id}` | Modifier un utilisateur |
| Utilisateurs | `PATCH` | `/api/users/{id}/deactivate` | Désactiver un compte |
| Candidats | `POST` | `/api/candidate-profiles` | Créer un profil candidat |
| Candidats | `GET` | `/api/candidate-profiles/{id}` | Consulter un profil candidat |
| Candidats | `PUT` | `/api/candidate-profiles/{id}` | Modifier un profil candidat |
| Recruteurs | `POST` | `/api/recruiter-profiles` | Créer un profil recruteur |
| Recruteurs | `GET` | `/api/recruiter-profiles/{id}` | Consulter un profil recruteur |
| Recruteurs | `PUT` | `/api/recruiter-profiles/{id}` | Modifier un profil recruteur |
| CV | `POST` | `/api/candidate-profiles/{id}/resume` | Déposer un CV PDF (`multipart/form-data`) |
| Offres | `GET` | `/api/job-offers` | Lister les offres |
| Offres | `GET` | `/api/job-offers/{id}` | Consulter une offre |
| Offres | `POST` | `/api/job-offers?recruiterProfileId={id}` | Créer une offre |
| Offres | `PUT` | `/api/job-offers/{id}?recruiterProfileId={id}` | Modifier une offre |
| Offres | `DELETE` | `/api/job-offers/{id}?recruiterProfileId={id}` | Supprimer une offre |
| Offres | `GET` | `/api/job-offers/search` | Rechercher, filtrer et paginer les offres |
| Candidatures | `POST` | `/api/job-applications` | Créer une candidature avec un CV |
| Candidatures | `GET` | `/api/job-applications/candidate/{id}` | Lister les candidatures d'un candidat |
| Candidatures | `GET` | `/api/job-applications/recruiter/{id}` | Lister les candidatures reçues par un recruteur |

Exemple de recherche d'offres :

```text
GET /api/job-offers/search?keyword=java&location=Casablanca&workMode=HYBRID&offerType=PFE&page=0&size=10
```

## Configuration du CV

Les CV sont enregistrés sur le disque dans le répertoire configuré par :

```properties
app.storage.resume-directory=uploads/resumes
```

Le répertoire `uploads/` est ignoré par Git. Seules les métadonnées du CV sont stockées dans PostgreSQL.

## Feuille de route

- [x] Monolithe Spring Boot, PostgreSQL et structure en couches.
- [x] Comptes, profils, offres, CV et création de candidatures.
- [x] Recherche, filtres et pagination des offres.
- [ ] CRUD complet des CV et candidatures.
- [ ] Changement de statut et historique des candidatures.
- [ ] Authentification JWT et autorisation par rôle.
- [ ] Compétences, offres sauvegardées et entretiens.
- [ ] Notifications internes et email optionnel.
- [ ] Documentation OpenAPI/Swagger et tests automatisés.
- [ ] Découpage en microservices, API Gateway et messagerie asynchrone.
- [ ] Docker Compose et frontend Angular.

## Règles de sécurité à venir

Les endpoints actuels utilisent encore temporairement certains identifiants, par exemple `recruiterProfileId`. Après l'ajout de JWT, ces identifiants proviendront du compte authentifié et les routes seront protégées selon les rôles `CANDIDATE`, `RECRUITER` et `ADMIN`.

## Auteur

Projet personnel de portfolio - DevHire.
