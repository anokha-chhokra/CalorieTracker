# CalorieTracker

CalorieTracker is a full-stack calorie and nutrition tracking app built with a Spring Boot backend and a React + Vite frontend. It allows users to sign up, log food entries, view daily and weekly nutrition summaries, and manage their calorie goals.

## Features

- User registration and login with JWT-based authentication
- Daily calorie goal tracking and profile management
- Food search powered by USDA nutrition data
- Meal logging with calories, protein, carbs, and fat
- Daily dashboard with progress rings and macro breakdowns
- Weekly and monthly trends for calorie intake
- Food history and deletion support
- H2 file-backed database for local development

## Tech stack

### Backend
- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (jjwt)

### Frontend
- React 19
- Vite
- Recharts
- CSS custom UI

## Repository structure

```text
CalorieTracker/
├── backend/               # Spring Boot API server
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Controllers, services, models, security, config
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── frontend/              # React application
│   ├── src/
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   └── index.html
├── .gitignore
└── README.md
```

## How it works

The project is split into two main apps:

- The backend exposes REST APIs under `/api` for authentication, food search, entry management, profile updates, and dashboard metrics.
- The frontend runs as a single-page app and calls the backend for authentication and nutrition data.
- The backend stores user and food-entry data in an H2 database file located under `backend/data/calorietrack`.

## Prerequisites

Before running the app locally, make sure you have:

- Java 21+
- Maven or the included `mvnw` wrapper
- Node.js 18+
- npm

## Backend setup

From the repository root:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Important configuration

The backend configuration is in:

```text
backend/src/main/resources/application.properties
```

Default settings include:

- H2 database: `jdbc:h2:file:./data/calorietrack`
- JWT secret: `app.jwt-secret`
- CORS origin: `http://localhost:5173`
- USDA API key: `usda.api-key`

For local development, the app is preconfigured to work with the default dev values, but you should replace the JWT secret and API key before using it in any non-local environment.

## Frontend setup

From the repository root:

```bash
cd frontend
npm install
npm run dev
```

The frontend typically runs on:

```text
http://localhost:5173
```

## Main API endpoints

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Food search
- `GET /api/foods/search?query={foodName}`

### Entries
- `GET /api/entries` (or dashboard-related entry endpoints depending on implementation)
- `POST /api/entries`
- `DELETE /api/entries/{id}`

### Dashboard
- `GET /api/dashboard`

### Profile
- `PUT /api/profile`

The exact endpoint names may vary slightly by controller and service implementation, so refer to the backend code in `backend/src/main/java/org/belex/backend/controller` for the most current contract.

## Development notes

- The backend uses JWT authentication and protects most routes behind Spring Security.
- The frontend stores the token in browser storage and sends it on authenticated requests.
- The app is designed primarily for local development and demo use rather than production deployment.

## Demo flow

1. Start the backend.
2. Start the frontend.
3. Register a new account from the React app.
4. Search for a food item like "banana" or "chicken breast".
5. Select a food and add it to a meal for the day.
6. View your daily totals and trend charts on the dashboard.

## Future improvements

Possible enhancements include:

- Persistent user preferences and multi-user account management
- Improved USDA API integration and caching
- Better nutrition analytics and comparison views
- Mobile-friendly layouts and accessibility improvements
- Production-grade deployment config (Docker, CI/CD, environment separation)

## License

This project does not currently include a formal license file. If you plan to share or distribute it publicly, add an appropriate open-source license before release.
