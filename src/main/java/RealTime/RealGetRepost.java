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


import Util.MyLogger;
import Util.ShutdownThread;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class RealGetRepost extends Thread {

String thistimedir = null;
    
    static final String repostdir = "."+File.separator+"data"+File.separator+"repost"+File.separator;/*repostmid文件夹路径*/
    //static final String uiddir = ".\\realestate\\uid\\";/*uid文件夹路径*/

    String midpath = "."+File.separator+"data"+File.separator+"complete_mid";
    String expireMid = "."+File.separator+"data"+File.separator+"expire_mid";
    
    static StringReaderOne readmid = null;
    protected String outputDir = null;
    protected int mark = 0 ;
    static protected int sleeping = 36000000/(GetStatusById.limitCount*1/10);

    TokenManage tm = null;
    Token tokenpack = null;
    Weibo weibo = null;
    String start = null;
    SimpleDateFormat inputFormat = null;;
    SimpleDateFormat createTimeFormat = null;
    SimpleDateFormat inputFormat1 = new SimpleDateFormat(
			"yyyy-MM-dd");
    
    LogManager lMgr = LogManager.getLogManager();
    String thisName = "WeiboLog";
    Logger log = Logger.getLogger(thisName);
    MyLogger mylogger = null;

    Set<String> listSet = null;
//    Set<String> completeMidSet = null;

    int pagecount = 200;
    long startTime = 0l;
    Set<String> failedToken = new HashSet<String>();
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;
    int day = 1; 
    void init()
    {
    	readmid = new StringReaderOne(midpath);
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        createTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy",Locale.ENGLISH);
        startTime = new Date().getTime();
        mylogger = new MyLogger(inputFormat.format(startTime));
        lMgr.addLogger(log);
        
        this.outputDir = "."+File.separator+"data"+File.separator+"repost"+File.separator;
        listSet = new HashSet<String>();
//        completeMidSet = new HashSet<String>();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try
        {
 //           Tool.refreshToken();
            tm = new TokenManage();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        System.out.println("Start Crawling at " + inputFormat.format(startTime));

        thistimedir = thistimedir + File.separator;
//        readCompleteMid();
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
    
    public Date GetStatusById(Weibo weibo, String mid)
    {

        Date createTime = null;
        Response status = null;
        
        try
        { 
        	
        	 status = weibo.getStatusesById(mid);
             try
             {
             	if(status!=null)
             	{
             		createTime  = createTimeFormat.parse(status.asJSONObject().get("created_at").toString());
             	}
             	else
             	{
             		createTime  = createTimeFormat.parse("Sun Dec 11 16:05:23 CST 2011");
             	}

             }
             catch(Exception e)
             {
             	
             }
        	 
        }catch (weibo4j.model.WeiboException e1) {
            
            //e2.printStackTrace();
        }
        catch (WeiboException e) {
//                System.out.println("error");
        	System.out.println(e.getMessage());
                // TODO Auto-generated catch block
                // TODO Auto-generated catch block
                if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
                {
                    
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
 //                   tm.ChekState();
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
 //                   tm.ChekState();
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

        return createTime;
    }

    public int GetRepostByMid(Weibo weibo, String mid, myWriter writeStatus, Long sinceId, int page)
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        Paging paging = new Paging();
        paging.setCount(200);
        paging.setPage(page);
        paging.setSinceId(sinceId); 
        System.out.println("page : "+ page);
        while(true)
        {
            try
            {
                res = weibo.getRepostTimeline1(mid,paging);
                //判断是否爬取正确格式的文件
                if(!res.toString().equals("[]") && res!=null)
                {
                    wapper = Status.constructWapperStatus(res.toString());
                    if(wapper == null)
                    {
                        continue;
                    }
                    statusList = wapper.getStatuses();
                }else
                {
                    return 0;
                }
                
                //将结果写回文件
                if (res.toString().length() > 0 && statusList.size()>0)
                {

                    writeStatus.Write(res.toString());
                    for (Status status : statusList) 
                    {
                    	if(mark == 0)
                    	{
                    		Tool.write(repostdir+mid+"_tmp.txt", status.getMid(), false, "UTF8");
                    		mark++;
                    	}	
                    }
                    
                    
                }
                break;
            }  catch (WeiboException e) {
 //               System.out.println("error");
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
            }catch (weibo4j.model.WeiboException e1) {
                
                //e2.printStackTrace();
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
        if (statusList.size() > 0)
        {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);
        return ret;
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
/*    void readCompleteMid()
    {
        File file = new File(completemidPath);
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
                if (!completeMidSet.contains(line))
                {
                    completeMidSet.add(line);
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
*/
    void CrawlRepost()
    {
    	
        File file = new File(repostdir+"posReal.txt");
        if (!file.exists())
        {
        	try
        	{
        		file.createNewFile();
        	}
        	catch (IOException e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        }

        while(true)
        {
     	
	    	StringReaderOne readPos= new StringReaderOne(repostdir+"posReal.txt");
	        int pos = 0;
	        if(!readPos.IsOver())		//如果没有获取上一轮获取该用户微博数据时，用户最新发布微博的mid
	        {
	        	pos = Integer.parseInt(readPos.GetStrictNewID());
	        }
	        readmid.setPos(pos);
	        
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
	        
	        System.out.println(readmid.getPos());
	        myWriter writeStatus = null;

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
                Date date = GetStatusById(weibo,mid);
                long day =0 ;

                if(date != null)
                {
                	day = (new Date().getTime()-date.getTime())/(60*60*24*1000) ;
                }
                tm.AddIPCount(1, mid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

	            if(day>30)
	            {
	            	readmid.removeOldID(mid);
	            	Tool.write(expireMid, mid);
	            	continue;
	            }
	            //GetStatusById(weibo,mid);
	            
	            //recovery
	 /*           if(completeMidSet.contains(mid))
	            {
	                System.out.println(mid + " has been crawled");
	                readuid.idOK();
	                continue;
	            }
	            */
	            nowStatusCount = 0;
	            this.outputDir = "."+File.separator+"data"+File.separator+"repost"+File.separator+inputFormat1.format((new Date()).getTime())+File.separator;
	            File testFile = new File(this.outputDir);      
	        	if(!testFile.exists())
	    		{
	    		    testFile.mkdir();
	    		}  
	        	
	        	
	            writeStatus = new myWriter(this.outputDir+mid+"_repost", false);
	            
		        file = new File(repostdir+mid+"_tmp.txt");
		        if (!file.exists())
		        {
		        	try
		        	{
		        		file.createNewFile();
		        	}
		        	catch (IOException e)
		    		{
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		        	
		        }
		        StringReaderOne readMidTmp = new StringReaderOne(repostdir+mid+"_tmp.txt");
		        
		        String sinceMid = null ;
		        if(readMidTmp.IsOver())		//如果没有获取上一轮获取该用户微博数据时，用户最新发布微博的mid
		        {
		        	sinceMid="1";		        	
		        }
		        else
		        {
		        	sinceMid = String.valueOf(readMidTmp.GetStrictNewID());
		        }
	
		        Long sinceId = Long.parseLong(sinceMid);
	            
	            System.out.println("Start getting " + mid);
	            getStatusCount = 0;
	            mark = 0;
	            for(int page=1;;page++)
	            {
	                tm.ChekState();
//	                tokenpack = tm.GetToken();
	
	                while(tokenpack == null)
	                {
	                    tokenpack = tm.GetToken();
	                }
	                weibo = new Weibo();
	                weibo.setToken(tokenpack.token);
	
	                // -----------------------------------------------------------------
	                int responsecount = 0;
	                responsecount = GetRepostByMid(weibo,mid,writeStatus,sinceId,page);
	                
	
	                // -----------------------------------------------------------------
	
	                tm.AddIPCount(1, mid.toString());
	                System.out.println("ipcount: " + TokenManage.getIpcount());

	                if ((responsecount <= 0))
	                {
	                    break;
	                }
	                
	            }
	            writeStatus.closeWrite();
	//           Tool.write(completemidPath,mid);
	            readmid.idOK();
	            Tool.write(repostdir+"posReal.txt", String.valueOf(readmid.getPos()), false, "UTF8");
	        }
	        Tool.write(repostdir+"posReal.txt", "0", false, "UTF8");
//	        Tool.write(midpath, "",false,"UTF8");
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
        init();

        CrawlRepost();

        // CrawlUid();

        deconstruct();
    }

    public static void main(String[] args)
    {
        ShutdownThread shutdown = new ShutdownThread();

        RealGetRepost randu = new RealGetRepost();
        randu.run();
    }
}
