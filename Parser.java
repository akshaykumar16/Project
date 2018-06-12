package jdbc;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author Akshay16
 */
public class Parser {

    /**
     * @param args the command line arguments
     */
  private static Connection myConn=null;
  private static Statement myStmt=null;
  private static ResultSet myResult=null;
  private static PreparedStatement myPrep=null;
    
  private static final DateTimeFormatter FORMATTER=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");//Two formatters have different strings.
  private static final DateTimeFormatter FORMATTER1=DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss.SSS");
 
 
     private static final Scanner SCAN=new Scanner(System.in);
 
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException  {
        // TODO code application logic here 
        String startDate= args[0];
        String duration=args[1];
        int threshold=Integer.parseInt(args[2]);
        String url="jdbc:MySql://localhost:3306/demo?autoReconnect=true&useSSL=false";//path from mysql
        String user="student";//username
        String pass="student";//password
        
        try
        {
            myConn=DriverManager.getConnection(url,user,pass);//establishing connection with mysql
            CallableStatement myCall=myConn.prepareCall("{call `insert_into_mysql`(?,?,?,?,?)}");//calling storedprocedure
            
            
             FileInputStream fstream = new FileInputStream("access.log");//
             BufferedReader br = new BufferedReader(new InputStreamReader(fstream));//parsing a log file
             String strLine;

        
        while ((strLine = br.readLine()) != null)//ends at the end of the logfile
        {
            
     
            String[] i=strLine.split("[|]");//spliting each log file with delimiter pipe(|) as requested
        
            String timestamp = i[0];
            LocalDateTime t1=LocalDateTime.parse(timestamp,FORMATTER);//converting date to localdatetime
            Timestamp ts=Timestamp.valueOf(t1);//converting localdatetime to timestamp  
            myCall.setTimestamp(1, ts);//storing value of each record in their appropiate column
            myCall.setString(2, i[1]);
            myCall.setString(3,i[2]);
            myCall.setInt(4, Integer.parseInt(i[3]));
            myCall.setString(5, i[4]); 
            myCall.execute();//executing the stored procedure
        }
         
        }
        
        catch(SQLException | FileNotFoundException e)
         {
           System.out.println(e.getMessage());
        }
        
        //System.out.println("Enter the date and the format of the date is yyyy-MM-dd.HH:mm:ss ");//To be executed from run file.
        //String startDate=SCAN.nextLine();
        //System.out.println("Enter the duration and the duration can take string either 'hourly'   or'daily'   " );
        //String duration=SCAN.next();
        //System.out.println("Enter the threshold value ");
        //int threshold=SCAN.nextInt();
        findIps(startDate,duration,threshold);// calling the method.if you pass time as "Hourly" you will get number of requests made by a IP in a particular hour 
        //and if you pass time as "daily" you will get number of request made by ip in a day.
    }
    public static void findIps(String startDate,String time,int threshold) throws SQLException
    {
        
        PreparedStatement stmt=myConn.prepareStatement("select Ip,count(Ip) as GreaterThanThreshold from LogFil where Dat  between  ? and  ?  group by Ip having count(Ip)>?");
        
        LocalDateTime localdate=LocalDateTime.parse(startDate.concat(".000"), FORMATTER1);
        Timestamp t1=Timestamp.valueOf(localdate);
        LocalDateTime end;
             Timestamp t2=null;
        if(time.equalsIgnoreCase("hourly"))
        {
           end=localdate.plusHours(1);
           t2=Timestamp.valueOf(end);
        }
        else if(time.equalsIgnoreCase("daily"))
        {
            end=localdate.plusHours(24);
            t2=Timestamp.valueOf(end);
        }
     
        else
        {
       
            System.out.println("please enter either daily or hourly");
 
        }
        
        stmt.setTimestamp(1, t1);
        stmt.setTimestamp(2, t2);
        stmt.setInt(3,threshold);
        
        myResult=stmt.executeQuery();
        System.out.println("IP Address   \t  Thresholdvalue");
        while(myResult.next())
        {
            int res=myResult.getInt("GreaterThanThreshold"); 
            String res1=myResult.getString("Ip");
            System.out.println(res1+"\t  "+res);
           
           
            myPrep=myConn.prepareStatement("insert into whyItsBlocked"+"(IP,threshold)"+"values"+"(?,?)");
            myPrep.setString(1, res1);
            myPrep.setInt(2, res);
            myPrep.executeUpdate();
        }
        
        
        
    }
    
    }


