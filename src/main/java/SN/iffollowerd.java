package SN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import weibo4j.Weibo;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import Dao.Dao;
import Dao.JDBC;
import Util.Token;
import Util.TokenManage;
import Util.Tool;

public class iffollowerd {
	
	private static Connection queryconnection = null;
	static Dao dao;
	Token tokenpack = null;
	static TokenManage tm = null;	
	int localIpcout;
	
	static void init()
	{
		try
		{
			//Tool.refreshToken();
			tm = new TokenManage();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		
	}
	static boolean GetDB()
	{
		// !!JDBC.setURL();
		JDBC.setURL("jdbc:mysql://localhost:3306/test");
		queryconnection = JDBC.getConnection();
		
		if (queryconnection == null)
		{
			System.out.println("Fail when server get connect to sql");
			return false;
		}

		dao = new Dao();
		return true;
	}
	 protected void run(String line) throws WeiboException, weibo4j.WeiboException, InterruptedException, IOException
	 {

		 Weibo weibo = null;
		 localIpcout++;
		 
		 String s=line.substring(0,line.lastIndexOf(" "));
         String[] info=s.split("<-");
         String user1=info[0].split(" ")[0];
         String user2=info[1].split(" ")[0].substring(info[1].split(" ")[0].indexOf("#")+1);
       
         while(true){
         tm.ChekState();
         tokenpack = tm.GetToken();
			
			 if (tokenpack == null)
				continue;
			 weibo = new Weibo();
			 weibo.setToken(tokenpack.token);
			
			 try 
		 {
         User u1 = weibo.showUserByName(user1);
        
        //User u2 = weibo.showUserByName(user2);
       
         write("E://changetouid.txt", u1.getId()+"<-"+user2);
		 }catch (weibo4j.model.WeiboException e1)
		{ 
	           if(e1.getStatusCode() == 401)
			  {
				System.out.println("token invalid, change token");
				tm.ChekState();
				tokenpack = tm.GetToken();
				
				tm.AddIPCount(localIpcout, "ll");
				while (tokenpack == null)
					tokenpack = tm.GetToken();
			
				weibo.setToken(tokenpack.token);
				continue;
				}
	          else if(e1.getStatusCode()==400)
	           {
					write("E://wrong.txt",line);
					break;
				}
	           else if(e1.getStatusCode() == 403)			
			   {					
				System.out.println("request too many times , sleep 5~45s");
				try
				{						
				    double a = Math.random()*50000;  
                  a = Math.ceil(a);  
                  int randomNum = new Double(a).intValue(); 
                  System.out.println("sleep : " +randomNum/1000 +"s");
                  Thread.sleep(randomNum);
                  tm.ChekState();
					tokenpack = tm.GetToken();
					tm.AddIPCount(localIpcout, "ll");
					while (tokenpack == null)
						tokenpack = tm.GetToken();
					System.out.println(tokenpack.token);
					weibo.setToken(tokenpack.token);
					continue;
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else		
			{
				 double a = Math.random()*50000;  
                  a = Math.ceil(a);
				 int randomNum = new Double(a).intValue(); 
               System.out.println("sleep : " +randomNum/1000 +"s");
               Thread.sleep(randomNum);
               continue;
			}
			
			//WriteFailLog();
			//e1.printStackTrace();
		}
			 break;
         }
        // Vector v=dao.GetUserFriendsID(user2);
        
         /*if(v!=null)
         {
        	 System.out.println(v.size());
        	 
        	 if(!v.contains(user1))
             {
        	   System.out.println("no");
        	   tool.write(".\\data\\unfriendship", line);
        	 }
         }
         
         v.clear();*/ 
     
	 }
	 protected void getEdgeinFrindship() throws WeiboException, weibo4j.WeiboException, InterruptedException, IOException
	 {
		 FileInputStream fis;
	     InputStreamReader isr;
	     BufferedReader br = null;
	     String line = null;
	     
	     File file = new File("E:\\result_bockboneNetwork_10000");
	     //File file = new File("Z:\\code\\data\\report\\RetweetTree1\\part-00000");
	     fis = new FileInputStream(file);
         isr = new InputStreamReader(fis,"utf-8");
         br = new BufferedReader(isr);
      
		 
			 while((line = br.readLine()) != null)
	         {
				 run(line);
	         }
		 
			  
	 }
	 static void write(String filepath,String str)
	    {
		
	        FileWriter fileWriter = null;
	        BufferedWriter bw= null;
	        try {
	            fileWriter = new FileWriter(filepath,true);
	            bw = new BufferedWriter(fileWriter);
	            bw.append(str);
	            bw.newLine();    
	            bw.flush();
	            fileWriter.close();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } 
	    }
	public static void main(String[] args) throws WeiboException, weibo4j.WeiboException, InterruptedException, IOException
	{	 
		init();
		//if (!GetDB())
		//	return;
		
		iffollowerd f=new iffollowerd();
		f.getEdgeinFrindship();
		
	}
	

}


