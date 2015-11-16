package GetFailed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import Util.Tool;

public class FailedGetUid {

	 private String inputDir = "."+File.separator+"data"+File.separator+"status";
	 private String outputPath = "."+File.separator+"data"+File.separator+"uid_failed_status";
	 private String outputPath1 = "."+File.separator+"data"+File.separator+"uid_Maxid_status";
	 private String tag = null;
	 protected String outputDataDir = null;
	 
	 public FailedGetUid(String tag,String ouputDataDir)
	 {
	     this.tag = tag;
		 this.outputDataDir = ouputDataDir;
	 }
	 
	 public FailedGetUid()
	 {
	    
	 }
	 public static void main(String[] args)
	 {
	     if(args.length!=2)
	     {
	         System.out.println("enter the type and output path,if you want to move data,type is move; if you want to " +
	         		"get the maxId to crawl the complete data, type is maxId");
	         return ;
	     }
		FailedGetUid failedGetUid = new FailedGetUid(args[0],args[1]);
		
		failedGetUid.run();
	 }
	 public void run()
	 {
	     if(this.tag.equals("move"))
	     {
	         analysisData();
	     }else if(this.tag.equals("maxId"))
	     {
	         getMaxId();
	     }
		 analysisData();
		 
	 }
	 private void getMaxId()
	 {
	     File filedir = new File(inputDir);
        File[] fileList = filedir.listFiles();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        StatusWapper wapper = null;
        List<Status> statusList = new ArrayList<Status>();
        int count = 0;
        String maxId = null;
        int hisStatusCount = 0;
        long nowStatusCount = 0 ;
        boolean flag = false;
        ArrayList<String> resultList = new ArrayList<String>();
        String deleteFilepath = null;
        try {
            for(File file:fileList)
            {
                System.out.println(file.getName());
                //***初始化变量***
                maxId = null;
                count = 0;
                hisStatusCount = 0;
                nowStatusCount = 0;
                flag = false;
                String[] list = file.getName().split("_");
              
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis,"GBK");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    if(!line.endsWith("}")||!line.startsWith("{"))
                    {
                        continue;
                    }
                    resultList.add(line);
                   
                    wapper = Status.constructWapperStatus(line);
                    if(!flag)
                    {
                        nowStatusCount = wapper.getTotalNumber();
                        flag = true;
                    }
                    statusList = wapper.getStatuses();
                    if(statusList.size()>0)
                    {
                        maxId = statusList.get(statusList.size()-1).getMid();
                    }
                    count += statusList.size();
                    statusList.clear();
                }
                br.close();
                isr.close();
                fis.close();
                //如果获取数据比需要获取的数据少则得到maxId继续爬数据。
                //System.out.println(maxId);
                long needGetCount = nowStatusCount - hisStatusCount;
                long missStatusCount = nowStatusCount - count;
                //缺失大于2%，则需要继续获取
                if(maxId!=null && flag && missStatusCount>(double)nowStatusCount*10/100 && missStatusCount>=50)
                {
                    //写出文件内容，uid_sinceId_maxId_oldStatus_newStatus_getStatusThisTime
                    String record = list[0]+"_"+maxId+"_"+missStatusCount;
                    //System.out.println(record);
                    Tool.write(this.outputPath1, record);
                }
                //获取数据差少于2%条 或者 总量差值 小于 20，则算成功
                if(flag&& (missStatusCount<=(double)nowStatusCount*10/100||missStatusCount<50))
                {
                    //已成功再删除
                    if(Tool.write(this.outputDataDir+File.separator+file.getName(), resultList))
                    {
                        Tool.deleteFile(file);
                    }else
                    {
                        System.out.println("write file error");
                        return;
                    }
                  
                }
                if(!flag)
                {
                    if(Tool.write(this.outputPath, list[0]))
                    {
                        Tool.deleteFile(file);
                    }
                }
                resultList.clear();
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
            } catch (WeiboException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (weibo4j.model.WeiboException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
    }
	 private void analysisData()
	 {
		 File filedir = new File(inputDir);
		 File[] fileList = filedir.listFiles();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        StatusWapper wapper = null;
        List<Status> statusList = new ArrayList<Status>();
        int count = 0;
   
        long nowStatusCount = 0 ;
        boolean flag = false;
        ArrayList<String> resultList = new ArrayList<String>();
        String deleteFilepath = null;
        try {
            for(File file:fileList)
            {
                //***初始化变量***
                count = 0;
               
                nowStatusCount = 0 ;
                flag = false;  
                String[] list = file.getName().split("_");
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis,"GBK");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    if(!line.startsWith("{")||!line.endsWith("}"))
                    {
                        continue;
                    }
                    resultList.add(line);
                    try {
                        wapper = Status.constructWapperStatus(line);
                    }catch (weibo4j.model.WeiboException e) {
                        // TODO Auto-generated catch block
                        System.err.println("reconstruct the json error");
                        continue;
                        //e.printStackTrace();
                    }catch (WeiboException e) {
                        // TODO Auto-generated catch block
                        
                        System.err.println("reconstruct the json error");
                        continue;
                        //e.printStackTrace();
                    } 
                    if(wapper==null)
                    {
                        System.err.println("reconstruct the json error");
                        continue;
                    }
                    if(!flag)
                    {
                        nowStatusCount = wapper.getTotalNumber();
                        flag = true;
                    }
                    
                    statusList = wapper.getStatuses();
                    count += statusList.size();
                    statusList.clear();
                }
                br.close();
                isr.close();
                fis.close();
                //获取数据差少于1%条，则算成功
                if(flag&& (nowStatusCount - count)<= (double)nowStatusCount*2/100||(nowStatusCount-count)<20)
                {
                    //已成功再删除
                    if(Tool.write(this.outputDataDir+File.separator+file.getName(), resultList))
                    {
                        Tool.deleteFile(file);
                    }else
                    {
                        System.out.println("write file error");
                        return;
                    }
                  
                }
                if(!flag)
                {
                    if(Tool.write(this.outputPath, list[0]))
                    {
                        Tool.deleteFile(file);
                    }
                }
                resultList.clear();
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
	    }        	   


}
