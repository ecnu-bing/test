package GetNeedGet;

import Util.Tool;
import weibo4j.WeiboException;
import weibo4j.model.Status;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetNeedGetSinceID {
	
	protected String fileName = null;
	private boolean isInited = false;
	protected File[] flist = null;
	protected static int count = 0;
	protected String inputDir = null;
	protected String outputFile = null;
	protected String failedDir = null;
	SimpleDateFormat inputFormat = null;
	
	private static String separator = "|#|";

	public GetNeedGetSinceID()
	{
		
	}
	public GetNeedGetSinceID(String inputDir,String outputFile,String failedDir)
    {
	    this.inputDir = inputDir;
	    this.outputFile = outputFile;
	    this.failedDir = failedDir;
    }

	
	public void run() throws Exception
	{
		Init();
		try {
			getNeedGetSinceId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void Init()
	{
		this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("CHINA"));
		//this.failedUidPath = "."+File.separator+"data"+File.separator+"FailedUid_"+fileName;
		//this.needGetSinceIdFilePath = "."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetSinceId";
		//this.filepath="."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetMaxId";
		File dir = new File(this.inputDir);
		if (!dir.exists() || !dir.isDirectory())
			return;
		flist = dir.listFiles();
		isInited = true;
	}
	
	protected void getNeedGetSinceId()throws Exception
	{
		if (!isInited)
			Init();
		if (flist == null)
			return;

		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		List<Status> statusList = new ArrayList<Status>();
		List<String> recordList = new ArrayList<String>();
		boolean flag = false;
		Set<Long> midSet = new HashSet<Long>();
		Long mid = null;
		String uid = null;
		int realStatusNum = 0;
		long sinceId = 0l;
		String line = null;
		for (File file : flist)
		{
			//System.out.println(file.getName());
			File[] flist1 = file.listFiles();
			for(File fileLevel1 : flist1)
			{
			    System.out.println(fileLevel1.getName());
			    File[] flist2 = fileLevel1.listFiles();
			    for(File fileLevel2: flist2)
			    {
			        //System.out.println(fileLevel2.getName());
	                fis = new FileInputStream(fileLevel2);
	                isr = new InputStreamReader(fis);
	                br = new BufferedReader(isr);
	                
	                //清空计数
	                realStatusNum = 0;
	                flag = false;
	                
	                sinceId = 0l;
	                java.util.Date date = null;
	                int count=0;
	                while((line = br.readLine()) != null && !flag)
	                {
	                    //文件解析错误则跳过
	                    if(!line.startsWith("["))
	                    {
	                        continue;
	                    }
	                    try
	                    {
	                    //System.out.println(statusList.size());    
	                    statusList=Status.constructStatuses(line);
	            
	                    
	                    //现在文件没有合并，不存在重复，所以可直接根据list size得到计数
	                    //hasGetStatusNum += statusList.size();
	                    //System.out.print(statusList.size());
	                    for (int i = 0; i<statusList.size()&&!flag;i++)
	                    {
	                        //记录更新到的微博的id
	                        sinceId = statusList.get(0).getId();
	                        //System.out.println(statusList.get(0).getUser());
	                        if(statusList.get(i).getUser()!= null)
	                        {
	                            realStatusNum = statusList.get(i).getUser().getStatusesCount();
	                            flag = true;
	                            break;
	                        }
	                    }
	                    /*for(Status status:statusList)
	                    {
	                        mid = status.getId();
	                        if(!midSet.contains(mid))
	                        {
	                            midSet.add(mid);
	                        }
	                    }*/
	                    statusList.clear();
	                            
	                }catch (WeiboException e)
	                {
	                    
	                }
	                catch(NumberFormatException e1)
	                {
	                    System.out.println(fileLevel2.getAbsolutePath());
	                    continue;
	                    /*br.close();
	                    isr.close();
	                    fis.close();
	                    
	                    //move()
	                    String command = "mv " + fileLevel2.getAbsolutePath() + " z" +file.separator+"failed";
	                    try {
                            Process process = Runtime.getRuntime().exec(command);
                            process.waitFor();  
                        } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/
	                }
	                }
	                br.close();
	                isr.close();
	                fis.close();
	                uid = fileLevel2.getName().substring(0,fileLevel2.getName().indexOf("_"));
	                //Tool.write(this.outputFile, uid+"_"+sinceId+"_"+realStatusNum);
	                //
	                if(sinceId==0)
	                {
	                    recordList.add(uid);
	                    String command = "mv " + fileLevel2.getAbsolutePath() + " "+this.failedDir;
                        try {
                            Process process = Runtime.getRuntime().exec(command);
                            process.waitFor();  
                        } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
	                }
	                //System.out.println(uid+"_"+sinceId+"_0_"+realStatusNum+"_"+midSet.size());
	                //midSet.clear();
			    }
			    Tool.write(this.outputFile, recordList);
	            recordList.clear();
			}
			
		}
	}

    public static void main(String[] args) throws Exception
    {
        if(args.length<=2)
        {
            System.out.println("enter the inputDir and outputFile");
            return;
        }
        GetNeedGetSinceID getNeedGetSinceid = new GetNeedGetSinceID(args[0],args[1],args[2]);
        getNeedGetSinceid.run();
    }
}
