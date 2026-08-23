# Famous Quote Quiz — Backend

Spring Boot (Kotlin) REST API for the Famous Quote Quiz client. It handles login/authentication and drives the quiz
logic (question selection, answer options and answer validation) for both quiz modes.

## Tech stack

- Kotlin 2.3, Spring Boot 4.1 (Spring MVC, Spring Data JPA, Bean Validation)
- H2 file database (`./data/quizdb`), schema created by Hibernate on startup
- Spring Security as an OAuth2 resource server: stateless HS256 JWT authentication, BCrypt password hashing
- springdoc-openapi for the generated OpenAPI document and Swagger UI

## Running

```bash
./gradlew bootRun
```

The API is served on `http://localhost:8080`. On first start 32 famous quotes and a demo user are seeded.

- Demo credentials: `demo@quiz.com` / `password123`
- Swagger UI: `http://localhost:8080/swagger-ui.html`, OpenAPI document: `http://localhost:8080/v3/api-docs`
- H2 web console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/quizdb`, user `sa`, no password)

In Swagger UI, call `POST /api/auth/login` first, then paste the returned token into the **Authorize** dialog to try
the secured endpoints.

Tests: `./gradlew test`. Build a runnable jar: `./gradlew bootJar` then `java -jar build/libs/*.jar`.

Configurable properties (`src/main/resources/application.properties`):

| Property | Default | Description |
| --- | --- | --- |
| `auth.secret` | dev key, override with `AUTH_SECRET` | HMAC key the JWTs are signed with, at least 32 characters |
| `auth.token-ttl` | `15m` | Lifetime of an issued access token (JWT) |
| `auth.refresh-token-ttl` | `30d` | Lifetime of an issued refresh token |
| `quiz.questions-per-session` | `10` | Questions per quiz session |
| `quiz.multiple-choice-options` | `3` | Options per multiple choice question |

Android emulators reach the host machine at `http://10.0.2.2:8080`; for a physical device use the machine's LAN IP.

## Authentication

Login and refresh return two tokens:

- an **access token**, a JWT signed with HS256 whose subject is the user id, valid for 15 minutes and sent with every
  request as `Authorization: Bearer <token>`
- a **refresh token**, an opaque random value stored in the database, valid for 30 days and only ever sent to
  `POST /api/auth/refresh` or `POST /api/auth/logout`

Spring Security's resource server filter chain verifies the access token's signature and expiry and rejects anything
else with a `401` in the same JSON shape as the other errors. Controllers receive the authenticated user through a
`@CurrentUser` parameter, which loads the entity for the subject of the validated token.

Recommended client behaviour: store both tokens, and when a call returns `401`, call `/api/auth/refresh` once, retry
the original request with the new access token, and only fall back to the login screen if the refresh also fails.
Checking `expiresAt` before a request and refreshing early avoids most `401`s altogether. This keeps a long quiz alive
even if the access token expires halfway through.

Every refresh rotates the refresh token: the old value is deleted and a new one is returned, so a leaked refresh token
stops working as soon as the real device refreshes. `POST /api/auth/logout` deletes the refresh token, which is what
makes logout effective server side; the current access token still works until it expires, so the client should drop
it as well.

## API overview

The same endpoints are browsable and callable in Swagger UI; the summary below is the quick reference.

### `POST /api/auth/login` → `200`

Authenticates a seeded user. `401` if the credentials are invalid, `400` with `fieldErrors` if a field is empty.

```json
{ "email": "demo@quiz.com", "password": "password123" }
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJkZW1vQHF1aXouY29tIn0...",
  "expiresAt": "2026-08-13T12:39:54Z",
  "refreshToken": "qE0m9v3s3nJ8xO1a...",
  "refreshTokenExpiresAt": "2026-09-12T12:39:54Z",
  "user": { "id": 1, "email": "demo@quiz.com", "displayName": "Demo User", "memberSince": "2026-08-13T12:47:01.552815Z" }
}
```

### `POST /api/auth/refresh` → `200`

Exchanges a refresh token for a fresh access token and a rotated refresh token. Same response body as login. `401` if
the refresh token is unknown, already rotated, revoked or expired.

```json
{ "refreshToken": "qE0m9v3s3nJ8xO1a..." }
```

### `POST /api/auth/logout` → `204`

Deletes the refresh token so it can no longer be used. Takes the same body as refresh and needs no access token.

### `POST /api/quiz/sessions` → `201`

Starts a new session of 10 randomly picked quotes. Call it again to restart the quiz, e.g. when the mode is changed
on the settings tab. `mode` is `BINARY` (default) or `MULTIPLE_CHOICE`.

```json
{ "mode": "BINARY" }
```

```json
{
  "sessionId": "54274b23-0dbd-45e6-87c7-ad6b83a439a3",
  "mode": "BINARY",
  "totalQuestions": 10,
  "answeredQuestions": 0,
  "completed": false,
  "currentQuestion": {
    "questionNumber": 1,
    "totalQuestions": 10,
    "quote": "It always seems impossible until it's done.",
    "proposedAuthor": "Martin Luther King Jr.",
    "options": ["YES", "NO"]
  }
}
```

In `BINARY` mode `proposedAuthor` is the author the user has to confirm or reject ("Who said it?") and is the real
author roughly half of the time; the options are `YES`/`NO`. In `MULTIPLE_CHOICE` mode `proposedAuthor` is `null` and
`options` holds three author names, exactly one of which is correct. The correct answer is never sent to the client
before the answer is submitted.

### `POST /api/quiz/sessions/{sessionId}/answers` → `200`

Submits the answer for the current question. `answer` must be one of the options of that question
(`YES`/`NO` in binary mode, an author name in multiple choice mode).

```json
{ "answer": "YES" }
```

```json
{
  "correct": true,
  "correctAuthor": "Nelson Mandela",
  "message": "Correct! The right answer is: Nelson Mandela",
  "sessionCompleted": false,
  "nextQuestion": { "questionNumber": 2, "totalQuestions": 10, "quote": "...", "proposedAuthor": "Socrates", "options": ["YES", "NO"] }
}
```

`message` is ready to be shown in the client's result dialog. When the tenth answer is submitted,
`sessionCompleted` is `true` and `nextQuestion` is `null`.

## Errors

Errors share one JSON shape. `fieldErrors` is filled for request validation failures, which lets the client map
messages to the matching input field (e.g. on the login screen).

```json
{ "status": 400, "message": "Validation failed", "fieldErrors": { "email": "Enter a valid email" } }
```

| Status | When |
| --- | --- |
| `400` | Validation failure, answer not among the options, or session already completed |
| `401` | Missing, malformed or expired access token; unknown, rotated, revoked or expired refresh token; wrong login credentials |
| `404` | Quiz session does not exist or belongs to another user |

## Architecture

Hexagonal (ports and adapters). Use cases live in the application layer and talk to storage through outbound ports;
HTTP and JPA sit at the edges as adapters.

A request such as `POST /api/quiz/sessions` goes REST controller → application service → domain → repository port →
Spring Data adapter → H2.

```
src/main/kotlin/com/quiz/famousquotequizbackend
├── domain                 JPA entities and quiz rules
│   ├── auth               RefreshToken
│   ├── quiz               QuizSession, QuizQuestion, QuizMode, BinaryAnswer
│   ├── quote              Quote
│   └── user               User
├── application            use cases; no Spring Data or HTTP
│   ├── port/driven        repository interfaces (User, Quote, QuizSession, RefreshToken)
│   ├── service            AuthService, QuizService, JwtService
│   └── dto                response DTOs (AuthResponse, SessionResponse, QuestionResponse, …)
├── adapter
│   ├── driving/rest       AuthController, QuizController, request DTOs, @CurrentUser
│   └── driven/persistence Spring Data JpaRepository types + adapters that implement the ports
└── infrastructure
    ├── common             ApiException hierarchy, GlobalExceptionHandler, ErrorResponse
    └── config             SecurityConfig, WebConfig, OpenApiConfig, DataSeeder, Auth/Quiz properties
```

- **Domain** owns `User`, `Quote`, `QuizSession` and related types. Sessions know how to pick the current question and
  count answers. These classes are also JPA `@Entity` mappings.
- **Application** runs the use cases. `AuthService` handles login, refresh-token rotation and logout; `QuizService`
  starts a session and scores answers. Both depend on the `port/driven` interfaces, not on Spring Data.
- **Driving adapters** are the HTTP API. Incoming request bodies live next to the controllers; outgoing responses live
  in `application/dto`. There are no inbound ports — controllers call the services directly.
- **Driven adapters** implement the ports by wrapping Spring Data repositories.
- **Infrastructure** is Spring wiring: JWT resource-server security, CORS, OpenAPI, H2, seeding, and the shared error
  JSON shape.

Integration tests under `src/test/.../auth` and `src/test/.../quiz` cover the HTTP flows end to end.
