# Part-Time Platform 🧑‍💼

A full-stack part-time job recruitment platform for college students and employers.

## Tech Stack

**Backend**
- Java 21 + Spring Boot 3.5
- MyBatis-Plus ORM
- MySQL 8.0
- Druid Connection Pool
- JWT Authentication (Auth0)
- WebSocket Real-time Chat
- Knife4j API Docs

**Frontend**
- Vue 3
- Vite
- Element Plus
- Axios
- WebSocket Client

## Features

- 👥 **Three roles** — Student, Employer, Admin
- 📋 **Job management** — Post, browse, search, apply for part-time jobs
- 💬 **Real-time chat** — WebSocket-based instant messaging between students and employers
- 🏢 **Company profiles** — Employer info and rating
- ⚖️ **Dispute resolution** — Admin-managed complaint system
- 👑 **Admin dashboard** — User management, job approval, dispute handling

## Getting Started

### Prerequisites

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven (or use the included wrapper)

### Database Setup

```sql
CREATE DATABASE bishe_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then run `src/main/resources/mock_data_init.sql` to initialize tables and test data.

### Configuration

Copy the config template and fill in your database credentials:

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Edit `application.yml` with your MySQL username and password.

### Run Backend

```bash
./mvnw spring-boot:run
```

The API server starts at http://localhost:9999

### Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts at http://localhost:5173

### API Documentation

Once the backend is running, visit http://localhost:9999/doc.html for the Knife4j API docs.

## Project Structure

```
part-time/
├── frontend/                    # Vue 3 frontend
│   └── src/
│       ├── api/                 # API calls
│       ├── components/          # Reusable components
│       ├── views/               # Page views
│       ├── router/              # Route config
│       ├── store/               # Pinia stores
│       └── utils/               # Utilities (WebSocket, etc.)
├── src/
│   └── main/
│       ├── java/com/example/bishe_demo/
│       │   ├── config/          # App config (WebSocket, Swagger, etc.)
│       │   ├── controller/      # REST controllers
│       │   ├── service/         # Business logic
│       │   ├── mapper/          # MyBatis mappers
│       │   ├── entity/          # Data models
│       │   ├── websocket/       # Chat WebSocket handler
│       │   ├── interceptor/     # Login interceptor
│       │   └── utils/           # Utilities (JWT, MD5, etc.)
│       └── resources/           # Config files & SQL
├── pom.xml                      # Maven build
└── package.json                 # Root npm dependencies
```

## License

MIT License — see [LICENSE](LICENSE)
