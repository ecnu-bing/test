/**
 * WeiboCrawler2 
 * @date Jun 26, 2011
 * @author haixinma
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Util.TokenManage;
import Util.Tool;

import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import Dao.DBOI;

public class Test {
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public Test()
	{
		//db = new DBOI("twitter");
		//db1 = new DBOI("twitter");
	}
    public static void main(String[] args)
    {
    	//String str = "@xiafan68 @Nancy不敢喝牛奶 .....@爱呼吸的兔子 #@@测试一下。。.";
    	//String str1 = "回复@普利司通POTENZA-RE050A:test @wnqian //@普利司通POTENZA-RE050A:难道是蒙牛的 //@郑渊洁:回复@_微微凉_:介个恕不能透露，透露就成广告乐。 //@_微微凉_:真健康。不过很好奇您喝的牛奶是什么牌子。";
    	Test test = new Test();
    	//test.statis();
    	//test.getData();
    	//test.delaSocialNetwork();
    	//test.testRefreshToken();
    	
    }
    public void testRefreshToken()
    {
        int day = 0;
        long startTime = new Date().getTime();
        TokenManage tm = new TokenManage();
        while(true)
        {
            if(new Date().getTime() > startTime+day*60000)
            {
                System.out.println(new Date().toString());
                ++day;
                //从网络上更新token
                Tool.refreshToken();
                //更新代码使用token
                tm = new TokenManage();
                System.out.println(tm.GetToken());
            }
        }
    }
    public void delaSocialNetwork()
    {
        Map<String,Integer> outdegreeCount = new HashMap<String,Integer>();
        Map<String,Integer> indegreeCount = new HashMap<String,Integer>();
        ArrayList<String> edgeList = new ArrayList<String>();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        String text = null;
        List<Status> statusList = new ArrayList<Status>();
        try {
            fis = new FileInputStream(".\\data\\wnqian_all");
            isr = new InputStreamReader(fis,"GBK");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                String[] list = line.split("\t");
                if(indegreeCount.containsKey(list[0]))
                {
                    int temp = indegreeCount.get(list[0]);
                    indegreeCount.put(list[0], ++temp);
                }else
                {
                    indegreeCount.put(list[0], new Integer(0));
                }
                if(outdegreeCount.containsKey(list[1]))
                {
                    int temp = outdegreeCount.get(list[1]);
                    outdegreeCount.put(list[1], ++temp);
                }else
                {
                    outdegreeCount.put(list[1], new Integer(1));
                }
                edgeList.add(line);
            }
            br.close();
            isr.close();
            fis.close();
            
        }catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  
        catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        List<String> tempList = new ArrayList<String>();
        Iterator iterator = outdegreeCount.entrySet().iterator();
        while(iterator.hasNext())
        {
           Entry<String,Integer> entry = (Entry<String,Integer>) iterator.next();
           String key = entry.getKey();
           int value = entry.getValue();
           if(value>10)
           {
               if(!indegreeCount.containsKey(key) || indegreeCount.get(key)<10)
               {
                   tempList.add(key);
               }
           }
        }
        boolean flag = true;
        for(String edge:edgeList)
        {
            flag = false;
            for(String temp:tempList)
            {
                if(edge.contains(temp))
                {
                    flag =true;
                    
                }
            }
            if(!flag)
            {
                System.out.println(edge);
            }

        }
    }
    public void statis()
    {
        Map<Integer,Integer> followersCountMap = new HashMap<Integer,Integer>();
        long count = 0;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        //String inputDir = "Z:\\userstatus\\needGetSinceId201202\\1\\1";
        String inputDir = "Z:\\code\\data\\BackBone\\RTDistribution";
        File dir = new File(inputDir);
        File[] flist1 = dir.listFiles();
        String rtText =null;
        String text = null;
        List<Status> statusList = new ArrayList<Status>();
        try {
            for(File file:flist1)
            {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis,"utf8");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    ++count;
                    String[] list = line.split("\t");
                    int followersCount = Integer.parseInt(list[1]);
                    if(followersCountMap.containsKey(followersCount))
                    {
                        int temp = followersCountMap.get(followersCount);
                        followersCountMap.put(followersCount, ++temp);
                    }else
                    {
                        followersCountMap.put(followersCount,new Integer(1));
                    }
                }
                br.close();
                isr.close();
                fis.close();
                
            }
            
        }catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  
        catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        Iterator iterator = followersCountMap.entrySet().iterator();
        while(iterator.hasNext())
        {
           Entry<Integer,Integer> entry = (Entry<Integer,Integer>) iterator.next();
           int key = entry.getKey();
           int value = entry.getValue();
           System.out.println(key+"\t"+value);
        }
        System.out.println(count);
    }
    public void getData()
    {
        Map<String,Integer> edgeCount = new HashMap<String,Integer>();
    	FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        //String inputDir = "Z:\\userstatus\\needGetSinceId201202\\1\\1";
        String inputDir = ".\\data\\repost";
        File dir = new File(inputDir);
        File[] flist1 = dir.listFiles();
        int count = 0;
        String rtText =null;
        String text = null;
        List<Status> statusList = new ArrayList<Status>();
        try {
        	//for(File file:flist1)
        	{
        		//fis = new FileInputStream(file);
        	    fis = new FileInputStream("D:\\code\\Segment\\mergeFile");
                isr = new InputStreamReader(fis,"utf8");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    if(!line.equals(""))
                    {
                        System.out.println(line);
                    }
                    
                    /*if(!line.startsWith("["))
                    {
                        continue;
                    }*/
                	/*//System.out.println(line);
                	StatusWapper wapper = Status.constructWapperStatus(line);
                	//statusList = Status.constructStatuses(line);
                	statusList = wapper.getStatuses();
                	for(Status status:statusList)
                	{
                		System.out.println(inputFormat.format(status.getCreatedAt())+"\t"+status.getUser().getName()+"\t"
                		        +status.getRetweetedStatus().getUser().getName());
                	}
                	System.out.println("*******************************************");*/
                }
                br.close();
                isr.close();
                fis.close();
                
        	}
            
        }catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  
        catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } /*catch (WeiboException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (weibo4j.model.WeiboException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */
        System.out.println(count);
    }
}
