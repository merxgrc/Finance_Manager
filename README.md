# Finance_Manager Web App
The Finance_Manager Web App is a personal finance tool that allows users to link their bank accounts, fetch account data including balances and transactions, and helps the track their spendiing and budgeting. 

## Project Status
This project is a personal project that I built as my first solo full stack project. My goal was to make something that can potentially bring value to real users while providing me with hands-on experience in building both the frontend and backend, as well as integrating with external services like Plaid for financial data.

## Architecture
* Backend: 
    * Kotlin and Ktor framework
    * Retrofit and Gson for communication with Plaid API
* Database: 
    * PostgreSQL with Exposed ORM for interactions with user data, access tokens, account details, and transactions.
* Frontend:
    * React.js
    * Axios for API calls
    * Plaid Link SDK for secure bank account linking.

## Setup
Start by cloning the repository into a project directory.
* Backend: Set up a Gradle project on Intellij
    * Build: ./gradlew build
    * Run: ./gradlew run
    * Visit server: localhost 8000
* Database:
    * In terminal install/setup PostgreSQL
    * Start connection to PostgreSQL: pg_ctl -D "C:\Program Files\PostgreSQL\17\data" start
    * Connect: psql -U postgres -d finance_manager
* Frontend:
    * cd into frontend directory
    * npm install 
    * npm start
    * Visit app: localhost:3000

## Reflection
This project was conceived as a way to improve personal financial management by consolidating data from linked bank accounts into a single, user-friendly platform. I set out to build a web application that not only allowed users to view their bank balances and transaction data in real time, but also provided a foundation for more advanced features such as expense categorization using machine learning and interactive data visualizations using libraries like Chart.js or D3.js. Throughout the development process, I encountered challenges including secure token exchange with Plaid, ensuring seamless data flow between the frontend and backend, and managing database transactions effectively. These challenges provided valuable lessons in end-to-end application development, reinforcing the importance of modular design, robust error handling, and continuous testing. Overall, this project has been an excellent learning experience and a stepping stone toward more sophisticated financial technology solutions.
