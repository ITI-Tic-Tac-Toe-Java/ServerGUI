# Tic-Tac-Toe Server GUI

A robust, multi-threaded Java server application equipped with a Graphical User Interface (GUI) to manage, monitor, and coordinate real-time multiplayer Tic-Tac-Toe games. This repository represents the server-side architecture responsible for handling client connections, managing game states, and persisting player data.

---

## Features

- **Server Control Dashboard:** Toggle the server state (Start/Stop) cleanly with a centralized switch.
- **Real-time Live Monitoring:** Dynamic charts (e.g., PieChart) tracking the live distribution of active, idle, and offline players.
- **Client Session Management:** Securely handles multi-client synchronization using TCP/IP Sockets and thread pools.
- **Database Integration:** Seamlessly interacts with a relational database (MySQL) to persist user profiles, credentials, match histories, and scores.
- **Game Coordination:** Matches online players, coordinates turns, validates moves, and updates global leaderboards instantly.

---

## Screenshots

<p align = "center" >
<img width="800" src="https://github.com/user-attachments/assets/ea944a66-14ff-4076-a816-771771e0cfb2" />

</p>

## 🏗️ Architecture

The server adheres to scalable software design principles, isolating the user interface from network processing and data access layers:

* **GUI / View Layer:** Built using JavaFX (FXML) implementing a responsive layout to visualize system logs and active connection metrics.
* **Business Logic / Controller:** Manages multi-threaded communication loops, utilizing a client handler pattern to isolate individual player socket pipes.
* **DataAccess Layer (DAO):** Handles connection pooling and secure transactions to fetch/update game state records dynamically from the database.

---

## 🛠️ Tech Stack & Prerequisites


Before setting up the project, ensure you have the following installed:

* **Java Development Kit (JDK):** Version 8 or higher (e.g., JDK 11 / JDK 17)
* **JavaFX SDK:** (If using modular Java 11+)
* **Database:** MySQL Server
* **Build Tool:** Maven or Gradle (or standard IntelliJ/Eclipse project configuration)

---

## 📦 Database Setup

1. Open your MySQL client (e.g., MySQL Workbench or CLI).
2. Create a new database named `tictactoe`:
   ```sql
   CREATE DATABASE tictactoe;
   USE tictactoe;
Import the database schema or run your project's table initialization scripts (DB_tables.sql if provided).

Update the database credentials in the server configurations (src/main/resources/config.properties or inside your Database Connection class):

Properties
db.url=jdbc:mysql://localhost:3306/tictactoe
db.username=root
db.password=your_password
⚙️ Getting Started
Cloning the Repository
Bash
git clone [https://github.com/ITI-Tic-Tac-Toe-Java/ServerGUI.git](https://github.com/ITI-Tic-Tac-Toe-Java/ServerGUI.git)
cd ServerGUI
Building & Running the App
Option 1: Using an IDE (IntelliJ IDEA / NetBeans / Eclipse)
Open the cloned folder as a Maven/Gradle project in your favorite IDE.

Ensure the JDK is correctly mapped in your Project Structure.

Locate the main application class containing the main method (e.g., ServerApp.java).

Run the class.

Option 2: Running via JAR File
If you have a pre-built executable JAR file:

Bash
java -jar ServerGUI.jar
🖥️ Usage Guide
Launch: Open the application. By default, the server initializes in a Stopped state.

Start Server: Click the primary Toggle Button to boot up the server. The connection listener will begin binding to the assigned port (e.g., 5005).

Monitor Traffic: Watch the dashboard populate live charts showing real-time statistics as Client applications connect, register, authenticate, and enter match lobbies.

Shutdown Safely: Disconnecting or toggling the server off will safely close open socket streams and notify active clients before cleaning up database connections.

🤝 Team Members
Developed with ❤️ as part of the ITI Project collaboration.

- Abdullh Gaber
- Elbaraa
- Thaowpsta
- Esraa Ehab
