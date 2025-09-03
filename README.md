# Business Requirements – Personal Library Organizer

## 1. Project Overview
The **Personal Library Organizer** is a web application designed to help users catalog, search, and manage their personal book collections.  
Users can enter a book’s **ISBN** to automatically retrieve its details from a public book information API (Google Books API) and store it in a **PostgreSQL** database. The application allows easy searching and filtering of the collection by **author**, **title** or **genre**.

## 2. Objectives
- Provide a **simple and intuitive interface** for managing personal book collections.
- Automate the process of retrieving and storing book details using ISBN numbers.
- Enable **fast and accurate searching** of the library collection.
- Demonstrate integration of **Spring Boot**, **Maven**, and **PostgreSQL** in a full-stack application.
- Serve as a **portfolio project** showcasing REST API integration, database management, and search functionality.

## 3. Functional Requirements

### 3.1 Book Entry
- User provides a valid ISBN (validated by check digit) via the application’s interface.
- The system retrieves book details (title, author, genre, publication date, publisher, description, language, page count) from a public API.
- Retrieved details are stored in the PostgreSQL database.
- Duplicate entries are prevented (based on ISBN).
- The description must always be stored in the same language as the book edition. If the description is provided in another language, the system will translate it before saving (leveraging LibreTranslate API).

### 3.2 Search & Filtering
- User can search the database by:
    - **Title**
    - **Author**
    - **Genre**
    - **ISBN**
- Search results are displayed in a list with key book details.

### 3.3 Data Management
- User can view the entire collection in a list format.
- User can delete entries from the collection.
- User can update book details if API data is incomplete or incorrect.

## 4. Non-Functional Requirements

### 4.1 Scalability
- System should be designed to handle future expansion (e.g., adding more search filters, user authentication, or exporting data).

### 4.2 Security
- Application should validate ISBN input to avoid invalid API requests.
- Database credentials and API keys must be stored securely (e.g., environment variables).

### 4.4 Technology Stack
- **Backend:** Spring Boot (Java)
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **External API:** Google Books API, Open Library API & LibreTranslate
- **Deployment:** Docker support for portability

## 5. Success Criteria
The project will be considered successful when:
1. Users can add books by ISBN and have details stored in the database automatically.
2. Search functionality works reliably for author, title and genre.
3. The application runs locally with minimal setup using clear installation instructions in the README.
4. Code is well-structured, documented, and version-controlled on GitHub.

## 6. Run Instructions

### 6.1 Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed
- Get a [Google Api Key](https://console.cloud.google.com/apis/credentials)

### 6.2 Setup

1. Create a `.env` file in the project root with the following variables:

  ```env
  POSTGRES_USER=your_username
  POSTGRES_PASSWORD=your_password
  POSTGRES_DB=db_name
  GOOGLE_BOOKS_API_KEY=your_api_key
  ```
2. Build and start the application with docker compose

  ```
  docker compose up --build -d
  ```

### 6.3 API Testing with Bruno

This project includes a [Bruno](https://www.usebruno.com/) collection for testing the REST API.

- Install Bruno
- Import the collection from [the file](personal-library-bruno-collection.json).
- Update the environment variables in Bruno to match your setup.
- Use the provided requests to test the API endpoints.
