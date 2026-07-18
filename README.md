**Scholarly** is a full-stack web application built using Spring Boot, React, and PostgreSQL to manage **Scholarships** for universities/enterprise portals. This platform streamlines the complete scholarship lifecycle by enabling students to apply online, faculty to review their documents if OCR detects mismatch for re-confirmation and administrators to manage scholarships, eligibility, and much more.  
Here’s the list of all the **features** i inculcated in it:  
• 🔐 **User Authentication:** Secure signup, login, and logout handling powered by JWT (JSON Web Tokens).  
• 👤 **Role-Based Profiles:** Custom-tailored dashboard experiences for Students, Faculty Reviewers, and Admins.  
• 📄 **Automated OCR Verification:** Integrated PDF transcript parsing with automated CGPA extraction to instantly flag mismatches.  
• ⚡ **Instant Scholarship Auto-Approval:** Hands-free workflow that auto-approves applications when OCR results perfectly match self-declared inputs.  
• 📊 **Analytics Dashboard:** Real-time visual tracking of active students, pending audits, and global approval/rejection metrics.  
• 📨 **Console-Simulated Mailer:** Clean plain-text email logging directly in the terminal to trace application updates and verification alerts in real time.  
• 📦 **RESTful API Engine:** Robust backend communication driven by secure Spring Boot endpoints and protected routes.  
• 🛡️ **Production-Grade Polish:** Custom global exception handlers translating database/validation errors into clean, user-friendly messages.

**Screenshots:**
<img width="1920" height="1080" alt="Screenshot 2026-07-18 201230" src="https://github.com/user-attachments/assets/b7e11f8b-9b39-4ea3-9909-c42bb055b6ff" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 201330" src="https://github.com/user-attachments/assets/9f94e839-f826-4f4f-86dd-e0515c0a866e" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 201920" src="https://github.com/user-attachments/assets/756c5535-024e-4a36-ac00-1dac3c3d4df3" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 202115" src="https://github.com/user-attachments/assets/40af2695-8386-413c-8d0f-1745aedf8fe5" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 202153" src="https://github.com/user-attachments/assets/23f3a2f5-52cb-426e-b5e6-7048f6e82950" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 202234" src="https://github.com/user-attachments/assets/d0927920-0706-4d7e-a6ca-d3897a172a20" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 202253" src="https://github.com/user-attachments/assets/c32dc40b-efbb-42a8-990d-1f6e78f1091b" />  
<img width="1445" height="573" alt="Screenshot 2026-07-18 202507" src="https://github.com/user-attachments/assets/79f35cd3-8462-4b02-806a-d0dca24b400f" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 203152" src="https://github.com/user-attachments/assets/c2422b57-8fa5-4bbd-8805-451d7d4f238d" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 204020" src="https://github.com/user-attachments/assets/67852e32-ed3d-4d90-956f-7dc0ac0e3123" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 203416" src="https://github.com/user-attachments/assets/45bdc32d-bea6-42e7-b2a2-8337ede5e8c9" />  
<img width="1920" height="1080" alt="Screenshot 2026-07-18 204833" src="https://github.com/user-attachments/assets/c50027bf-34ea-4a8f-b687-c6765b7c3e42" />

**Experience scholarly live: Local Installation**  
Follow these steps to run Scholarly on your local development machine.  
Prerequisites  
• **Java Development Kit (JDK):** Version 21 or higher.  
• **Node.js:** Version 18 or higher (includes npm).  
• **Database:** PostgreSQL installed locally OR Docker (to run PostgreSQL inside a container).

**Step 1: Database Setup**  
You can set up the database using either Option A (Docker) **OR** Option B (Native PostgreSQL):  
**Option A: Using Docker Compose** 
If you already have Docker installed, simply run the f command in the root project folder:  
docker-compose up -d  
This automatically configures a PostgreSQL container running on port 5432 with username postgres, password secret, and creates the scholarly database.  
**Option B: Native PostgreSQL Setup**  
If you are running PostgreSQL directly on your machine:  
1. Open your PostgreSQL client
2.  Create a new database named scholarly:
      CREATE DATABASE scholarly;
3. Ensure the database matches the application defaults:
   • Username: postgres
   • Password: secret
   • Port: 5432 (If your local credentials differ, configure environment variables for DB_URL, DB_USER, and DB_PASSWORD before starting the application).
   **Step 2: Run the Spring Boot Backend**
1. Open a terminal and navigate to the backend directory:
    cd scholarly-backend
2. Build and launch the application using the Maven wrapper:
   .\mvnw spring-boot:run
3. The server will start on port 8080 (accessible at /api) and automatically initialize the database schema.
   **Step 3: Run the Vite Frontend**
1. Open a new terminal window and navigate to the frontend directory:
   cd scholarly-frontend
2. Install project dependencies:
   npm install
3. Start the development server:
   npm run dev
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
