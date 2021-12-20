# Student_Record_System
A Student Record Management System done in Java using JDBC and Oracle.

To run the Java program locally, follow the steps:
1. Download & install Oracle 10g Express Edition. Remember the password set during installation, to change the password in code accordingly.
2. Go to C:\oraclexe\app\oracle\product\10.2.0\server\jdbc\lib folder
3. Copy the path of ojdbc14.jar and also copy the file.
4. Go to environment variables and add new path in system variables. Set variable name as "classpath" (without the quotes) and paste the above copied file path. Append ;.; at the end of the file location (without any spaces).
5. Then go to C:\Program Files (x86)\Java\jre1.6.0\lib\ext and paste the ojdbc14.jar file.
6. Restart the computer.
7. Type Run SQL Command Line in Windows Search bar.
8. Type ```connect system;``` and press Enter.
9. Enter the password set during time of Oracle installation.
10. Type ```set autocommit on``` and press Enter, so as to save changes when a new data is entered into database.
11. Compile and run the java file.
12. Before entering names of student, add course details.