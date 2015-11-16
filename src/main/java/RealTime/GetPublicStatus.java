package RealTime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.IDs;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import Util.ShutdownThread;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class GetPublicStatus extends Thread {
    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String hotMid = null; 
    protected String realMid = null; 
    protected String outputDir = null;
    protected String outputDirMid = null;     //存取获取微博的mid的目录
    
    protected int mark = 0;     //标记，当mark=1时，表示当时获取的是用户最新发布的一条微博
    static protected int sleeping = 36000000/(GetStatusById.limitCount*6/10);
    Weibo weibo = null;

    Date start = null;
    SimpleDateFormat inputFormat = null;
    SimpleDateFormat createTimeFormat = null;
    
    Set<String> hotMidSet = null;
    Set<String> realMidSet = null;
    Set<String> failedToken = new HashSet<String>();
    SimpleDateFormat inputFormat1 = new SimpleDateFormat(
			"yyyy-MM-dd");
    int getStatusCount = 0;
    
    long startTime = 0l;
    int day = 1;

    protected void Init()
    {    	
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        createTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy",Locale.ENGLISH);
        String time = inputFormat.format((new Date()).getTime()).toString();
        startTime = new Date().getTime();
        System.out.println("start time : "+ time);
        
        this.outputDir = "."+File.separator+"data"+File.separator+"status"+File.separator;
        this.outputDirMid = "."+File.separator+"data"+File.separator+"mid"+File.separator;
        File testFile = new File(this.outputDir);
        if(!testFile.exists())
        {
            testFile.mkdir();
        }
        testFile = new File(this.outputDirMid);
        if(!testFile.exists())
        {
            testFile.mkdir();
        }
        
        
        this.hotMid = "."+File.separator+"data"+File.separator+"mid_needGet";
        this.realMid = "."+File.separator+"data"+File.separator+"real_mid";

        //read sinceidList;
        
        //boolean flag = false;
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try
        {        	
//        	Tool.refreshToken();
            tm = new TokenManage();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }      
        
    }
    public void run() 
    {
        Init();
        try {
			CrawlStatus();
		} catch (weibo4j.WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
    void readRealMid()
    {
        File file = new File(realMid);
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
                if (!realMidSet.contains(line))
                {
                    realMidSet.add(line);
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
    
 
    //获取最新公共微博
    protected int getPublicStatus(Weibo weibo, myWriter writeStatus, myWriter writeMid) throws weibo4j.WeiboException
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        while(true)
        {
        try
        {
        	res = weibo.getPublicPlace(200);
            //判断学校的网是否出问题了，待补充
            //
            //判断是否爬取正确格式的文件
            try
            {
            	if(!res.toString().equals("[]") && res!=null)
                {
            		wapper = Status.constructWapperStatus(res.toString());
                    if(wapper == null)
                    {
                        continue;
                    }
                    statusList = wapper.getStatuses();
                }
                else
                {
                	return 0;
                }
                    
            }catch (WeiboException e){
                e.printStackTrace();
                return 0;
            } 
            int i = 0;
/*            while(statusList.size()==0)
            {
                ++i;
                if(i==2)
                {
                    break;
                }
                System.out.println("retry the "+i+"th times");
                tm.ChekState();
                tokenpack = tm.GetToken();
                tm.AddIPCount(1);
                while (tokenpack == null || failedToken.contains(tokenpack.token))
                    tokenpack = tm.GetToken();
                weibo.setToken(tokenpack.token);
                res = weibo.getPublicPlace(200);
                if(!res.toString().equals("[]") && res!=null)
                {
                    wapper = Status.constructWapperStatus(res.toString());
                    if(wapper == null)
                    {
                        continue;
                    }
                    statusList = wapper.getStatuses();
                }
                else
                {
                    return 0;
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
            }*/
            //将结果写回文件
            if (res.toString().length() > 0 && statusList.size()>0)
            {
                writeStatus.Write(res.toString());
                for (Status status : statusList) 
                {                                                                                                                              
                	if(status.getRetweetedStatus() == null && status.getRepostsCount()>=500)
                	{
                		
                		long day = (new Date().getTime()-status.getCreatedAt().getTime())/(60*60*24*1000) ;
                		if(day <=5)
                		{	                		
	                		hotMidSet = new HashSet<String>();
	                		readHotMid();
	                		if(!hotMidSet.contains( status.getMid().toString()))
	                		{
	                			Tool.write(hotMid, status.getMid().toString(), true, "UTF8");  
	                			if(GetStatusById.getRepost!=null && GetStatusById.getRepost.isAlive() && GetHotRepost.readmid != null)
	                			{
	                				GetHotRepost.readmid.addId(status.getMid().toString());
	                			}
	                			if(GetStatusById.getRepost == null || !GetStatusById.getRepost.isAlive())
	                			{
	                				GetStatusById.getRepost= new GetHotRepost();
	                				sleeping = 3600000/(GetStatusById.limitCount*2/10);
                    				GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*1/10);
	                				GetStatusById.getRepost.start();
	                			}
	                		}
                		}
                	}
                	if(status.getRetweetedStatus() == null && status.getRepostsCount()>=3 && status.getRepostsCount()<500)
                	{
                		long day = (new Date().getTime()-status.getCreatedAt().getTime())/(60*60*24*1000) ;
                		if(day <= 5)
                		{
		                	realMidSet = new HashSet<String>();
		                	readRealMid();
		                	if(!realMidSet.contains( status.getMid().toString()))
		                	{
		                		Tool.write(realMid, status.getMid().toString(), true, "UTF8");
		                		RealDealMid.readmid.addId(status.getMid().toString());
		                		/*while( true)
		                         {
		                         	if(RealDealMid.readmid == null)
		                         	{
		                         		try
		                         		{
		                         			System.out.println("sleep : " + 1 +"s");
		                         			Thread.sleep(1000);
		                         		}   
		                                 catch (InterruptedException e1)
		                                 {
		                                     //e1.printStackTrace();
		                                 }
		                         	}
		                         	else
		                         	{
		                         		RealDealMid.readmid.addId(status.getMid().toString());
		                                break;
		                         	}
		                         }*/
		                         
	                		}
                		}
                	}
                	if(status.getRetweetedStatus() != null && status.getRetweetedStatus().getRepostsCount()>=500)
                	{
                		long day = (new Date().getTime()-status.getRetweetedStatus().getCreatedAt().getTime())/(60*60*24*1000) ;
                		if(day <= 5)
                		{
	                		hotMidSet = new HashSet<String>();
	                		readHotMid();
	                		if(!hotMidSet.contains( status.getRetweetedStatus().getMid().toString()))
	                		{
	                			Tool.write(hotMid, status.getRetweetedStatus().getMid().toString(), true, "UTF8");
	                			if(GetStatusById.getRepost !=null && GetStatusById.getRepost.isAlive() && GetHotRepost.readmid != null)
	                			{
	                				GetHotRepost.readmid.addId(status.getRetweetedStatus().getMid().toString());
	                			}
	                			if(GetStatusById.getRepost == null || !GetStatusById.getRepost.isAlive())
	                			{
	                				GetStatusById.getRepost= new GetHotRepost();
	                				sleeping = 3600000/(GetStatusById.limitCount*2/10);
                    				GetStatusById.sleeping = 3600000/(GetStatusById.limitCount*1/10);
	                				GetStatusById.getRepost.start();
	                			}
	                		}
                		}
                		
                	}
                	
                	if(status.getRetweetedStatus() != null && status.getRetweetedStatus().getRepostsCount()>=3 && status.getRetweetedStatus().getRepostsCount()<500)
                	{
                		long day = (new Date().getTime()-status.getRetweetedStatus().getCreatedAt().getTime())/(60*60*24*1000) ;
                		if(day <= 5)
                		{
		                	realMidSet = new HashSet<String>();
		                	readRealMid();
		                	if(!realMidSet.contains( status.getRetweetedStatus().getMid().toString()))
		                	{
		                		Tool.write(realMid, status.getRetweetedStatus().getMid().toString(), true, "UTF8"); 
		                		RealDealMid.readmid.addId(status.getRetweetedStatus().getMid().toString());
		                		/*while( true)
		                         {
		                         	if(RealDealMid.readmid == null)
		                         	{
		                         		try
		                         		{
		                         			System.out.println("sleep : " + 1 +"s");
		                         			Thread.sleep(1000);
		                         		}   
		                                 catch (InterruptedException e1)
		                                 {
		                                     //e1.printStackTrace();
		                                 }
		                         	}
		                         	else
		                         	{
		                         		RealDealMid.readmid.addId(status.getRetweetedStatus().getMid().toString());
		                                break;
		                         	}
		                         }*/
		                         
	                		}
                		}
                	}
                	writeMid.Write(status.getMid().toString());
                }
                    
            }
            break;
         }
            //判断微博返回错误
        catch (WeiboException e)
        {
//        	System.out.println(e.getMessage());

            System.out.println("error");
            // TODO Auto-generated catch block
            // TODO Auto-generated catch block
            if(e.getStatusCode() == 400)
            {
        		if(e.getErrorCode() == 10006)
        		{
        			System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
//                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                    {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
        		}
        		else
        		{           		
        			break;
        		}
        	}
            
            else if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
            {
                
                System.out.println("token invalid, change token");
                //failedToken.add(tokenpack.token);
//                tm.ChekState();
                tokenpack = tm.GetToken();
                tm.AddIPCount(1);
                while (tokenpack == null || failedToken.contains(tokenpack.token))
                {
                    System.out.println(tm.GetToken());
                    tokenpack = tm.GetToken();
                }
                weibo.setToken(tokenpack.token);
            }else if(e.getStatusCode() == 403)
            {
                System.out.println("error1");
//                tm.ChekState();
                tokenpack = tm.GetToken();
                tm.AddIPCount(1);
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
        
                
           
            //e.printStackTrace();
        } catch (weibo4j.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
   
    protected void CrawlStatus() throws weibo4j.WeiboException
    {
        getStatusCount = 0;
        myWriter writeStatus = null;
        myWriter writeMid = null;
        
        //实时微博数据获取
        while(true)
        {
        	//每12小时更新一次token
/*		    if(new Date().getTime() > startTime+day*12*60*60*1000)
		    {
		    	++day;
		            //从网络上更新token
		        Tool.refreshToken();
		            //更新代码使用token
		        tm = new TokenManage();
		    }*/
		        
		    //recovery
        	 this.outputDir = "."+File.separator+"data"+File.separator+"status"+File.separator+inputFormat1.format((new Date()).getTime())+File.separator;
        	File testFile = new File(this.outputDir);      
        	if(!testFile.exists())
    		{
    		    testFile.mkdir();
    		}        
		    writeStatus = new myWriter(this.outputDir+File.separator+"public_status", true);     //以当前时间命名存放获取微博数据的文件
		    writeMid = new myWriter(this.outputDirMid+"public_mid", false);		  //存放最新公共微博数据的文件
			           
//		    tm.ChekState();
			tokenpack = tm.GetToken();
			
			while(tokenpack == null)
			{
				tokenpack = tm.GetToken();
			}
            weibo = new Weibo();
            weibo.setToken(tokenpack.token);
            int responsecount = 0;
            responsecount = getPublicStatus(weibo,writeStatus,writeMid);
            	
            // -----------------------------------------------------------------

            tm.AddIPCount(1);
            System.out.println("ipcount: " + TokenManage.getIpcount());
		    writeStatus.closeWrite();
			writeMid.closeWrite();            

            if (responsecount <= 0)
            {
//            	writeStatus.delete();
                continue;
            }
			            


		   

		}

    }
    
    
    public static void main(String[] arg) throws weibo4j.WeiboException,
            IOException {
    	 ShutdownThread shutdown = new ShutdownThread();
    	
        GetPublicStatus getStatus = new GetPublicStatus();
        
        getStatus.run();
        
    }

}
