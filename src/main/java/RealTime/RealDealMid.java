package RealTime;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;


import weibo4j.org.json.JSONException;
import Util.MyLogger;
import Util.ShutdownThread;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class RealDealMid extends Thread {

	String thistimedir = null;

    String midpath = "."+File.separator+"data"+File.separator+"real_mid";
    String expireMid = "."+File.separator+"data"+File.separator+"expire_mid";
    
    static StringReaderOne readmid;
    
    
    static protected int sleeping = 36000000/(GetStatusById.limitCount/2*6/10);


    TokenManage tm = null;
    Token tokenpack = null;
    Weibo weibo = null;
    String start = null;
    SimpleDateFormat inputFormat = null;;
    SimpleDateFormat createTimeFormat = null;
    protected String hotMid = null; 

    LogManager lMgr = LogManager.getLogManager();
    String thisName = "WeiboLog";
    Logger log = Logger.getLogger(thisName);
    MyLogger mylogger = null;

    Set<String> listSet = null;
    Set<String> hotMidSet = null;
//    Set<String> completeMidSet = null;

    long startTime = 0l;
    Set<String> failedToken = new HashSet<String>();
    int day = 1; 
    void init() throws IOException
    {	
    	this.hotMid = "."+File.separator+"data"+File.separator+"mid_needGet";
    	File file=new File(midpath);
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	readmid = new StringReaderOne(midpath);
    
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        createTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy",Locale.ENGLISH);
        startTime = new Date().getTime();
        mylogger = new MyLogger(inputFormat.format(startTime));
        lMgr.addLogger(log);
        listSet = new HashSet<String>();
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try
        {
//            Tool.refreshToken();
            tm = new TokenManage();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        System.out.println("Start Crawling at " + inputFormat.format(startTime));

        thistimedir = thistimedir + File.separator;

    }
    
    void readHotMid()
    {
        File file = new File(hotMid);
        if(!file.exists())
        {
            return;
        }
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf-8");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                if (!hotMidSet.contains(line))
                {
                    hotMidSet.add(line);
                }
            }
            fis.close();
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void newFolder(String folderPath)
    {
        try
        {
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            if (!myFilePath.exists())
            {
                myFilePath.mkdir();
            }
        }
        catch (Exception e)
        {
            System.out.println("新建文件夹操作出错");
            e.printStackTrace();
        }
    }

    int MutedTest(Weibo weibo, String id)
    {
        int count = 0;
        return count;
    }

    void deconstruct()
    {
        tm.CloseLogger();
        System.out.println("All data get succeeded at "
                + inputFormat.format(new Date().getTime()));
    }
    
    public void GetStatusById(Weibo weibo, String mid)
    {

        //StatusWapper res;
        Response res = null;
        Date createTime = null;
        int count = 0;
        try
        {
        	res = weibo.getStatusesById(mid);
       
        	try 
        	{        		
        		createTime = createTimeFormat.parse(res.asJSONObject().get("created_at").toString()); 

        		count = res.asJSONObject().getInt("reposts_count");
        	} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	}
        	if(createTime == null)
        	{
        		try {
        			createTime = createTimeFormat.parse("Tue May 31 17:46:55 +0800 2011");
        		} catch (ParseException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
        		}
        	}

         		
         	long day = (new Date().getTime()-createTime.getTime())/(60*60*24*1000) ;
         	
         	if(day<=2 && count>=500)
         	{
         		hotMidSet = new HashSet<String>();
         		readHotMid();
         		if(!hotMidSet.contains(mid))
         		{
         			Tool.write(hotMid, mid, true, "UTF8");
         			if(GetStatusById.getRepost !=null && GetStatusById.getRepost.isAlive() && GetHotRepost.readmid != null)
         			{
         				GetHotRepost.readmid.addId(mid);
         			}
         			if(GetStatusById.getRepost == null || !GetStatusById.getRepost.isAlive())
         			{
         				GetStatusById.getRepost= new GetHotRepost();
         				GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        				GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*2/10);
         				GetStatusById.getRepost.start();
         			}
         		}
         		
         		readmid.removeOldID(mid);
         		readmid.setPos(readmid.getPos()-1);
         		
         	}
//         	if(day>2 && count<500)
         	if(day>2)
 	        {	
 //        		Tool.write("E:\\text", mid);
 	            readmid.removeOldID(mid);
 	            readmid.setPos(readmid.getPos()-1);
 	        }

       
       
       
                
        
            } catch (weibo4j.model.WeiboException e1) {
                
                //e2.printStackTrace();
            } catch (WeiboException e) {
  //              System.out.println("error");
            	System.out.println(e.getMessage());
                // TODO Auto-generated catch block
                // TODO Auto-generated catch block
                if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
                {
                    
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
//                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, mid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                    {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                }else if(e.getStatusCode() == 403)
                {
                    System.out.println("error1");
//                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, mid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                    {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }  
                    weibo.setToken(tokenpack.token);
                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    try
                    {
                        double a = Math.random()*50000;  
                        a = Math.ceil(a);  
                        int randomNum = new Double(a).intValue(); 
                        System.out.println("sleep : " +randomNum/1000 +"s");
                        Thread.sleep(randomNum);
                    }catch (InterruptedException e1)
                    {
                        //e1.printStackTrace();
                    }
                    
                    
                }
            }


      
        try
        {
            double a = Math.random()*sleeping;  
            a = Math.ceil(a);  
            int randomNum = new Double(a).intValue(); 
            System.out.println("sleep : " +randomNum/1000 +"s");
            Thread.sleep(randomNum);
        }
        catch (InterruptedException e1)
        {
            //e1.printStackTrace();
        }

        return;
}

    void WriteFailLog()
    {
        System.out.println(inputFormat.format(new Date())
                + " #Fail Get id at rootuid pos of " + readmid.getPos());
        mylogger.Write(inputFormat.format(new Date())
                + " #Fail Get id at rootuid pos of " + readmid.getPos());
        System.out.println(inputFormat.format(new Date())
                + " #Fail Get Token at token pos " + tm.getPos());
        mylogger.Write(inputFormat.format(new Date())
                + " #Fail Get Token at token pos " + tm.getPos());
    }

    private static boolean IsConnectDisableOfNCEU(String SC)
    {
        Pattern pattern=Pattern.compile("window\\.location='http://202\\.120\\.95\\.235'");
        Matcher matcher=pattern.matcher(SC);
        if(matcher.find())
        {
            return true;
        }
        return false;
    }

    void CrawlRepost()
    {

        while(true)
        {
        	while(true)
        	{
        		 SimpleDateFormat s = new SimpleDateFormat("HH"); 
        		  
        		 if(s.format(new Date()).equalsIgnoreCase("00"))
        		 {
        			 break;
        		 }
        		 else
        		 {
        			 System.out.println("sleep:59分钟");
        		        try {
        					Thread.sleep(3540000);
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        		 }
        			 
        			  
        			  
        		
        	}
        	GetStatusById.limitCount = GetStatusById.limitCount/2;
        	if(GetStatusById.getRepost == null || !GetStatusById.getRepost.isAlive())
        	{
        		GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*3/10);
        		GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*6/10);
        		RealGetRepost.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        	}
        	else
        	{
        		GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        		GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*2/10);
        		GetHotRepost.sleeping = 3600000/(GetStatusById.limitCount*6/10);
        		RealGetRepost.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        	}
        	readmid.setPos(0);
            while(true)
            {
            	
    	        if(readmid.IsOver())
    	        {
    	        	try
                    {
    	        		int a=100000;
                        System.out.println("sleep : " +a/1000 +"s");
                        Thread.sleep(a);
                        continue;
                    }
                    catch (InterruptedException e1)
                    {}
    	        }
    	        else
    	        {
    	        	break;
    	        }
            }
	        	        

	        while (!readmid.IsOver())
	        {
	            //每12小时更新一次token
/*	            if(new Date().getTime() > startTime+day*12*60*60*1000)
	            {
	                ++day;
	                //从网络上更新token
	                //Tool.refreshToken();
	                //更新代码使用token
	                tm = new TokenManage();
	            }*/
	            String mid = String.valueOf(readmid.GetStrictNewID());

//	            tm.ChekState();
                tokenpack = tm.GetToken();

                while(tokenpack == null)
                {
                    tokenpack = tm.GetToken();
                }
	            weibo = new Weibo();
                weibo.setToken(tokenpack.token);
                GetStatusById(weibo,mid);
                tm.AddIPCount(1, mid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

	            readmid.idOK();
	        }
	        
	       try
	       { 
	    	   FileWriter fw = new FileWriter(midpath,false);
	    	   fw.write("");
	    	   fw.close();
	       }
	       catch (IOException e){
   				// TODO Auto-generated catch block
	    	   e.printStackTrace();
   			}
	        readmid.setPos(0);
	        while(!readmid.IsOver())
	        {
	        	Tool.write(midpath,readmid.GetStrictNewID());
	        	readmid.idOK();
	        }
	        System.out.println("=========completed==============");
	        
	        GetStatusById.limitCount = GetStatusById.limitCount*2;
        	if(GetStatusById.getRepost == null || !GetStatusById.getRepost.isAlive())
        	{
        		GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*3/10);
        		GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*6/10);
        		RealGetRepost.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        	}
        	else
        	{
        		GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        		GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*2/10);
        		GetHotRepost.sleeping = 3600000/(GetStatusById.limitCount*6/10);
        		RealGetRepost.sleeping = 3600000/(GetStatusById.limitCount*1/10);
        	}
        	
	        System.out.println("sleep:1小时");
	        try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
        }
    }

    private void write(String filepath,String str)
    {
        FileWriter fileWriter = null;
        BufferedWriter bw= null;
        try {
            fileWriter = new FileWriter(filepath,true);
            bw = new BufferedWriter(fileWriter);
            bw.append(str);
            bw.newLine();    
            bw.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    // ---------------------------------------------------------------------------------------------
    public void run()
    {
        try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        CrawlRepost();

        // CrawlUid();

        deconstruct();
    }

    public static void main(String[] args)
    {
        ShutdownThread shutdown = new ShutdownThread();

        RealDealMid randu = new RealDealMid();
        randu.run();
    }
}

