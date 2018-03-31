package org.kandathil.vivek.db;

/*
 * Connects to a database server using PostgreSQL
 * Retrieves data from the first 100 tuples of a table, prints, and outputs the data to a .csv file
 */

/*
 * Created on Fri Mar 30, 2018
 * @author: vivekkandathil
 */

//import necessary Java SQL libraries
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

//File writing
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class PostgresDatabaseQuery 
{
	//parameters for connecting to database
	private final String strURL = "jdbc:postgresql://10.20.30.17:5432/aemdb?user=postgres&password=password&ssl=false";
	private final String strUser = "postgres";
	private final String strPassword = "password";
	
	public static void main(String[] args) 
	{
		//variable to store name of database
		String strDB = "aem.hpedispatcher";
		//declare new instance of PostgresDatabaseQuery
		PostgresDatabaseQuery postgres = new PostgresDatabaseQuery();
		//Call getData method to connect to database, receive, and output data
		postgres.getData(strDB);
	}
	
	//Connects to database server, returns "Connection" object
	public Connection connect()
	{
		Connection conn = null;
		
		try 
		{
			//Connect to database
			conn = DriverManager.getConnection(strURL, strUser, strPassword);
			System.out.println("Connected to the AEMDB server.");
		}
		//catch any SQL database access error(s)
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	//Method to return number of rows in AEMDB database
	public void getData(String strDatabaseName)
	{
		//Select first 100 rows from database
        String SQL = "SELECT * FROM " + strDatabaseName + " LIMIT 100";
        
        //Use connect method and execute query
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
        			//Run SQL query (defined by String 'SQL')
                ResultSet rs = stmt.executeQuery(SQL)) 
        {
            //call method to display information
            displayData(rs);     
        } 
        //catch any SQL database access error(s)
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
	}
	
	//print data from table
	public void displayData(ResultSet rs) throws SQLException
	{
		//String builder to append rows into the output for a CSV file
		StringBuilder sb = new StringBuilder();
		
		ResultSetMetaData metadata = rs.getMetaData();
		
		//used in a loop to ensure that values from every column are returned
	    int columnCount = metadata.getColumnCount();  
	    
	    //Print each row from ResultSet
	    while (rs.next()) 
	    {
	    		//String to hold the value of each tuple while looping through the result set
	    		String row = "";	   
	    		
	    		try
	    		{
	    			//Initialize/Create new file
	    			String path="/Users/vivekkandathil/Desktop/output.csv";
	    			File file = new File(path);

	    			// Create file if it does not exist
	    			if (!file.exists()) 
	    			{
	    				file.createNewFile();
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println(e);
	    		}
	        
	    		for (int i = 1; i <= columnCount; i++) 
	    		{
	    			//append each cell to the row string with a comma delimiter
	    			row += rs.getString(i) + ",";   
       		}
	    		
	    		//Append data (with whitespaces removed) to string builder
	    		sb.append("\n" + row.replaceAll("\\s", ""));
	    		//Print comma delimited rows with trailing whitespaces removed
	    		System.out.println(row.replaceAll("\\s",""));
	    }
	    
	    	//WRITE DATA TO CSV FILE
	    try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/vivekkandathil/Desktop/fileWritten.csv"), "utf-8"))) 
	    {
	    		//write a string to the file
	    		writer.write(sb.toString());
	    }
	    catch(Exception e)
	    {
	    		System.out.println(e);
	    }
	    
	    //Success message
	    System.out.println("File written");
	}
	
}
