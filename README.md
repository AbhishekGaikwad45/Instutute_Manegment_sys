Institute Management System
<img width="1920" height="1080" alt="Screenshot 2025-11-30 230724" src="https://github.com/user-attachments/assets/527678c0-5228-424a-adf4-eaae1564ae21" />

<img width="1908" height="830" alt="Screenshot 2025-12-12 193620" src="https://github.com/user-attachments/assets/04a446ec-6578-47d1-ad5e-826ed96eaee1" />
<img width="1910" height="842" alt="Screenshot 2025-12-12 193518" src="https://github.com/user-attachments/assets/46b28225-30e1-4908-8674-3a3615b125be" />
<img width="1919" height="843" alt="Screenshot 2025-12-12 193453" src="https://github.com/user-attachments/assets/f1c560af-032a-4e77-8f17-d0d57bfba742" />



A full-stack Spring Boot + React web application for managing students, faculty, batches, attendance, and payments in an institute. The system includes role-based login, CRUD operations, and a clean REST API.

 Features
 Faculty /  Student /  Counsellor / Admin

Role-based login system (faculty login with email + birthDate method supported)

Add, update, delete, and view:

Students

Faculty

Batches

Assign students to batches

Take & view attendance

Student fee/payment management

CORS-enabled backend for React

Responsive UI built with React

Tech Stack
Frontend

React.js

React Router DOM

React Toastify

Fetch / Axios

Backend

Spring Boot

Spring Web

Spring Data JPA

MySQL 

ModelMapper

Validation API

Server Communication

React → Spring Boot REST API (JSON)

 Installation & Setup

1️ Backend Setup (Spring Boot)
Requirements

Java 17+

Maven

MySQL database

Postman (optional)

Steps
cd backend
mvn clean install
mvn spring-boot:run


Default runs on:

http://localhost:8080

2️ Frontend Setup (React)
Requirements

Node.js

npm or yarn

Steps
cd frontend
npm install
npm start


Runs on:

http://localhost:3000
