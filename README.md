<h1>Notebook Application</h1>

A Spring Boot REST API for managing notes with tagging, filtering, and text analysis features.
Features:

    📝 Create, read, update, and delete notes

    🏷️ Tag notes with categories (Business, Personal, Important)

    🔍 Filter notes by tags

    📊 Get word statistics for notes

    🗃️ MongoDB database storage

    🐳 Docker containerization

Prerequisites

  - Docker and Docker Compose installed on your system

Quick Start

  - Clone or download the project to your local machine
  - Navigate to the project directory:

```bash 
cd Notebook
```

Start the application:
```bash
docker compose up --build
```
Wait for the containers to start. You should see output indicating:

```
Started NotebookApplication in x.xxx seconds
```

Access the application at: http://localhost:8080


<h1>API Endpoints</h1>
Notes Management

    POST /notes - Create a new note

    GET /notes - Get all notes (paginated)

    GET /notes/{id} - Get a specific note by ID

    PUT /notes/{id} - Update a note

    DELETE /notes/{id} - Delete a note

Tag Filtering

    GET /notes/tag - Get notes filtered by tags (paginated, request body contains tags list)

Statistics

    GET /notes/{id}/stats - Get word statistics for a note
