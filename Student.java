import java.sql.DriverManager;
import java.sql.Connection;
class Student
{
	public static void main(String args[])
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "root", "helloworld");
			System.out.println("Database connected");
		}
		catch(Exception e)
		{
			System.out.println("database not connected: "+e);
		}
	}
}
