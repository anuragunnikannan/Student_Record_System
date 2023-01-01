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

    /* static final String DRIVER = "com.mysql.jdbc.Driver"; */
    static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/mydb";   //For connection to database
    /* static final String USERNAME = "system"; */
    static final String USERNAME = "root";
    /* static final String PASSWORD = "mysql"; */
    static final String PASSWORD = "helloworld";

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
        query = "SELECT * FROM information_schema.tables WHERE table_name='admins'";
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            //creating Admins table
            query = "CREATE TABLE IF NOT EXISTS admins(admin_name VARCHAR(50), password VARCHAR(255), PRIMARY KEY(admin_name))";
            st.executeUpdate(query);
            System.out.println("Admins Table Created\n");
            System.out.println("Enter Admin Name:");
            String u = br.readLine();
            System.out.println("Enter Password:");
            Console console = System.console();
            char arr[] = console.readPassword();
            String p = String.valueOf(arr);
            query = "INSERT INTO admins VALUES(?, SHA2(?, 256))";
            ps = con.prepareStatement(query);
            ps.setString(1, u);
            ps.setString(2, p);
            ps.executeUpdate();
            clrscr();
            System.out.println("Admin account created\n");

            //creating Students table
            query = "CREATE TABLE IF NOT EXISTS students(reg_no INT PRIMARY KEY, f_name VARCHAR(50) NOT NULL, l_name VARCHAR(50), s_year INT NOT NULL, section VARCHAR(1) NOT NULL, roll_no INT NOT NULL, email VARCHAR(50) CHECK(email LIKE '%_@__%.__%') UNIQUE, dob DATE NOT NULL, phno CHAR(10) NOT NULL UNIQUE,CONSTRAINT sec_roll UNIQUE(s_year, section, roll_no))";
            st.executeUpdate(query);
            System.out.println("Students Table Created\n");

            //creating Courses table
            query = "CREATE TABLE IF NOT EXISTS courses(course_id INT PRIMARY KEY, course_name VARCHAR(50))";
            st.executeUpdate(query);
            System.out.println("Courses Table Created\n");

            //creating Enrolls table
            query = "CREATE TABLE IF NOT EXISTS enrolls(reg_no INT PRIMARY KEY,course_id INT,FOREIGN KEY (reg_no) REFERENCES students(reg_no) ON DELETE CASCADE,FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE)";
            st.executeUpdate(query);
            System.out.println("Enrolls Table Created\n");

            //creating Grades table
            query = "CREATE TABLE IF NOT EXISTS grades(reg_no INT PRIMARY KEY, cgpa DECIMAL(2, 1), FOREIGN KEY (reg_no) REFERENCES students(reg_no) ON DELETE CASCADE)";
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
        query = "INSERT INTO admins VALUES(?, SHA2(?, 256))";
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
        query = "DELETE FROM admins WHERE admin_name='"+u+"'";
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
        query = "SELECT MAX(reg_no) FROM students";
        int x = 10001;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            x = rs.getInt(1);
            if(x == 0)
            {
                x = 10001;
            }
            else if(x >= 10001)
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
        query = "INSERT INTO students VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        query = "INSERT INTO enrolls VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setString(2, course_id);
        ps.executeUpdate();
        query = "INSERT INTO grades VALUES(?, ?)";
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
        query = "DELETE FROM students WHERE reg_no="+r;
        int deleted = st.executeUpdate(query);
        if(deleted!=0)
        {
            query = "DELETE FROM enrolls WHERE reg_no="+r;
            st.executeUpdate(query);
            query = "DELETE FROM grades WHERE reg_no="+r;
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
        query = "INSERT INTO courses VALUES(?, ?)";
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
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.year, s.section, s.roll_no, s.email, s.dob, s.phno, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE course_name='"+cn+"'";
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            query = "DELETE FROM courses WHERE course_name='"+cn+"'";
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
        query = "UPDATE students SET phno='"+ph+"' WHERE reg_no="+r;
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
        query = "UPDATE students SET section='"+s+"', roll_no="+ro+" WHERE reg_no="+r;
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
        query = "SELECT * FROM courses WHERE course_id="+id;
        rs = st.executeQuery(query);
        if(!rs.next())
        {
            System.out.println("\nCourse does not exist!");
        }
        else
        {
            query = "UPDATE enrolls SET course_id='"+id+"' WHERE reg_no="+r;
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

    //Updating the cgpa of a student
    public void adminMenu10()throws Exception
    {
        clrscr();
        st = con.createStatement();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new cgpa: ");
        double c = Double.parseDouble(br.readLine());
        query = "UPDATE grades SET cgpa="+c+" WHERE reg_no="+r;
        int updated = st.executeUpdate(query);
        if(updated!=0)
        {
            System.out.println("\nUpdation of cgpa of student registration number = "+r+" has been successful");
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
        query = "SELECT * FROM admins WHERE admin_name='"+u+"' AND password=SHA2('"+p+"', 256)";
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
                System.out.println("10. Update cgpa");
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
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.s_year, s.section, s.roll_no, s.email, s.dob, s.phno, c.course_name, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10), rsm.getColumnName(11));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("reg_no"), rs.getString("f_name"), rs.getString("l_name"), rs.getInt("s_year"), rs.getString("section"), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
        query = "SELECT s.reg_no, s.s_year, s.section, s.roll_no, s.email, s.dob, s.phno, c.course_name, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE f_name='"+f_name+"' AND l_name='"+l_name+"'";
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9));
            System.out.println();
            System.out.printf("%-10d%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("reg_no"), rs.getInt("s_year"), rs.getString("section").charAt(0), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.email, s.dob, s.phno, c.course_name, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE s_year="+year+" AND section='"+section+"' AND roll_no="+roll_no;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8));
            System.out.println();
            System.out.printf("%-10d%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rs.getInt("reg_no"), rs.getString("f_name"), rs.getString("l_name"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
        query = "SELECT s.f_name, s.l_name, s.s_year, s.section, s.roll_no, s.email, s.dob, s.phno, c.course_name, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE s.reg_no="+reg_no;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
            System.out.println();
            System.out.printf("%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getString("f_name"), rs.getString("l_name"), rs.getInt("s_year"), rs.getString("section"), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.section, s.roll_no, s.email, s.dob, s.phno, c.course_name, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE s_year="+year;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        int count = 0;
        while(true)
        {
            if(rs.next())
            {
                System.out.printf("%-10d%-10s%-10s%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("reg_no"), rs.getString("f_name"), rs.getString("l_name"), rs.getString("section"), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.s_year, s.section, s.roll_no, s.email, s.dob, s.phno, g.cgpa FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE course_name='"+course_name+"'";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        int count = 0;
        while(true)
        {
            if(rs.next())
            {
                System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("reg_no"), rs.getString("f_name"), rs.getString("l_name"), rs.getInt("s_year"), rs.getString("section"), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("cgpa"));
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

    //Higher than a specific cgpa
    public void userMenu7()throws Exception
    {
        clrscr();
        System.out.println("Enter a cgpa:");
        cgpa = Double.parseDouble(br.readLine());
        query = "SELECT s.reg_no, s.f_name, s.l_name, s.s_year, s.section, s.roll_no, s.email, s.dob, s.phno, c.course_name FROM students s INNER JOIN enrolls e ON s.reg_no=e.reg_no INNER JOIN courses c ON e.course_id=c.course_id INNER JOIN grades g ON s.reg_no=g.reg_no WHERE g.cgpa > "+cgpa;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("reg_no"), rs.getString("f_name"), rs.getString("l_name"), rs.getInt("s_year"), rs.getString("section"), rs.getInt("roll_no"), rs.getString("email"), displayDate(rs.getString("dob")), rs.getString("phno"), rs.getString("course_name"));
            System.out.println();
        }
    }

    //Display highest cgpa in respective course and year
    public void userMenu8()throws Exception
    {
        clrscr();
        query = "SELECT s.s_year, c.course_name, MAX(g.cgpa) AS cgpa FROM students s, enrolls e, courses c, grades g WHERE s.reg_no=e.reg_no AND c.course_id=e.course_id AND s.reg_no=g.reg_no GROUP BY c.course_name, s.s_year ORDER BY s_year, course_name";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-20s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-20s%-10s", rs.getInt("s_year"), rs.getString("course_name"), rs.getBigDecimal("cgpa"));
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
            System.out.println("7. Search students having cgpa higher than given");
            System.out.println("8. Display highest cgpa in respective course and year");
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