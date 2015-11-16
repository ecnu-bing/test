package GetFailed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weibo4j.model.Status;
import weibo4j.WeiboException;

public class FailedSinceId {
	private String inputDir = "."+File.separator+"logs";
	 private String outputPath = "."+File.separator+"data"+File.separator+"uidBySinceidFailed";
	 private String dataDir = "."+File.separator+"data"+File.separator+"uidBySinceId";
	 public FailedSinceId()
	 {
		 
	 }
	 public static void main(String[] args) throws weibo4j.model.WeiboException
	 {
		 FailedSinceId failedSinceId = new FailedSinceId();
		 failedSinceId.run();
	 }
	 public void run() throws weibo4j.model.WeiboException
	 {
		 analysisLog();
	 }
	 private void analysisLog() throws weibo4j.model.WeiboException
	 {
		 File filedir = new File(inputDir);
	        String[] fileList = filedir.list();
	        FileInputStream fis;
	        InputStreamReader isr;
	        BufferedReader br;
	        FileInputStream fis1;
	        InputStreamReader isr1;
	        BufferedReader br1;
	        String line1 = null;
	        Set<String> uidSet = new HashSet<String>();
	        String line = null;
	        String uid = null;
	        String deleteFilepath = null;
	        String dataFile = null;
	        List<Status> statusList = new ArrayList<Status>();
	        Long maxId = null;
	        try {
	            for(String file:fileList)
	            {
	            	if(!file.contains("_sinceId_"))
	            	{
	            		continue;
	            	}
	                fis = new FileInputStream(inputDir+File.separator+file);
	                isr = new InputStreamReader(fis,"GBK");
	                br = new BufferedReader(isr);
	                while((line = br.readLine()) != null)
	                {
	                	System.out.println(line);
	                	if(line.startsWith("missing"))
	                    {
	                        uid = line.substring(line.indexOf(":")+2).trim();
	                        if(!uidSet.contains(uid))
	                        {
	                        	uidSet.add(uid);
	                            deleteFilepath = dataDir + File.separator + file.substring(0,file.indexOf("_"))+File.separator+uid+"_SinceIdstatus.txt.txt";
	                            System.out.println(deleteFilepath);
	                            deleteFile(new File(deleteFilepath));
	                        }
	                    } 
	                	else
	                	{
	                		String termList[] = line.split("_");
		                	if(termList.length < 5)
		                	{
		                		continue;
		                	}
		                	int oldStatusCount = Integer.parseInt(termList[2]);
		                	int newStatusCount = Integer.parseInt(termList[3]);
		                	int gotStatusCount = Integer.parseInt(termList[4]);
		                	String record = termList[0]+"_"+termList[1]+"_"+termList[2];
		                	if(newStatusCount - oldStatusCount > 100+gotStatusCount)
		                	{
	                            dataFile = dataDir + File.separator + file.substring(0,file.indexOf("_"))+File.separator+record+"_SinceIdstatus.txt.txt";
	                            if(!new File(dataFile).exists())
	                            {
	                            	continue;
	                            }
	                            fis1 = new FileInputStream(dataFile);
	        	                isr1 = new InputStreamReader(fis1,"GBK");
	        	                br1 = new BufferedReader(isr1);
	        	                while((line1 = br1.readLine()) != null)
	        	                {
	        	                	if (!line.startsWith("["))
	        						{
	        	                		continue;
	        						}
	        						try
	        						{
	        							statusList = Status.constructStatuses(line);
	        							//现在文件没有合并，不存在重复，所以可直接根据list size得到计数
	        							if(statusList.size()>0)
	        							{
	        								maxId = statusList.get(statusList.size()-1).getId();
	        							}
	        						}catch (WeiboException e)
	        						{
	        							//System.out.println(e.getMessage());
	        						}
	        	                }
	        	                if(maxId != null)
	        	                {
	        	                	write(".\\data\\uid_sinceId_maxId",termList[0]+"_"+termList[1]+"_"+maxId);
	        	                }
	        	                maxId = null;
	        	              }
		                	
	                	}
	                }
	                br.close();
	                isr.close();
	                fis.close();
	                //deleteFile(new File(inputDir+File.separator+file));
	            }
	            write(outputPath, uidSet);
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
	    private void write(String filepath,Set<String> list)
	    {
	        FileWriter fileWriter = null;
	        BufferedWriter bw= null;
	        try {
	            fileWriter = new FileWriter(filepath,true);
	            bw = new BufferedWriter(fileWriter);
	            for(String s:list)
	            {
	                bw.append(s);
	                bw.newLine();
	            }
	            bw.close();
	            fileWriter.close();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } 
	    }
	    static void deleteFile(File file)
	    {
	        try
	        {
	            if (file.exists())
	            {
	                file.delete();
	                System.out.println("delete："+file.getName());
	            }
	        }
	        catch (Exception e)
	        {
	            System.out.println("delete failed："+file.getName());
	            e.printStackTrace();
	        }
	    }

}
