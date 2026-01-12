# PostgreSQL Setup Guide for BE26

## Prerequisites
- PostgreSQL installed on your system
- PostgreSQL running and accessible

## Setup Steps

### 1. Create Database
Open PostgreSQL command line and run:

```sql
CREATE DATABASE be26db;
```

### 2. Verify Connection
Test connection with these credentials:
- **Host**: localhost
- **Port**: 5432
- **Database**: be26db
- **Username**: postgres
- **Password**: postgres

### 3. Start the Application
Run the rebuilt JAR:

```bash
java -jar target\be26-0.0.1-SNAPSHOT.jar
```

The application will:
- Connect to PostgreSQL
- Automatically create tables (ddl-auto: update)
- Be ready for API requests

## Configuration Details

### application.yml Settings
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/be26db
  username: postgres
  password: postgres
```

### Available Endpoints
- **Register**: `POST /api/auth/register`
- **Login**: `POST /api/auth/login`
- **Products**: `GET/POST /api/products`
- **Orders**: `GET/POST /api/orders`
- **Payments**: `GET/POST /api/payments`

## Accessing Database Data

### Option 1: psql Command Line
```bash
psql -U postgres -d be26db
```

Then query:
```sql
SELECT * FROM "user";
SELECT * FROM product;
SELECT * FROM "order";
SELECT * FROM payment;
```

### Option 2: Database Client Tools
- DBeaver
- pgAdmin
- DataGrip

Connect with the credentials above.

## Tables Created Automatically
- `user` - User accounts
- `product` - Product catalog
- `order` - Orders (note: renamed to avoid SQL keyword)
- `payment` - Payment records

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL is running: `pg_ctl status`
- Check if listening on localhost:5432: `netstat -an | findstr 5432`

### Database Not Found
- Create the database: `CREATE DATABASE be26db;`

### Permission Denied
- Verify postgres user password
- Check pg_hba.conf for authentication method

