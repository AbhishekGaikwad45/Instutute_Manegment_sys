Institute Management System

![counseller Dashboard](https://github.com/user-attachments/assets/8ea06cf0-d91c-4b35-8457-fdb615c1ce11)
![Faculty Dashboard](https://github.com/user-attachments/assets/7c2525fe-c660-4f9b-8fce-6eb04d242955)
![Student Dashboard](https://github.com/user-attachments/assets/290e900f-f234-497d-892c-8097d5a72c14)


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

MySQL / PostgreSQL / Any relational DB

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
