package GetStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.IDs;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class friend {
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
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;
    
    long startTime = 0l;
    int day = 1;
    public void GetStatus() throws Exception {
        
    }
    protected void Init()
    {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = inputFormat.format((new Date()).getTime()).toString();
        startTime = new Date().getTime();
        System.out.println("start time : "+ time);
        
        this.outputDir = "."+File.separator+"data"+File.separator+"status"+File.separator;
        File testFile = new File(this.outputDir);
        if(!testFile.exists())
        {
            testFile.mkdir();
        }
        this.needGetUid = "." + File.separator + "data" + File.separator+ "uid_status";
        this.completeuidPath = "." + File.separator + "data" + File.separator+ "complete_status";
        //read sinceidList;
        readuid = new StringReaderOne(needGetUid);
        //read completeList;
        completeUidSet = new HashSet<String>();
        //getStatus
        readCompleteUid();
        boolean flag = false;
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
    public void run() throws JSONException
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
    protected void getStatusByUid(Weibo weibo,  String source,String target,String timegap) throws JSONException
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
       
        //StatusWapper res;
        Response res = null;
     
      
        while(true)
        {
            try
            {
                //res = weibo.getFriendsTimeline(uid, sinceId);
                //System.out.println("yes");
                res =  weibo.showIffriend(source,target);
                String result=res.asJSONObject().getString("source");
         	   
        	    String ret=result.substring(result.indexOf("following")+11,result.indexOf("followed_by")-2);
        	    
        	   Tool.write(".\\data\\status\\result", source+"\t"+target+"\t"+timegap+"\t"+ret);
        	   
            
            }
            //判断微博返回错误
            catch (WeiboException e)
            {
                if( e.getStatusCode() == 401)
                {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, source);
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
                    tm.AddIPCount(1, source);
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
                    
                    
                }else if(e.getStatusCode() == 400){
                break;
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
            break;
        }
      
    }
    
    protected void CrawlStatus() throws JSONException
    {
        getStatusCount = 0;
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
            newuid = newuid.replaceAll("\t", "");
            String [] user=newuid.split("\\|#\\|");
            //recovery
            if(completeUidSet.contains(newuid))
            {
                System.out.println(newuid + " has been crawled");
                readuid.idOK();
                continue;
            }
            nowStatusCount = 0;
            
            //writeStatus = new myWriter(this.outputDir+"result", false);
            
            System.out.println("Start getting " + newuid);
            getStatusCount = 0;
           
                tm.ChekState();
                tokenpack = tm.GetToken();

                while(tokenpack == null)
                {
                    tokenpack = tm.GetToken();
                }
                weibo = new Weibo();
                weibo.setToken(tokenpack.token);

                // -----------------------------------------------------------------
               
                getStatusByUid(weibo,user[0],user[1],user[2]);
                

                // -----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

              
                
           
            //writeStatus.closeWrite();
            Tool.write(completeuidPath,newuid);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }
    
    public static void main(String[] arg) throws weibo4j.WeiboException,
            IOException, JSONException {
        friend iffriend = new friend();
        iffriend.run();

    }

}
