# DevHire - V2 Sécurité JWT

V2 ajoute l'authentification et l'autorisation à DevHire. Le backend reste un monolithe Spring Boot, mais les opérations sensibles utilisent désormais l'identité contenue dans un JWT plutôt que des identifiants de propriétaire fournis par le client.

## Objectifs de V2

- Authentifier les utilisateurs par email et mot de passe.
- Générer des JWT signés et limités dans le temps.
- Protéger les routes avec Spring Security.
- Autoriser les accès selon les rôles `CANDIDATE`, `RECRUITER` et `ADMIN`.
- Vérifier la propriété métier dans les services : un candidat ne gère que ses ressources, un recruteur ne gère que ses offres et candidatures reçues.
- Bloquer les comptes désactivés.

## Composants de sécurité

```text
LoginRequest
    -> AuthService
    -> AuthenticationManager
    -> DaoAuthenticationProvider
    -> CustomUserDetailsService
    -> BCrypt verification
    -> JwtService
    -> AuthResponse (Bearer token)

Subsequent request
    -> Authorization: Bearer <token>
    -> JwtAuthenticationFilter
    -> SecurityContext
    -> SecurityConfig role rules
    -> Service ownership checks
```

### Classes principales

| Classe | Responsabilité |
|---|---|
| `SecurityConfig` | API stateless, règles d'accès, provider d'authentification et ordre du filtre JWT. |
| `CustomUserDetailsService` | Charge un utilisateur par email et expose son rôle à Spring Security. |
| `JwtService` | Génère, lit et valide les JWT signés en HMAC-SHA256. |
| `JwtAuthenticationFilter` | Lit l'en-tête Bearer et place l'utilisateur dans le contexte de sécurité. |
| `AuthService` | Vérifie les identifiants et renvoie la réponse d'authentification. |
| `AuthController` | Expose la connexion et la consultation du compte connecté. |
| `JwtProperties` | Valide la clé et l'expiration JWT depuis la configuration. |

## Dépendances ajoutées

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
```

## Configuration JWT

Dans `application.properties` local :

```properties
app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration-ms=3600000
```

`3600000` correspond à une durée de validité d'une heure.

### Générer une clé secrète

Sous PowerShell :

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

Définissez ensuite la valeur produite dans une variable utilisateur Windows :

```powershell
[Environment]::SetEnvironmentVariable(
  "JWT_SECRET",
  "VOTRE_CLE_BASE64_GENEREE",
  "User"
)
```

Redémarrez le terminal ou l'IDE après cette commande.

> Ne publiez jamais `JWT_SECRET`, les mots de passe PostgreSQL ou des tokens JWT dans GitHub, Postman partagé, captures d'écran ou messages.

## Routes publiques

| Méthode | Route | Description |
|---|---|---|
| `POST` | `/api/users` | Inscription d'un candidat ou recruteur. |
| `POST` | `/api/auth/login` | Connexion et obtention d'un token JWT. |
| `GET` | `/api/job-offers/**` | Consultation et recherche publique des offres. |

## Connexion

```text
POST /api/auth/login
```

```json
{
  "email": "sara@example.com",
  "password": "DevHire@2026"
}
```

Réponse :

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": 1,
  "email": "sara@example.com",
  "role": "CANDIDATE"
}
```

Pour les requêtes protégées, envoyez :

```http
Authorization: Bearer <accessToken>
```

Dans Postman, utilisez l'onglet **Authorization**, choisissez **Bearer Token**, puis collez seulement la valeur du token.

## Règles d'accès

| Ressource | Public | CANDIDATE | RECRUITER | ADMIN |
|---|---:|---:|---:|---:|
| Inscription / connexion | Oui | Oui | Oui | Oui |
| Consultation des offres | Oui | Oui | Oui | Oui |
| Profil candidat et CV | Non | Oui, uniquement les siens | Non | À compléter |
| Profil recruteur | Non | Non | Oui, uniquement le sien | À compléter |
| Création / modification / suppression d'offre | Non | Non | Oui, uniquement ses offres | À compléter |
| Créer / modifier / supprimer une candidature | Non | Oui, uniquement les siennes | Non | À compléter |
| Candidatures reçues et changement de statut | Non | Non | Oui, uniquement pour ses offres | À compléter |
| Liste globale des utilisateurs | Non | Non | Non | Oui |

## Routes sécurisées principales

### Compte connecté

```text
GET   /api/auth/me
PUT   /api/users/me
PATCH /api/users/me/deactivate
```

Après un changement d'email ou de mot de passe, reconnectez-vous pour obtenir un nouveau token. Après désactivation, le compte est refusé à la prochaine authentification.

### Profil candidat et CV

```text
GET  /api/candidate-profiles/me
PUT  /api/candidate-profiles/me

POST   /api/resumes
GET    /api/resumes
GET    /api/resumes/{resumeId}
GET    /api/resumes/{resumeId}/download
DELETE /api/resumes/{resumeId}
```

Le candidat est déduit du JWT. Il ne transmet donc jamais `candidateProfileId` pour gérer ses propres ressources.

### Profil recruteur et offres

```text
GET /api/recruiter-profiles/me
PUT /api/recruiter-profiles/me

POST   /api/job-offers
PUT    /api/job-offers/{id}
DELETE /api/job-offers/{id}
```

Le service vérifie que le recruteur authentifié est propriétaire de l'offre avant toute modification ou suppression.

### Candidatures

```text
POST   /api/job-applications
GET    /api/job-applications/me
PUT    /api/job-applications/{id}
DELETE /api/job-applications/{id}

GET   /api/job-applications/received
PATCH /api/job-applications/{id}/status
```

La candidature est créée à partir du candidat connecté. Le CV sélectionné doit appartenir à ce candidat. Le recruteur doit être propriétaire de l'offre concernée pour consulter les candidatures reçues ou modifier leur statut.

## Vérifications de sécurité implémentées

- Mots de passe hachés avec BCrypt ; aucun mot de passe ou hash n'est retourné par l'API.
- JWT signé par une clé Base64 d'au moins 256 bits.
- Expiration des tokens.
- API sans session serveur (`STATELESS`).
- Basic Auth et formulaire Spring Security désactivés.
- CSRF désactivé car l'API utilise l'en-tête `Authorization` et non des cookies de session.
- Rôles exposés à Spring Security sous la forme `ROLE_CANDIDATE`, `ROLE_RECRUITER` et `ROLE_ADMIN`.
- Compte désactivé rejeté par `CustomUserDetailsService`.
- Contrôles de propriété dans les services pour éviter l'accès à une ressource d'un autre utilisateur.
- Réponses d'erreur centralisées pour validation, authentification, ressources absentes et accès refusé.

## Tests Postman recommandés

| Cas | Résultat attendu |
|---|---|
| Connexion avec identifiants valides | `200 OK` et token Bearer |
| Connexion avec mauvais mot de passe | `401 Unauthorized` |
| Route protégée sans token | `401 Unauthorized` |
| Token candidat sur une route recruteur | `403 Forbidden` |
| Recruteur tentant de modifier une offre d'un autre recruteur | Refus métier |
| Candidat tentant d'utiliser le CV d'un autre candidat | Refus métier |
| Candidat tentant d'accéder à une candidature étrangère | `403 Forbidden` |
| Compte désactivé tentant de se connecter | `401 Unauthorized` |

## Limites et suite du projet

V2 implémente le socle JWT. Les prochaines évolutions prévues sont :

- tests automatisés de sécurité et tests d'intégration ;
- rate limiting sur la connexion et protection contre les tentatives répétées ;
- gestion optionnelle du renouvellement/révocation des tokens ;
- Swagger / OpenAPI ;
- historique de statuts, entretiens et notifications ;
- microservices, API Gateway, RabbitMQ/Kafka, Docker et Angular.

## Lancement

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

L'API est disponible sur :

```text
http://localhost:8080
```
