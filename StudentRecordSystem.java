import java.io.*;
import java.sql.*;
public class StudentRecordSystem
{
    int reg_no;
    String f_name;
    String l_name;
    int year;
    char section;
    int roll_no;
    String email;   //Variables for accepting data from user
    String dob;
    String phno;
    String course_id;
    String course_name;
    double cgpa;

    String query;

    static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/mydb";   //For connection to database
    static final String USERNAME = "root";
    static final String PASSWORD = "mysql";

    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static PreparedStatement ps = null;

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    //Function for clearing the screen
    public static void clrscr()
    {
        //Clears Screen in java
        try
        {
            if (System.getProperty("os.name").contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (IOException | InterruptedException ex)
        {
        }
    
    }

    //Creating tables if not present. 
    public void checkTables()throws Exception
    {
        st = con.createStatement();
        query = "SELECT * FROM information_schema.tables WHERE table_name='Admins'";
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            //creating Admins table
            query = "CREATE TABLE IF NOT EXISTS Admins(Admin_Name VARCHAR(50), Password VARCHAR(255), PRIMARY KEY(Admin_Name))";
            st.executeUpdate(query);
            System.out.println("Admins Table Created\n");
            System.out.println("Enter Admin Name:");
            String u = br.readLine();
            System.out.println("Enter Password:");
            Console console = System.console();
            char arr[] = console.readPassword();
            String p = String.valueOf(arr);
            query = "INSERT INTO Admins VALUES(?, SHA2(?, 256))";
            ps = con.prepareStatement(query);
            ps.setString(1, u);
            ps.setString(2, p);
            ps.executeUpdate();
            clrscr();
            System.out.println("Admin account created\n");

            //creating Students table
            query = "CREATE TABLE IF NOT EXISTS Students(Reg_No INT PRIMARY KEY, F_Name VARCHAR(50) NOT NULL, L_Name VARCHAR(50), S_Year INT NOT NULL, Section VARCHAR(1) NOT NULL, Roll_No INT NOT NULL, Email VARCHAR(50) CHECK(Email LIKE '%_@__%.__%') UNIQUE, Dob DATE NOT NULL, Phno CHAR(10) NOT NULL UNIQUE,CONSTRAINT Sec_roll UNIQUE(S_Year, Section, Roll_No))";
            st.executeUpdate(query);
            System.out.println("Students Table Created\n");

            //creating Courses table
            query = "CREATE TABLE IF NOT EXISTS Courses(Course_ID INT PRIMARY KEY, Course_Name VARCHAR(50))";
            st.executeUpdate(query);
            System.out.println("Courses Table Created\n");

            //creating Enrolls table
            query = "CREATE TABLE IF NOT EXISTS Enrolls(Reg_No INT PRIMARY KEY,Course_ID INT,FOREIGN KEY (Reg_No) REFERENCES Students(Reg_No) ON DELETE CASCADE,FOREIGN KEY (Course_ID) REFERENCES Courses(Course_ID) ON DELETE CASCADE)";
            st.executeUpdate(query);
            System.out.println("Enrolls Table Created\n");

            //creating Grades table
            query = "CREATE TABLE IF NOT EXISTS GRADES(Reg_No INT PRIMARY KEY, CGPA DECIMAL(2, 1), FOREIGN KEY (Reg_No) REFERENCES Students(Reg_No) ON DELETE CASCADE)";
            st.executeUpdate(query);
            System.out.println("Grades Table Created\n");
        }
    }

    //Adding new admin
    public void adminMenu1()throws Exception
    {
        clrscr();
        System.out.println("Enter username of new admin: ");
        String u = br.readLine().toUpperCase();
        System.out.println("Enter password of new admin: ");
        Console console = System.console();
        char arr[] = console.readPassword();
        String p = String.valueOf(arr);
        query = "INSERT INTO Admins VALUES(?, SHA2(?, 256))";
        ps = con.prepareStatement(query);
        ps.setString(1, u);
        ps.setString(2, p);
        ps.executeUpdate();
        clrscr();
        System.out.println("\n"+u+" has been added to admin");
    }

    //Deleting admin
    public void adminMenu2()throws Exception
    {
        clrscr();
        System.out.println("Enter username of admin to delete: ");
        String u = br.readLine().toUpperCase();
        query = "DELETE FROM Admins WHERE Admin_Name='"+u+"'";
        int deleted = st.executeUpdate(query);
        if(deleted!=0)
        {
            System.out.println("\n"+u+" has been deleted from admin");
        }
        else
        {
            System.out.println("\n"+u+" not found in admin");
        }
    }

    //Generating Registration Number
    public int generateRegNo()throws Exception
    {
        clrscr();
        query = "SELECT MAX(REG_NO) FROM STUDENTS";
        int x = 10001;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            x = rs.getInt(1);
            if(x >= 10001)
            {
                x++;        //generates reg_no from 10001 and onwards.
            }
        }
        return x;
    }
    
    //Adding all details of Student
    public void adminMenu3()throws Exception
    {
        clrscr();
        reg_no = generateRegNo();
        System.out.println("Enter first name: ");
        f_name = br.readLine().toUpperCase();
        System.out.println("Enter last name: ");
        l_name = br.readLine().toUpperCase();
        System.out.println("Enter year: ");
        year = Integer.parseInt(br.readLine());
        System.out.println("Enter section: ");
        section = Character.toUpperCase((char)br.read());
        br.readLine();
        System.out.println("Enter roll no.: ");
        roll_no = Integer.parseInt(br.readLine());
        System.out.println("Enter email: ");
        email = br.readLine();
        System.out.println("Enter date of birth: ");
        dob = br.readLine();
        System.out.println("Enter phone no.: ");
        phno = br.readLine();
        System.out.println("Enter course ID: ");
        course_id = br.readLine().toUpperCase();
        System.out.println("Enter grade: ");
        cgpa = Double.parseDouble(br.readLine());
        query = "INSERT INTO STUDENTS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setString(2, f_name);
        ps.setString(3, l_name);
        ps.setInt(4, year);
        ps.setString(5, String.valueOf(section));
        ps.setInt(6, roll_no);
        ps.setString(7, email);
        ps.setString(8, dob);
        ps.setString(9, phno);
        ps.executeUpdate();
        query = "INSERT INTO ENROLLS VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setString(2, course_id);
        ps.executeUpdate();
        query = "INSERT INTO GRADES VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setDouble(2, cgpa);
        ps.executeUpdate();
        System.out.println("\nInsertion of Student details successful");
    }

    //Deleting student details
    public void adminMenu4()throws Exception
    {
        clrscr();
        System.out.println("Enter registration number of student to delete: ");
        int r = Integer.parseInt(br.readLine());
        query = "DELETE FROM STUDENTS WHERE REG_NO="+r;
        int deleted = st.executeUpdate(query);
        if(deleted!=0)
        {
            query = "DELETE FROM ENROLLS WHERE REG_NO="+r;
            st.executeUpdate(query);
            query = "DELETE FROM GRADES WHERE REG_NO="+r;
            st.executeUpdate(query);
            System.out.println("\nDeletion of student details successful");
        }
        else
        {
            System.out.println("Student with Registration Number = "+r+" not found!");
        }
    }

    //Adding new Course details
    public void adminMenu5()throws Exception
    {
        clrscr();
        System.out.println("Enter Course ID");
        String c = br.readLine().toUpperCase();
        System.out.println("Enter Course Name: ");
        String cn = br.readLine().toUpperCase();
        query = "INSERT INTO COURSES VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setString(1, c);
        ps.setString(2, cn);
        ps.executeUpdate();
        System.out.println("\nInsertion of new course details successful");
    }

    //Deleting a Course
    public void adminMenu6()throws Exception
    {
        clrscr();
        System.out.println("Enter course name: ");
        String cn = br.readLine().toUpperCase();
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE COURSE_NAME='"+cn+"'";
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            query = "DELETE FROM COURSES WHERE COURSE_NAME='"+cn+"'";
            int deleted = st.executeUpdate(query);
            if(deleted!=0)
            {
                System.out.println("\nDeletion of course details successful");
            }
            else
            {
                System.out.println("\nDeletion of course details unsuccessful");
            }
        }
        else
        {
            System.out.println("\nCourse is in progress. Cannot be removed!");
        }
    }

    //Updating phone number of a student
    public void adminMenu7()throws Exception
    {
        clrscr();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new phone number: ");
        String ph = br.readLine();
        query = "UPDATE STUDENTS SET PHNO='"+ph+"' WHERE REG_NO="+r;
        int updated = st.executeUpdate(query);
		if(updated!=0)
		{
			System.out.println("\nPhone number of student registration number = "+r+" has been updated");
		}
		else
		{
			System.out.println("\nUpdation Unsuccessful. Registration Number does not exist!");
		}
    }

    //Updating section and roll number
    public void adminMenu8()throws Exception
    {
        clrscr();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new section: ");
        char s = (char)br.read();
        br.readLine();
        System.out.println("Enter new roll number: ");
        int ro = Integer.parseInt(br.readLine());
        query = "UPDATE STUDENTS SET SECTION='"+s+"', ROLL_NO="+ro+" WHERE REG_NO="+r;
        int updated = st.executeUpdate(query);
        if(updated!=0)
        {
            System.out.println("\nSection and roll number of student registration number = "+r+" has been updated");
        }
        else
        {
            System.out.println("\nUpdation Unsuccessful. Registration Number does not exist!");
        }
    }

    //Updating the course enrolled by a student
    public void adminMenu9()throws Exception
    {
        clrscr();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new course id: ");
        String id = br.readLine();
        query = "SELECT * FROM COURSES WHERE COURSE_ID="+id;
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            System.out.println("\nCourse does not exist!");
        }
        else
        {
            query = "UPDATE ENROLLS SET COURSE_ID='"+id+"' WHERE REG_NO="+r;
            int updated = st.executeUpdate(query);
            if(updated!=0)
            {
                System.out.println("\nUpdation of course for student registration number = "+r+" has been successful");
            }
            else
            {
                System.out.println("\nUpdation Unsuccessful. Registration Number/Course ID does not exist!");
            }
        }
    }

    //Updating the CGPA of a student
    public void adminMenu10()throws Exception
    {
        clrscr();
        st = con.createStatement();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new CGPA: ");
        double c = Double.parseDouble(br.readLine());
        query = "UPDATE GRADES SET CGPA="+c+" WHERE REG_NO="+r;
        int updated = st.executeUpdate(query);
        if(updated!=0)
        {
            System.out.println("\nUpdation of CGPA of student registration number = "+r+" has been successful");
        }
        else
        {
            System.out.println("\nUpdation Unsuccessful. Registration Number does not exist!");
        }
    }

    //Function for all the admin operations
    public boolean admin()throws Exception
    {
        clrscr();
        System.out.println("Enter admin username: ");
        String u = br.readLine();
        u = u.toUpperCase();
        System.out.println("Enter password: ");
        Console console = System.console();
        char arr[] = console.readPassword();
        String p = String.valueOf(arr);
        query = "SELECT * FROM ADMINS WHERE ADMIN_NAME='"+u+"' AND PASSWORD=SHA2('"+p+"', 256)";
        rs = st.executeQuery(query);
        if(rs.next())
        {
            clrscr();
            System.out.println("\nWelcome "+u+"!");
            int choice = 0;
            while(choice!=11)
            {
                System.out.println("\n1. Add new admin");
                System.out.println("2. Delete admin");
                System.out.println("3. Add Student details");
                System.out.println("4. Delete Student details");
                System.out.println("5. Add Course details");
                System.out.println("6. Delete Course details");
                System.out.println("7. Update Phone Number");
                System.out.println("8. Update Section and Roll No.");
                System.out.println("9. Update enrolled course");
                System.out.println("10. Update CGPA");
                System.out.println("11. Exit");
                System.out.println("\nEnter your choice: ");
                choice = Integer.parseInt(br.readLine());
                switch(choice)
                {
                    case 1:
                    adminMenu1();
                    break;
                    case 2:
                    adminMenu2();
                    break;
                    case 3:
                    adminMenu3();
                    break;
                    case 4:
                    adminMenu4();
                    break;
                    case 5:
                    adminMenu5();
                    break;
                    case 6:
                    adminMenu6();
                    break;
                    case 7:
                    adminMenu7();
                    break;
                    case 8:
                    adminMenu8();
                    break;
                    case 9:
                    adminMenu9();
                    break;
                    case 10:
                    adminMenu10();
                    break;
                    case 11:
                    clrscr();
                    break;
                    default:
                    System.out.println("Invalid Choice");
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    //For displaying the date in dd/mmm/yyyy
    public String displayDate(String a)
    {
        String b = a.substring(0, 10);
        return b;
    }

    //Display all students
    public void userMenu1()throws Exception
    {
        clrscr();
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.S_YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10), rsm.getColumnName(11));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("S_YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
    }

    //Search student by name
    public void userMenu2()throws Exception
    {
        clrscr();
        System.out.println("Enter first name: ");
        String f_name = br.readLine().toUpperCase();
        System.out.println("Enter last name: ");
        String l_name = br.readLine().toUpperCase();
        query = "SELECT S.REG_NO, S.S_YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE F_NAME='"+f_name+"' AND L_NAME='"+l_name+"'";
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9));
            System.out.println();
            System.out.printf("%-10d%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getInt("S_YEAR"), rs.getString("SECTION").charAt(0), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
        else
        {
            System.out.println("\nGiven name does not exist!");
        }
    }

    //Search student by year, section, roll no
    public void userMenu3()throws Exception
    {
        clrscr();
        System.out.println("Enter year: ");
        year = Integer.parseInt(br.readLine());
        System.out.println("Enter section: ");
        section = (char)br.read();
        br.readLine();
        System.out.println("Enter roll number: ");
        roll_no = Integer.parseInt(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE S_YEAR="+year+" AND SECTION='"+section+"' AND ROLL_NO="+roll_no;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8));
            System.out.println();
            System.out.printf("%-10d%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
        else
        {
            System.out.println("\nStudent does not exist!");
        }
    }

    //Search students by registration number
    public void userMenu4()throws Exception
    {
        clrscr();
        System.out.println("Enter registration no.: ");
        reg_no = Integer.parseInt(br.readLine());
        query = "SELECT S.F_NAME, S.L_NAME, S.S_YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE S.REG_NO="+reg_no;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
            System.out.println();
            System.out.printf("%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("S_YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
        else
        {
            System.out.println("\nGiven registration number does not exist!");
        }
    }

    //Search students studying in a specific year
    public void userMenu5()throws Exception
    {
        clrscr();
        System.out.println("Enter year: ");
        year = Integer.parseInt(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE S_YEAR="+year;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        int count = 0;
        while(true)
        {
            if(rs.next())
            {
                System.out.printf("%-10d%-10s%-10s%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
                System.out.println();
                count++;
            }
            else
            {
                break;
            }
        }
        if(count==0)
        {
            System.out.println("\nGiven year does not exist!");
        }
    }

    //Search students studying a specific course
    public void userMenu6()throws Exception
    {
        clrscr();
        System.out.println("Enter course name: ");
        course_name = br.readLine().toUpperCase();
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.S_YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, G.CGPA FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE COURSE_NAME='"+course_name+"'";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        int count = 0;
        while(true)
        {
            if(rs.next())
            {
                System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("S_YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("CGPA"));
                System.out.println();
                count++;
            }
            else
            {
                break;
            }
        }
        if(count==0)
        {
            System.out.println("\nGiven course does not exist!");
        }
    }

    //Higher than a specific CGPA
    public void userMenu7()throws Exception
    {
        clrscr();
        System.out.println("Enter a CGPA:");
        cgpa = Double.parseDouble(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.S_YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME FROM STUDENTS S INNER JOIN ENROLLS E ON S.REG_NO=E.REG_NO INNER JOIN COURSES C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADES G ON S.REG_NO=G.REG_NO WHERE G.CGPA > "+cgpa;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("S_YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"));
            System.out.println();
        }
    }

    //Display highest CGPA in respective course and year
    public void userMenu8()throws Exception
    {
        clrscr();
        query = "SELECT S.S_YEAR, C.COURSE_NAME, MAX(G.CGPA) AS CGPA FROM STUDENTS S, ENROLLS E, COURSES C, GRADES G WHERE S.REG_NO=E.REG_NO AND C.COURSE_ID=E.COURSE_ID AND S.REG_NO=G.REG_NO GROUP BY C.COURSE_NAME, S.S_YEAR ORDER BY S_YEAR, COURSE_NAME";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-20s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-20s%-10s", rs.getInt("S_YEAR"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
    }

    //Function for all user operations
    public void user()throws Exception
    {
        clrscr();
        int choice = 0;
        while(choice!=9)
        {
            System.out.println("\n1. Display all students");
            System.out.println("2. Search student by name");
            System.out.println("3. Search student by year, section and roll no.");
            System.out.println("4. Search student by registration number");
            System.out.println("5. Search students studying in a specific year");
            System.out.println("6. Search students studying a specific course");
            System.out.println("7. Search students having CGPA higher than given");
            System.out.println("8. Display highest CGPA in respective course and year");
            System.out.println("9. Exit");
            System.out.println("\nEnter your choice: ");
            choice = Integer.parseInt(br.readLine());
            switch(choice)
            {
                case 1:
                userMenu1();
                break;
                case 2:
                userMenu2();
                break;
                case 3:
                userMenu3();
                break;
                case 4:
                userMenu4();
                break;
                case 5:
                userMenu5();
                break;
                case 6:
                userMenu6();
                break;
                case 7:
                userMenu7();
                break;
                case 8:
                userMenu8();
                break;
                case 9:
                clrscr();
                break;
                default:
                System.out.println("Invalid Choice");
            }
        }
    }

    public static void main(String args[])throws Exception
    {
        int choice = 0;
        int fail_count = 0;
        while(choice!=3)
        {
            try
            {
				Class.forName(DRIVER);
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                st = con.createStatement();
                StudentRecordSystem s = new StudentRecordSystem();
                s.checkTables();
                System.out.println("\n1. Admin Mode");
                System.out.println("2. User Mode");
                System.out.println("3. Exit");
                System.out.println("\nEnter your choice: ");
                choice = Integer.parseInt(br.readLine());
                switch(choice)
                {
                    case 1:
                    boolean r = s.admin();
                    if(r==false)
                    {
                        fail_count++;
                        if(fail_count<3)
                        {
                            System.out.println("Wrong username or password. You have "+(3-fail_count)+" chances left");
                        }
                        else
                        {
                            System.out.println("You have entered wrong credentials 3 times. Terminating the program.");
                            System.exit(0);
                        }
                    }
                    else
                    {
                        clrscr();
                    }
                    break;
                    case 2:
                    s.user();
                    break;
                    case 3:
                    System.out.println("\nProgram Ends");
                    if(ps!=null)
                    {
                        ps.close();
                    }
                    if(st!=null)
                    {
                        st.close();
                    }
                    con.close();
                    System.exit(0);
                    break;
                    default:
                    System.out.println("\nInvalid Choice");
                }
            }
            catch(SQLException e)
            {
                System.out.println("\nError in SQL\n");
                continue;
            }
            catch(ClassNotFoundException e)
            {
                System.out.println("\nDriver Error\n");
            }
        }
    }
}