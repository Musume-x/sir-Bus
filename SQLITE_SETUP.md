# SQLite Database Setup Instructions

## Adding SQLite JDBC Driver

To use SQLite database in this project, you need to add the SQLite JDBC driver to your project's classpath.

### Steps:

1. **Download SQLite JDBC Driver**
   - Download from: https://github.com/xerial/sqlite-jdbc/releases
   - Get the latest `sqlite-jdbc-X.X.X.jar` file

2. **Add to NetBeans Project**
   - Right-click on your project in NetBeans
   - Select "Properties"
   - Go to "Libraries" section
   - Click "Add JAR/Folder"
   - Navigate to the downloaded `sqlite-jdbc-X.X.X.jar` file
   - Click "Open"
   - Click "OK"

3. **Alternative: Create lib folder**
   - Create a `lib` folder in your project root
   - Place the `sqlite-jdbc-X.X.X.jar` file in the `lib` folder
   - Add it to the project libraries as described above

### Database File Location

The database file `student_infoSys.db` will be created automatically in the project root directory when you first run the application.

## Features Implemented

- ✅ SQLite database connection using config class
- ✅ User registration (username, email, password, verify password)
- ✅ User login (email, password)
- ✅ Password hashing using SHA-256
- ✅ Navigation to landing page after successful login
- ✅ Sign up link in login form
- ✅ Login link in register form
- ✅ Remember me checkbox (UI only, functionality can be added later)
- ✅ Form validation
- ✅ Duplicate email/username checking
- ✅ Database table auto-creation on first run