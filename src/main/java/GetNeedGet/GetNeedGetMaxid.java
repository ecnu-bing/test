package GetNeedGet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import weibo4j.model.Status;
import weibo4j.WeiboException;

public class GetNeedGetMaxid {
	protected String fileName = null;
	private boolean isInited = false;
	protected File[] flist = null;
	protected static int count = 0;
	protected String inputDir = null;
	protected String successedDir = null;
	protected String needGetMaxIdFilePath = null;
	protected String maxidList = null;
	SimpleDateFormat inputFormat = null;
	
	
	private static String separator = "|#|";
	
	public GetNeedGetMaxid()
	{
		
	}
	public static void main(String[] args) throws weibo4j.model.WeiboException
	{
		GetNeedGetMaxid getNeedGetMaxid = new GetNeedGetMaxid();
		getNeedGetMaxid.run();
	}
	public void run() throws weibo4j.model.WeiboException
	{
		Init();
		/*try {
			getNeedGetMaxid();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}*/
	}
	public void Init()
	{
		this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("CHINA"));
		this.successedDir = "."+File.separator+"data"+File.separator+"successedUid";
		this.needGetMaxIdFilePath = "."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetSinceId";
		this.maxidList = "."+File.separator+"data"+File.separator+"updatelist"+File.separator+"MaxidList";
		File dir = new File(needGetMaxIdFilePath);
		flist=dir.listFiles();
		isInited = true;
	}
	/*protected void getNeedGetMaxid()throws IOException, weibo4j.model.WeiboException
	{
		if (!isInited)
			Init();

		if(this.flist == null)
		{	
			return ;
		}
		
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		List<Status> statusList = new ArrayList<Status>();
		List<String> needGetMaxidList = new ArrayList<String>();
		Set<Long> midSet = new HashSet<Long>();
		Long mid = null;
		boolean flag = false;
		int hasGetStatusNum = 0;
		int realStatusNum = 0;
		long maxId = 0l;
		for (File file : flist)
		{
			System.out.println(file.getName());
			
			String line = "";
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "GBK");
			br = new BufferedReader(isr);
				
				//清空计数
			hasGetStatusNum = 0;
			realStatusNum = 0;
			flag = false;
			maxId = 0l;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("["))
				{

				}
				else
				{
					continue;
				}
				try
				{
					statusList = Status.constructStatuses(line);
					if(statusList == null)
					{
						write(".\\data\\failedUTF8.txt",file.getName().substring(0,file.getName().indexOf("_")));
						deleteFile(new File(file.getAbsolutePath()));
						continue;
					}
					for (int i = 0; i<statusList.size()&&!flag;i++)
					{
							
						if(statusList.get(i).getUser()!= null)
						{
							realStatusNum = statusList.get(i).getUser().getStatusesCount();
							flag = true;
							break;
							}
						}
						for(Status status:statusList)
						{
							mid = status.getId();
							if(!midSet.contains(mid))
							{
								midSet.add(mid);
							}
						}
						if(statusList.size()>0)
						{
							maxId = statusList.get(statusList.size()-1).getId();
						}
					}catch (WeiboException e)
					{
						//System.out.println(e.getMessage());
					}
							
				}
				br.close();
				isr.close();
				fis.close();
				hasGetStatusNum = midSet.size();
				midSet.clear();
				String failedUid = null;
				//如果获取失败，则从新获取，并将获取失败的文件删除
				String commond = null;
				
				判断缺失
				 * 如果用户实际微博数大于5000，则如果缺少大于300条微博则需要获取缺失
				 * 如果用户实际微博数小于等于5000，则如果缺少大于100条微博则需要获取缺失
				 
				if(statusList.size() == 0)
				{
					
				}
				String uid = null;
				uid = fileLevel1.getName().substring(0,fileLevel1.getName().indexOf("_"));
				if(realStatusNum == 0)
				{
					write(this.needGetMaxUid,file.getName()+"_"+uid);
				}
				
				if(realStatusNum>5000)
				{
					if(realStatusNum - hasGetStatusNum >= 500)
					{
						write(this.needGetMaxUid,file.getName()+"_"+uid+"_"+maxId);
						
					}else
					{
						//mv 成功的文件
						moveFile(file.getAbsolutePath()+file.separator+fileLevel1.getName(),this.successedDir+file.separator+fileLevel1.getName());
					}
				}else
				{
					if(realStatusNum - hasGetStatusNum >= 300)
					{
						write(this.needGetMaxUid,file.getName()+"_"+uid+"_"+maxId);
					}else
					{
						moveFile(file.getAbsolutePath()+file.separator+fileLevel1.getName(),this.successedDir+file.separator+fileLevel1.getName());
					}
				}
		
		}
	}*/
	static void write(String filepath,String str)
    {
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
        BufferedWriter bw= null;
        try {
        	fileos = new FileOutputStream(filepath, true);
			osw = new OutputStreamWriter(fileos,"GBK");
			bw = new BufferedWriter(osw);
            bw.append(str);
            bw.newLine();    
            bw.close();
			osw.close();
			fileos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
	static void write(String filepath,List<String> list)
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
                System.out.println("delete file ："+file.getName());
            }
        }
        catch (Exception e)
        {
            System.out.println("failed delete file ："+file.getName());
            e.printStackTrace();
        }
    }
	static void moveFile(String source,String destination)
    {
        try
        {
        	File file = new File(source);
            if (file.exists())
            {
            	InputStream fis;
				InputStreamReader fisr;
				BufferedReader br;
				try {
					fis = new FileInputStream (file);
					fisr = new InputStreamReader(fis,"GBK");
					br = new BufferedReader(fisr);
					String rline = null;
					while ((rline = br.readLine()) != null)
					{
						write(destination,rline);
					}
					br.close();
					fisr.close();
					fis.close();
					file.delete();
				}
				catch (IOException e) {
					  e.printStackTrace();
				}	
            }
        }
        catch (Exception e)
        {
            System.out.println("failed rm file ："+source);
            e.printStackTrace();
        }
    }

}
