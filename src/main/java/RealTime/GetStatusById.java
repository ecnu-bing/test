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
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class GetStatusById extends Thread {
    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String needGetUid = null;
    protected String needGetMid = null;       //上一轮获取该用户微博数据时，用户最新发布微博的mid
    protected String hotMid = null; 
    protected String realMid = null; 
    protected ArrayList<String> sinceidList = new ArrayList<String>();
    protected String completeuidPath = null;
    protected String missinguidPath = null;
    protected String outputDir = null;
    protected String outputDirMid = null;     //存取获取微博的mid的目录
    
    protected int mark = 0;     //标记，当mark=1时，表示当时获取的是用户最新发布的一条微博
    static protected int limitCount = 20000 ;
    static protected int sleeping = 36000000/(limitCount*3/10);
    Weibo weibo = null;
    static GetHotRepost getRepost = null;

    Date start = null;
    SimpleDateFormat inputFormat = null;
    SimpleDateFormat createTimeFormat = null;
    static StringReaderOne readuid = null;
    static StringReaderOne readmid = null;  //读取上一轮获取该用户微博数据时，用户最新发布微博的mid
    SimpleDateFormat inputFormat1 = new SimpleDateFormat(
			"yyyy-MM-dd");
    Set<String> completeUidSet = null;
    Set<String> hotMidSet = null;
    Set<String> realMidSet = null;
    Set<String> failedToken = new HashSet<String>();
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;
    
    long startTime = 0l;
    int day = 1;
    public void RealTime() throws Exception {
        
    }
    protected void Init()
    {    	
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        createTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy",Locale.ENGLISH);
        String time = inputFormat.format((new Date()).getTime()).toString();
        startTime = new Date().getTime();
        System.out.println("start time : "+ time);
        
        this.outputDir = "."+File.separator+"data"+File.separator+"status"+File.separator+File.separator+inputFormat1.format((new Date()).getTime())+File.separator;
        this.outputDirMid = "."+File.separator+"data"+File.separator+"mid"+File.separator;
       
        File testFile = new File(this.outputDirMid);
        if(!testFile.exists())
        {
            testFile.mkdir();
        }
        
        this.needGetUid = "." + File.separator + "data" + File.separator+ "uid_status";
        this.completeuidPath = "." + File.separator + "data" + File.separator+ "complete_status";
        this.missinguidPath = "." + File.separator + "data" + File.separator+ "missing_status";  //??
        this.hotMid = "."+File.separator+"data"+File.separator+"mid_needGet";
        this.realMid = "."+File.separator+"data"+File.separator+"real_mid";

        //read sinceidList;
        
        //boolean flag = false;
        try
        {        	
            Tool.refreshToken();
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
    void readCompleteUid()
    {
        File file = new File(completeuidPath);
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
            isr = new InputStreamReader(fis,"GBK");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                if (!completeUidSet.contains(line))
                {
                    completeUidSet.add(line);
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
    
 

    
    //获得用户发布大微博
    protected int getStatusByUid(Weibo weibo, myWriter writeStatus, myWriter writeMid ,String uid, Long sinceId, int page) throws weibo4j.WeiboException
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        paging.setSinceId(sinceId);    	

        System.out.println("page : "+ page);
           
        while(true)
        {
            try
            {            	
                res = weibo.getUserTimelineByUidTest(uid,paging);
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
                        nowStatusCount = wapper.getTotalNumber();
                    }else
                    {
                        return 0;
                    }
                    
                }catch (WeiboException e){
                    e.printStackTrace();
                    break;
                } 
                int i = 0;
            /*    while((this.nowStatusCount==0 && statusList.size()==0)||(this.nowStatusCount >= this.getStatusCount && statusList.size()==0))
                {
                    ++i;
                    if(i==2)
                    {
                        break;
                    }
                    System.out.println("retry the "+i+"th times");
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                        tokenpack = tm.GetToken();
                    weibo.setToken(tokenpack.token);
                    res = weibo.getUserTimelineByUidTest(uid,paging);
                    if(!res.toString().equals("[]") && res!=null)
                    {
                        wapper = Status.constructWapperStatus(res.toString());
                        if(wapper == null)
                        {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        nowStatusCount = wapper.getTotalNumber();
                    }else
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
                    
                    String firstMid = "0";
                    for (Status status : statusList) 
                    {
                    	//获取用户最新发布微博的mid
                    	if(Long.parseLong(status.getMid()) < sinceId && statusList.size() == 1)
                    	{                   		
                    		statusList.clear();
                            System.out.println("ret : 0 " );                             
                            return 0;
                    		
                    	}

                    	if(mark < 2)
                    	{
                    		if(firstMid.compareTo(status.getMid().toString())<0)
                    		{
                    			firstMid = status.getMid().toString();
                    		}
                    		mark++;
                    	}
                    	if(mark == 2)
                    	{
                    		try
                    		{
                    			BufferedWriter writeMidTmp = new BufferedWriter(new FileWriter(this.outputDirMid+uid+"_mid_tmp.txt", false));
                    			writeMidTmp.append(firstMid);
                        		writeMidTmp.newLine();
                        		writeMidTmp.flush();
                        		writeMidTmp.close();
                    		}catch (IOException e)
                    		{
                    			// TODO Auto-generated catch block
                    			e.printStackTrace();
                    		}
                    		mark++;
                    		
                    		
                    	}
                    	if(status.getRetweetedStatus() == null && status.getRepostsCount()>=500)
                    	{
                    		long day = (new Date().getTime()-status.getCreatedAt().getTime())/(60*60*24*1000) ;
                    		if(day <= 5)
                    		{
	                    		hotMidSet = new HashSet<String>();
	                    		readHotMid();
	                    		if(!hotMidSet.contains( status.getMid().toString()))
	                    		{
	                    			Tool.write(hotMid, status.getMid().toString(), true, "UTF8");
	                    			if(getRepost !=null && getRepost.isAlive() && GetHotRepost.readmid != null)
		                			{
		                				GetHotRepost.readmid.addId(status.getMid().toString());
		                			}
	                    			if(getRepost == null || !getRepost.isAlive())
	                    			{
	                    				getRepost= new GetHotRepost();
	                    				sleeping = 3600000/(GetStatusById.limitCount*1/10);
	                    				GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*2/10);
	                    				getRepost.start();
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
	                    			if(getRepost != null && getRepost.isAlive() && GetHotRepost.readmid != null)
		                			{
		                				GetHotRepost.readmid.addId(status.getRetweetedStatus().getMid().toString());
		                			}
	                    			if(getRepost == null || !getRepost.isAlive())
	                    			{
	                    				getRepost= new GetHotRepost();
	                    				sleeping = 3600000/(GetStatusById.limitCount*1/10);
	                    				GetPublicStatus.sleeping = 3600000/(GetStatusById.limitCount*2/10);
	                    				getRepost.start();
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
                    	//写入微博mid
                    	
                    	writeMid.Write(status.getMid().toString());
         	        }
                    writeStatus.Write(res.toString());
                    
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e)
            {
//            	System.out.println(e.getMessage());

                System.out.println("error");
                // TODO Auto-generated catch block
                // TODO Auto-generated catch block
                if(e.getStatusCode() == 400)
                {
            		if(e.getErrorCode() == 10006)
            		{
            			System.out.println("token invalid, change token");
                        //failedToken.add(tokenpack.token);
//                        tm.ChekState();
                        tokenpack = tm.GetToken();
                        tm.AddIPCount(1, uid);
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
 //                   tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
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
                    tm.AddIPCount(1, uid);
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
            System.out.println("sleep : " +randomNum/1000 +"s====");
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
            readuid = new StringReaderOne(needGetUid);
            
            //read completeList;
            completeUidSet = new HashSet<String>();
            //getStatus
            readCompleteUid();
            
            //实时获取指定账户微博数据
		    while (!readuid.IsOver())
		    {
		       //每12小时更新一次token
		        if(new Date().getTime() > startTime+day*12*60*60*1000)
		        {
		            ++day;
		            //从网络上更新token
		            Tool.refreshToken();
		            //更新代码使用token
		            tm = new TokenManage();
		        }
		        String newuid = String.valueOf(readuid.GetStrictNewID());
		        
		        //recovery
		        if(completeUidSet.contains(newuid))
		        {
		            System.out.println(newuid + " has been crawled");
		            readuid.idOK();
		            continue;
		        }
		        nowStatusCount = 0;
		        
		        this.needGetMid = this.outputDirMid+newuid+"_mid_tmp.txt";
		        
		        File file = new File(needGetMid);
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
		        readmid = new StringReaderOne(needGetMid);
		        
		        String sinceMid = null ;
		        if(readmid.IsOver())		//如果没有获取上一轮获取该用户微博数据时，用户最新发布微博的mid
		        {
		        	sinceMid="1";		        	
		        }
		        else
		        {
		        	sinceMid = String.valueOf(readmid.GetStrictNewID());
		        }

		        Long sinceId = Long.parseLong(sinceMid);
		        
		        mark = 0;
		        
		        System.out.println("Start getting " + newuid);
		        getStatusCount = 0;
		        
		        this.outputDir = "."+File.separator+"data"+File.separator+"status"+File.separator+inputFormat1.format((new Date()).getTime())+File.separator;
		        File testFile = new File(this.outputDir);
		        if(!testFile.exists())
		        {
		            testFile.mkdir();
		        }
		        writeStatus = new myWriter(this.outputDir+newuid+"_status", false);
		        writeMid = new myWriter(this.outputDirMid+newuid+"_mid", false);		        
		        for(int page=1;;page++)
		        {		        

//		            tm.ChekState();
		            tokenpack = tm.GetToken();
		
		            while(tokenpack == null)
		            {
		                tokenpack = tm.GetToken();
		            }
		            weibo = new Weibo();
		            weibo.setToken(tokenpack.token);
		
		            // -----------------------------------------------------------------
		            int responsecount = 0;
		            responsecount = getStatusByUid(weibo,writeStatus,writeMid,newuid,sinceId,page);
		            
		
		            // -----------------------------------------------------------------
		
		            tm.AddIPCount(1, newuid.toString());
		            System.out.println("ipcount: " + TokenManage.getIpcount());
		
		            getStatusCount += responsecount;
		            if ((responsecount <= 0) || getStatusCount == this.nowStatusCount)
		            {
		                break;
		            }
		            
		   
		        }
		        
		        
		        if(this.nowStatusCount>2000){
		            	 Tool.write(missinguidPath,newuid);
		            }
		        writeStatus.closeWrite();
		        writeMid.closeWrite();
		        
		    
		        Tool.write(completeuidPath,newuid);
		        readuid.idOK();
		    }
		    
		    try
		    { 
		    	FileWriter fw = new FileWriter(completeuidPath,false);
		    	fw.write("");
		    	fw.close();
		    }
		    catch (IOException e){
	   				// TODO Auto-generated catch block
		    	   e.printStackTrace();
		    }
        
//	        Tool.write(completeuidPath,"",false,"UTF8");   //获取一轮指定用户微博数据后，清空completeuidPath中的用户id
	        
/*	        t2 = TokenManage.getIpcount()-t1;
	        t1 = TokenManage.getIpcount();
	        
	        //获取最新公共微博数据
	        while((TokenManage.getIpcount()-t1)/t2 != 2)    //使获取指定用户微博数据和最新公共微博数据占用带宽比为 1:2
	        {
	        	 //每12小时更新一次token
	            if(new Date().getTime() > startTime+day*12*60*60*1000)
	            {
	                ++day;
	                //从网络上更新token
	                Tool.refreshToken();
	                //更新代码使用token
	                tm = new TokenManage();
	            }
	        	        	
	            writeStatus = new myWriter(this.outputDir+"public_status", true);     //以当前时间命名存放获取微博数据的文件
	            writeMid = new myWriter(this.outputDirMid+"public_mid", false);		  //存放最新公共微博数据的文件
	            
	            tm.ChekState();
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
	
	            if (responsecount <= 0)
	            {
	                continue;
	            }
	            writeStatus.closeWrite();
	            writeMid.closeWrite();
		        	
	        }
	        t1 = TokenManage.getIpcount();
*/
	        System.out.println("=========completed==============");     //一轮实时微博数据获取完成

        }
    }
    
    
    public static void main(String[] arg) throws weibo4j.WeiboException,
            IOException {
    	
        GetStatusById getStatusById = new GetStatusById();
        GetPublicStatus getPublicStatus = new GetPublicStatus();
        RealGetRepost realGetRepost = new RealGetRepost();
        RealDealMid realDealMid = new RealDealMid();
        realDealMid.start();
       
        while(true){
        	System.out.println("========The Crawler is starting=========");
        	if(realDealMid.readmid!=null)
        	break;
        
       }
     
        getStatusById.start();
        getPublicStatus.start();
        realGetRepost.start();
        

    }

}
