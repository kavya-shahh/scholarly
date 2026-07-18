**Scholarly** is a full-stack web application built using Spring Boot, React, and PostgreSQL to manage **Scholarships** for universities/enterprise portals. This platform streamlines the complete scholarship lifecycle by enabling students to apply online, faculty to review their documents if OCR detects mismatch for re-confirmation and administrators to manage scholarships, eligibility, and much more.

Here’s the list of all the features I inculcated in it:

• 🔐 **User Authentication:** Secure signup, login, and logout handling powered by JWT (JSON Web Tokens).
• 👤 **Role-Based Profiles:** Custom-tailored dashboard experiences for Students, Faculty Reviewers, and Admins.
• 📄 **Automated OCR Verification:** Integrated PDF transcript parsing with automated CGPA extraction to instantly flag mismatches.
• ⚡ **Instant Scholarship Auto-Approval:** Hands-free workflow that auto-approves applications when OCR results perfectly match self-declared inputs.
• 📊 **Analytics Dashboard:** Real-time visual tracking of active students, pending audits, and global approval/rejection metrics.
• 📨 **Console-Simulated Mailer:** Clean plain-text email logging directly in the terminal to trace application updates and verification alerts in real time.
• 📦 **RESTful API Engine:** Robust backend communication driven by secure Spring Boot endpoints and protected routes.
• 🛡️ **Production-Grade Polish:** Custom global exception handlers translating database/validation errors into clean, user-friendly messages.

**Screenshots: **


**Experience scholarly live: Local Installation**
Follow these steps to run Scholarly on your local development machine.
Prerequisites
• **Java Development Kit (JDK):** Version 21 or higher.
• **Node.js:** Version 18 or higher (includes npm).
• **Database:** PostgreSQL installed locally OR Docker (to run PostgreSQL inside a container).

**Step 1: Database Setup**
You can set up the database using either Option A (Docker) or Option B (Native PostgreSQL):

**Option A: Using Docker Compose** 
If you already have Docker installed, simply run the f command in the root project folder:
```bash
docker-compose up -d
```
This automatically configures a PostgreSQL container running on port 5432 with username postgres, password secret, and creates the scholarly database.

**Option B: Native PostgreSQL Setup**
If you are running PostgreSQL directly on your machine:
1. Open your PostgreSQL client
2. Create a new database named scholarly:
```sql
CREATE DATABASE scholarly;
```
3. Ensure the database matches the application defaults:
• Username: postgres
• Password: secret
• Port: 5432 (If your local credentials differ, configure environment variables for DB_URL, DB_USER, and DB_PASSWORD before starting the application).

**Step 2: Run the Spring Boot Backend**
1. Open a terminal and navigate to the backend directory:
```bash
cd scholarly-backend
```
2. Build and launch the application using the Maven wrapper:
```bash
# Windows (PowerShell/CMD):
.\mvnw spring-boot:run

# macOS/Linux:
./mvnw spring-boot:run
```
3. The server will start on port 8080 (accessible at /api) and automatically initialize the database schema.

**Step 3: Run the Vite Frontend**
1. Open a new terminal window and navigate to the frontend directory:
```bash
cd scholarly-frontend
```
2. Install project dependencies:
```bash
npm install
```
3. Start the development server:
```bash
npm run dev
```
4. Access the web application in your browser at http://localhost:5173.

**Tech Stack:**
💻 Frontend
• **React.js:** Dynamic component-based framework for UI rendering.
• **Vite:** Modern frontend development server and bundler.
• **Vanilla CSS:** Custom styling, grid/flexbox layouts, modern transitions, and floating notifications.
• **HTML5 & ES6+ JavaScript:** Client structure and application state logic.
⚙️ Backend
• **Spring Boot:** Framework for the core REST API engine.
• **Spring Security:** Role-based access control (Student, Faculty, Admin) and stateless session filters.
• **JWT (JSON Web Tokens):** Secure token-based user authentication and verification.
• **Spring Data JPA & Hibernate:** Object-relational mapping (ORM) for data persistence.
• **Apache PDFBox:** Document processing engine used for text extraction and grade verification.
• **Maven:** Backend dependency management and compilation pipeline.
🗄️ Database & DevOps
• **PostgreSQL:** Relational database storing user profiles, scholarship data, and application tracking records.
• **Docker & Docker Compose:** Database containerization and orchestration for standard dev setups.
