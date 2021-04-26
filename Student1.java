import java.io.*;
import java.sql.*;
public class Student1
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

    static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";   //For connection to database
    static final String USERNAME = "system";
    static final String PASSWORD = "oracle";

    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    static PreparedStatement ps = null;

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    //Checking whether the tables are present or not. 
    public void checkTables(String dbName)throws Exception
    {
        DatabaseMetaData dbm;
        st = con.createStatement();
        dbm = con.getMetaData();
        //checking for ADMIN table
        rs = dbm.getTables(dbName, "%", "ADMIN", null);
        if(!rs.next())
        {
            String query = "CREATE TABLE ADMIN(ADMIN_NAME VARCHAR2(20) PRIMARY KEY, PASSWORD VARCHAR2(10))";
            st.executeQuery(query);
            query = "INSERT INTO ADMIN VALUES('ABCDEF', 12345)";
            st.executeQuery(query);
            System.out.println("ADMIN Table Created");
        }
        //checking for STUDENT table
        rs = dbm.getTables(dbName, "%", "STUDENT", null);
        if(!rs.next())
        {
            String query = "CREATE TABLE STUDENT(REG_NO NUMBER(5) PRIMARY KEY, F_NAME VARCHAR2(20) NOT NULL, L_NAME VARCHAR2(20), YEAR NUMBER(2) NOT NULL, SECTION VARCHAR2(1) NOT NULL, ROLL_NO NUMBER(2) NOT NULL, EMAIL VARCHAR2(50) CHECK(EMAIL LIKE '%_@__%.__%') UNIQUE, DOB DATE NOT NULL, PHNO CHAR(10) NOT NULL UNIQUE, CONSTRAINT SEC_ROLL UNIQUE(YEAR, SECTION, ROLL_NO))";
            st.executeUpdate(query);
            System.out.println("STUDENT Table Created");
        }
        //checking for COURSE table
        rs = dbm.getTables(dbName, "%", "COURSE", null);
        if(!rs.next())
        {
            String query = "CREATE TABLE COURSE(COURSE_ID VARCHAR2(5) PRIMARY KEY, COURSE_NAME VARCHAR2(20))";
            st.executeUpdate(query);
            System.out.println("COURSE Table Created");
        }
        //checking for ENROLL table
        rs = dbm.getTables(dbName, "%", "ENROLL", null);
        if(!rs.next())
        {
            String query = "CREATE TABLE ENROLL(REG_NO NUMBER(5), COURSE_ID VARCHAR2(5), PRIMARY KEY(REG_NO, COURSE_ID))";
            st.executeUpdate(query);
            System.out.println("ENROLL Table Created");
        }
        //checking for GRADE table
        rs = dbm.getTables(dbName, "%", "GRADE", null);
        if(!rs.next())
        {
            String query = "CREATE TABLE GRADE(REG_NO NUMBER(5) PRIMARY KEY, CGPA NUMBER(2,1))";
            st.executeUpdate(query);
            System.out.println("GRADE Table Created");
        }
    }

    //Adding new admin
    public void adminMenu1()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter username of new admin: ");
        String u = br.readLine().toUpperCase();
        System.out.println("Enter password of new admin: ");
        int p = Integer.parseInt(br.readLine());
        query = "INSERT INTO ADMIN VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setString(1, u);
        ps.setInt(2, p);
        ps.executeUpdate();
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("\n"+u+" has been added to admin table");
    }

    //Deleting admin
    public void adminMenu2()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter username of admin to delete: ");
        String u = br.readLine().toUpperCase();
        query = "DELETE FROM ADMIN WHERE ADMIN_NAME='"+u+"'";
        st.executeUpdate(query);
        System.out.println("\n"+u+" has been deleted from admin table");
    }

    //Generating Registration Number
    public int generateRegNo()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        query = "SELECT MAX(REG_NO) FROM STUDENT";
        int x = 0;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            x = rs.getInt(1);
            x = x+1;
        }
        else
        {
            x = 10001;
        }
        return x;
    }
    
    //Adding all details of Student
    public void adminMenu3()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
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
        query = "INSERT INTO STUDENT VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        query = "INSERT INTO ENROLL VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setString(2, course_id);
        ps.executeUpdate();
        query = "INSERT INTO GRADE VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, reg_no);
        ps.setDouble(2, cgpa);
        ps.executeUpdate();
        System.out.println("\nInsertion of Student details successful");
    }

    //Deleting student details
    public void adminMenu4()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter registration number of student to delete: ");
        int r = Integer.parseInt(br.readLine());
        query = "DELETE FROM STUDENT WHERE REG_NO="+r;
        st.executeUpdate(query);
        query = "DELETE FROM ENROLL WHERE REG_NO="+r;
        st.executeUpdate(query);
        query = "DELETE FROM GRADE WHERE REG_NO="+r;
        st.executeUpdate(query);
        System.out.println("\nDeletion of student details successful");
    }

    //Adding new Course details
    public void adminMenu5()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter Course ID");
        String c = br.readLine().toUpperCase();
        System.out.println("Enter Course Name: ");
        String cn = br.readLine().toUpperCase();
        query = "INSERT INTO COURSE VALUES(?, ?)";
        ps = con.prepareStatement(query);
        ps.setString(1, c);
        ps.setString(2, cn);
        ps.executeUpdate();
        System.out.println("\nInsertion of new course details successful");
    }

    //Deleting a Course
    public void adminMenu6()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter course name: ");
        String cn = br.readLine().toUpperCase();
        query = "DELETE FROM COURSE WHERE COURSE_NAME='"+cn+"'";
        st.executeUpdate(query);
        System.out.println("\nDeletion of course details successful");
        st.close();
        con.close();
    }

    //Updating phone number of a student
    public void adminMenu7()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new phone number: ");
        String ph = br.readLine();
        query = "UPDATE STUDENT SET PHNO='"+ph+"' WHERE REG_NO="+r;
        st.executeUpdate(query);
        System.out.println("\nPhone number of student registration number = "+r+" has been updated");
    }

    //Updating section and roll number
    public void adminMenu8()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new section: ");
        char s = (char)br.read();
        br.readLine();
        System.out.println("Enter new roll number: ");
        int ro = Integer.parseInt(br.readLine());
        query = "UPDATE STUDENT SET SECTION='"+s+"', ROLL_NO="+ro+" WHERE REG_NO="+r;
        st.executeUpdate(query);
        System.out.println("\nSection and roll number of student registration number = "+r+" has been updated");
    }

    //Updating the course enrolled by a student
    public void adminMenu9()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new course id: ");
        String id = br.readLine();
        query = "UPDATE ENROLL SET COURSE_ID='"+id+"' WHERE REG_NO="+r;
        st.executeUpdate(query);
        System.out.println("\nUpdation of course for student registration number = "+r+" has been successful");
    }

    //Updating the CGPA of a student
    public void adminMenu10()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        st = con.createStatement();
        System.out.println("Enter registration number: ");
        int r = Integer.parseInt(br.readLine());
        System.out.println("Enter new CGPA: ");
        double c = Double.parseDouble(br.readLine());
        query = "UPDATE GRADE SET CGPA="+c+" WHERE REG_NO="+r;
        st.executeUpdate(query);
        System.out.println("\nUpdation of CGPA of student registration number = "+r+" has been successful");
    }

    //Function for all the admin operations
    public boolean admin()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter admin username: ");
        String u = br.readLine();
        System.out.println("Enter password: ");
        int p = Integer.parseInt(br.readLine());
        query = "SELECT * FROM ADMIN WHERE ADMIN_NAME='"+u+"' AND PASSWORD="+p;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            System.out.print("\033[H\033[2J");  
            System.out.flush();
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
        String b = a.substring(0, 11);
        return b;
    }

    //Display all students
    public void userMenu1()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10), rsm.getColumnName(11));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
    }

    //Search student by name
    public void userMenu2()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter first name: ");
        String f_name = br.readLine().toUpperCase();
        System.out.println("Enter last name: ");
        String l_name = br.readLine().toUpperCase();
        query = "SELECT S.REG_NO, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE F_NAME='"+f_name+"' AND L_NAME='"+l_name+"'";
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9));
            System.out.println();
            System.out.printf("%-10d%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getInt("YEAR"), rs.getString("SECTION").charAt(0), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
        else
        {
            System.out.println("\n"+f_name+" "+l_name+" not present in the table");
        }
    }

    //Search student by year, section, roll no
    public void userMenu3()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter year: ");
        year = Integer.parseInt(br.readLine());
        System.out.println("Enter section: ");
        section = (char)br.read();
        br.readLine();
        System.out.println("Enter roll number: ");
        roll_no = Integer.parseInt(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE YEAR="+year+" AND SECTION='"+section+"' AND ROLL_NO="+roll_no;
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
            System.out.println("\nStudent does not exist");
        }
    }

    //Search studemts by registration number
    public void userMenu4()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter registration no.: ");
        reg_no = Integer.parseInt(br.readLine());
        query = "SELECT S.F_NAME, S.L_NAME, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE REG_NO="+reg_no;
        rs = st.executeQuery(query);
        if(rs.next())
        {
            ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
            System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
            System.out.println();
            System.out.printf("%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
        else
        {
            System.out.println("Given year does not exist");
        }
    }

    //Search students studying in a specific year
    public void userMenu5()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter year: ");
        year = Integer.parseInt(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE YEAR="+year;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();   //For printing the column names
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10s%-10d%-30s%-20s%-15s%-15s%-10s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
    }

    //Search students studying a specific course
    public void userMenu6()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter course name: ");
        course_name = br.readLine().toUpperCase();
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, G.CGPA FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE COURSE_NAME='"+course_name+"'";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("CGPA"));
            System.out.println();
        }
    }

    //Higher than a specific CGPA
    public void userMenu7()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println("Enter a CGPA:");
        cgpa = Double.parseDouble(br.readLine());
        query = "SELECT S.REG_NO, S.F_NAME, S.L_NAME, S.YEAR, S.SECTION, S.ROLL_NO, S.EMAIL, S.DOB, S.PHNO, C.COURSE_NAME FROM STUDENT S INNER JOIN ENROLL E ON S.REG_NO=E.REG_NO INNER JOIN COURSE C ON E.COURSE_ID=C.COURSE_ID INNER JOIN GRADE G ON S.REG_NO=G.REG_NO WHERE G.CGPA>"+cgpa;
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-30s%-20s%-15s%-15s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3), rsm.getColumnName(4), rsm.getColumnName(5), rsm.getColumnName(6), rsm.getColumnName(7), rsm.getColumnName(8), rsm.getColumnName(9), rsm.getColumnName(10));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-10s%-10s%-10d%-10s%-10d%-30s%-20s%-15s%-15s", rs.getInt("REG_NO"), rs.getString("F_NAME"), rs.getString("L_NAME"), rs.getInt("YEAR"), rs.getString("SECTION"), rs.getInt("ROLL_NO"), rs.getString("EMAIL"), displayDate(rs.getString("DOB")), rs.getString("PHNO"), rs.getString("COURSE_NAME"));
            System.out.println();
        }
    }

    //Display students with highest CGPA
    public void userMenu8()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        query = "SELECT S.YEAR, C.COURSE_NAME, MAX(G.CGPA) AS CGPA FROM STUDENT S, ENROLL E, COURSE C, GRADE G WHERE S.REG_NO=E.REG_NO AND C.COURSE_ID=E.COURSE_ID AND S.REG_NO=G.REG_NO GROUP BY C.COURSE_NAME, S.YEAR ORDER BY YEAR, COURSE_NAME";
        rs = st.executeQuery(query);
        ResultSetMetaData rsm = rs.getMetaData();
        System.out.printf("\n%-10s%-20s%-10s", rsm.getColumnName(1), rsm.getColumnName(2), rsm.getColumnName(3));
        System.out.println();
        while(rs.next())
        {
            System.out.printf("%-10d%-20s%-10s", rs.getInt("YEAR"), rs.getString("COURSE_NAME"), rs.getBigDecimal("CGPA"));
            System.out.println();
        }
    }

    //Function for all user operations
    public void user()throws Exception
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
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
            System.out.println("8. Display students with highest CGPA in their respective course and year");
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
                break;
                default:
                System.out.println("Invalid Choice");
            }
        }
    }
    public static void main(String args[])throws Exception
    {
        int choice = 0;
        while(choice!=3)
        {
            try
            {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                st = con.createStatement();
                Student1 s = new Student1();
                s.checkTables("xe");
                int fail_count = 0;
                System.out.print("\033[H\033[2J");  
                System.out.flush();
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