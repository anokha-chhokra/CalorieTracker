# CalorieTracker

CalorieTracker is a full-stack calorie and nutrition tracking application built with a Spring Boot backend and a React + Vite frontend. It helps users register accounts, set a daily calorie goal, search food nutrition data, log meals, and track their intake over time.

## Features

- User registration and login with JWT authentication
- Daily calorie goal and profile management
- Food search backed by USDA nutrition data
- Meal logging with calories, protein, carbs, and fat
- Dashboard showing remaining calories and macro totals
- Weekly and monthly nutrition trend charts
- History view for recent food entries
- Local file-based H2 database for easy development setup

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
- CSS-based UI

## Repository structure

```text
CalorieTracker/
├── backend/               # Spring Boot API server
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Application code
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
├── README.md
└── LICENSE (if added later)
```

## Setup guide

Follow the steps below to run the app locally.

### Prerequisites

Make sure you have the following installed on your machine:

- Java 21 or newer
- Maven (or use the included `mvnw` wrapper)
- Node.js 18 or newer
- npm

### 1) Clone the repository

```bash
git clone https://github.com/anokha-chhokra/CalorieTracker.git
cd CalorieTracker
```

### 2) Start the backend

From the project root:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows, use:

```bash
cd backend
mvnw.cmd spring-boot:run
```

The backend should start on:

```text
http://localhost:8080
```

### 3) Start the frontend

Open a new terminal and run:

```bash
cd frontend
npm install
npm run dev
```

The frontend should start on:

```text
http://localhost:5173
```

### 4) Open the app

Visit:

```text
http://localhost:5173
```

Then create an account or sign in to begin tracking calories.

## Configuration

The backend settings are stored in:

```text
backend/src/main/resources/application.properties
```

Important values include:

- `spring.datasource.url` → H2 local database path
- `app.jwt-secret` → JWT signing secret
- `app.jwt-expiration-ms` → JWT expiration time
- `app.cors.allowed-origin` → frontend origin for CORS
- `usda.api-key` → USDA FoodData Central API key

### Default local configuration

By default, the project is set up for local development with:

```properties
spring.datasource.url=jdbc:h2:file:./data/calorietrack;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
app.cors.allowed-origin=http://localhost:5173
usda.api-key=DEMO_KEY
```

For production or shared environments, update these values to secure and environment-specific settings.

## Running tests

Backend tests can be run with:

```bash
cd backend
./mvnw test
```

## Main API endpoints

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Food search
- `GET /api/foods/search?query={foodName}`

### Dashboard and nutrition
- `GET /api/dashboard`

### Entry management
- `POST /api/entries`
- `GET /api/entries`
- `DELETE /api/entries/{id}`

### Profile
- `PUT /api/profile`

## How it works

The project is split into two main parts:

- The backend exposes REST APIs, secures routes using Spring Security, authenticates users with JWT, and persists data in H2.
- The frontend is a single-page React app that manages authentication, food logging, dashboard display, and dietary tracking.
- The app fetches USDA nutrition metadata for food searches and calculates per-serving totals based on grams entered by the user.

## Development notes

- The backend is designed mainly for local development and demo usage.
- Most routes are protected behind authentication.
- The frontend stores the token in browser storage and includes it in authenticated requests.

## Demo flow

1. Run the backend.
2. Run the frontend.
3. Create an account.
4. Search for a food item such as "banana" or "chicken breast".
5. Add it to a meal entry for the day.
6. View your progress and trends on the dashboard.

## Future improvements

Possible enhancements include:

- Better nutrition analytics and comparison views
- Improved USDA API caching and error handling
- More robust deployment configuration
- Docker support for easier setup
- Better mobile and accessibility improvements

## License

This repository does not currently include a formal license file. If you plan to share or distribute it publicly, add an appropriate open-source license before release.
