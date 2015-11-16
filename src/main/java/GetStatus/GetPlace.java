package GetStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import Util.GetAccessToken;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class GetPlace {

    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String needGetUid = null;
    protected ArrayList<String> sinceidList = new ArrayList<String>();
    protected String completeuidPath = null;
    protected String outputDir = null;
    Weibo weibo = null;
    Date start = null;
    SimpleDateFormat inputFormat = null;
    static StringReaderOne readuid = null;
    Set<String> completeUidSet = null;
    Set<String> failedToken = new HashSet<String>();
    long realStatusCount = 0;
    String record = null;
    long startTime = 0l;
    int day = 1;
    protected void Init()
    {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        startTime = new Date().getTime();
        String time = inputFormat.format((new Date()).getTime()).toString();
        System.out.println("start time : "+ time);
        
        this.outputDir = "."+File.separator+"data"+File.separator+"place"+File.separator;
        File testFile = new File(this.outputDir);
        if(!testFile.exists())
        {
            testFile.mkdir();
        }
        this.needGetUid = "." + File.separator + "data" + File.separator+ "uid_place";
        this.completeuidPath = "." + File.separator + "data" + File.separator+ "complete_place";
        //read sinceidList;
        readuid = new StringReaderOne(needGetUid);
        //read completeList;
        completeUidSet = new HashSet<String>();
        readCompleteUid();
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
    
    public static void main(String[] arg) throws weibo4j.WeiboException,IOException {
        GetPlace update = new GetPlace();
        update.run();
    }
    
    public void run()
    {
        Init();

        CrawlStatus();
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
    protected int getPlaceStatus(Weibo weibo, myWriter writeStatus, String uid,int page)
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        Paging paging = new Paging();
        paging.setCount(50);
        paging.setPage(page);
        System.out.println("page : "+ page);
        while(true)
        {
            try
            {
                res = weibo.getPlaceByUid(uid,paging);
                /*SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                Calendar c=Calendar.getInstance(); 
                c.set(2013,9,10,00,00,00);
                String endTime = inputFormat.format(c.getTime());*/
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
                        
                        /*if(statusList.get(0).getCreatedAt()!=null&&inputFormat.format(statusList.get(0).getCreatedAt()).compareTo(endTime)>0){
                        	break;
                        }*/
                        realStatusCount = wapper.getTotalNumber();
                    }else
                    {
                        return 0;
                    }
                }catch (WeiboException e){
                    e.printStackTrace();
                    break;
                } 
                int i = 0;
                while(statusList.size()==0)
                {
                    ++i;
                    if(i==3)
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
                    weibo.getPlaceByUid(uid,paging);
                    if(!res.toString().equals("[]")&& res!=null)
                    {
                        wapper = Status.constructWapperStatus(res.toString());
                        if(wapper == null)
                        {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        realStatusCount = wapper.getTotalNumber();
                    }else
                    {
                        return 0;
                    }
                    
                }
                //将结果写回文件
                if (res.toString().length() > 0 && statusList.size()>0)
                {
                    writeStatus.Write(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e)
            {
                if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
                {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    tm.ChekState();
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
                    tm.ChekState();
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
            double a = Math.random()*5000;  
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
    protected void CrawlStatus()
    {
        int getStatusCount = 0;
        int responsecount = 0;
        myWriter writeStatus = null;
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
            //error recovery
            if(completeUidSet.contains(newuid))
            {
                System.out.println(newuid + " has been crawled");
                readuid.idOK();
                continue;
            }
            writeStatus = new myWriter(this.outputDir+newuid+"_placeStatus", false);
            responsecount = 0;
            getStatusCount = 0;
            System.out.println("Start getting " + newuid);
            for(int page=1;;page++)
            {
                tm.ChekState();
                tokenpack = tm.GetToken();

                while(tokenpack == null)
                {
                    tokenpack = tm.GetToken();
                }
                weibo = new Weibo();
                weibo.setToken(tokenpack.token);

                responsecount = getPlaceStatus(weibo,writeStatus,newuid,page);
                // -----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
                if ((responsecount <= 5) || (realStatusCount > 0&&(realStatusCount==getStatusCount)))
                {
                    break;
                }
                try
                {
                    double a = Math.random()*10000;  
                    a = Math.ceil(a);  
                    int randomNum = new Double(a).intValue(); 
                    System.out.println("sleep : " +randomNum/1000 +"s");
                    Thread.sleep(randomNum);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                
            }
            writeStatus.closeWrite();
            Tool.write(completeuidPath,newuid);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }
    

    
}
