# Fieldwise Tele-Veterinary

Spring Boot, MySQL, REST, and static HTML/CSS/vanilla JavaScript starter for a regional livestock healthcare network.

## Run locally

Requirements: JDK 21, Maven 3.8+, and MySQL 8.

Create or use a MySQL account with permission to create the `tele_veterinary` database. In PowerShell, set the account details in your local terminal, then start the application:

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:DB_USERNAME = 'your-mysql-user'
$env:DB_PASSWORD = 'your-mysql-password'
mvn spring-boot:run
```

Open `http://localhost:8081/`. The JDBC URL defaults to local MySQL and creates the database if the selected account has permission. Override it with `DB_URL` when needed. Hibernate creates/updates tables; demo records are inserted when the contact table is empty.

## Implemented workflow

- Contact CRUD for farmers, veterinary specialists, and pharma/lab vendors.
- Livestock CRUD with farmer ownership, unique tag numbers, and health status.
- Consultation request, specialist assignment, clinical completion, and rule-based diagnostic priority.
- Live dashboard counts, filters, responsive tables, and seeded demo data.
- JSON validation and centralized REST error responses.

The provided UI and APIs currently cover that core clinical slice. Invoices, payments, purchase orders, diagnostic requests, prescriptions, double-entry accounting, budgets, and financial reports from the larger project brief are not implemented yet.

## Main endpoints

- `GET|POST /api/farmers`, `GET|PUT|DELETE /api/farmers/{id}`
- `GET|POST /api/specialists`, `PUT|DELETE /api/specialists/{id}`
- `GET|POST /api/vendors`, `PUT|DELETE /api/vendors/{id}`
- `GET|POST /api/livestock`, `GET|PUT|DELETE /api/livestock/{id}`
- `GET|POST /api/televet/consultations`, `GET|PUT|DELETE /api/televet/consultations/{id}`
- `POST /api/televet/consultations/{id}/assign-specialist`
- `POST /api/televet/consultations/{id}/complete`
- `GET /api/dashboard`

Run tests with `mvn test`.