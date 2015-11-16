package GetNeedGet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import weibo4j.model.Status;
import weibo4j.WeiboException;

public class GetNeedGetMaxIdandSinceId {
	protected String fileName = null;
	private boolean isInited = false;
	protected File[] flist = null;
	protected static int count = 0;
	protected String inputDir = null;
	protected String failedUidPath = null;
	protected String needGetSinceIdFilePath = null;
	protected String needGetMaxIdFilePath = null;
	protected String needNotGetMaxIdFilePath = null;
	SimpleDateFormat inputFormat = null;
	
	private static String separator = "|#|";
	
	public GetNeedGetMaxIdandSinceId(String fileName)
	{
		if (fileName == null)
			return;

		this.fileName = fileName;
	}
	public static void main(String[] args) throws weibo4j.model.WeiboException
	{
		if(args.length<1)
		{
			System.out.println("enter the parater!");
			return;
		}
		GetNeedGetMaxIdandSinceId getNeedGetMaxIdandSinceId = new GetNeedGetMaxIdandSinceId(args[0]);
		getNeedGetMaxIdandSinceId.run();
	}
	public void run() throws weibo4j.model.WeiboException
	{
		Init();
		try {
			getNeedGetMaxIdandSinceId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void Init()
	{
		this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("CHINA"));
		this.failedUidPath = "."+File.separator+"data"+File.separator+"FailedUid_"+fileName;
		this.needNotGetMaxIdFilePath = ".." + File.separator + ".." + File.separator+ "userstatus" + File.separator + "needGetSinceId201107";
		this.needGetSinceIdFilePath = "."+File.separator+"data"+File.separator+"needGetSinceid_"+fileName;
		this.needGetMaxIdFilePath = "."+File.separator+"data"+File.separator+"needGetMaxid_"+fileName;
		this.inputDir = ".." + File.separator + ".." + File.separator+ "userstatus" + File.separator + "needGetSinceId&MaxId"+File.separator+fileName;
		//mycomputer
		//this.inputDir = ".\\data\\uid";
		File dir = new File(inputDir);
		if (!dir.exists() || !dir.isDirectory())
			return;
		flist = dir.listFiles();
		isInited = true;
	}
	protected void getNeedGetMaxIdandSinceId()throws IOException, weibo4j.model.WeiboException
	{
		if (!isInited)
			Init();
		if (flist == null)
			return;

		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		List<Status> statusList = new ArrayList<Status>();
		List<String> needGetMaxidList = new ArrayList<String>();
		List<String> needGetSinceidList = new ArrayList<String>();
		boolean flag = false;
		Set<Long> midSet = new HashSet<Long>();
		Long mid = null;
		int hasGetStatusNum = 0;
		int realStatusNum = 0;
		long maxId = 0l;
		long sinceId = 0l;
		for (File file : flist)
		{
			System.out.println(file.getName());
			File[] flist1 = file.listFiles();
			for(File fileLevel1 : flist1)
			{
				String line = "";
				fis = new FileInputStream(fileLevel1);
				isr = new InputStreamReader(fis, "GBK");
				br = new BufferedReader(isr);
				
				//清空计数
				hasGetStatusNum = 0;
				realStatusNum = 0;
				flag = false;
				maxId = 0l;
				sinceId = 0l;
				java.util.Date date = null;
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
						//现在文件没有合并，不存在重复，所以可直接根据list size得到计数
						hasGetStatusNum += statusList.size();
						for (int i = 0; i<statusList.size()&&!flag;i++)
						{
							//记录更新到的微博的id
							sinceId = statusList.get(0).getId();
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
						//记录获取的最后一条的微博的id
						if(statusList.size()>0)
						{
							maxId = statusList.get(statusList.size()-1).getId();
							if(statusList.get(statusList.size()-1).getUser() != null)
							{
								date = statusList.get(statusList.size()-1).getUser().getCreatedAt();
							}
						}
					}catch (WeiboException e)
					{
						
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
				
				String uid = null;
				if(hasGetStatusNum == 0)
				{
					failedUid = fileLevel1.getName().substring(0,fileLevel1.getName().indexOf("_"));
					write(this.failedUidPath,failedUid);
					commond = "rm " + fileLevel1.getPath();			
					
					try {
						Process process = Runtime.getRuntime().exec(commond);
						System.out.println(commond);
						process.waitFor();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					continue;
				}
				uid = fileLevel1.getName().substring(0,fileLevel1.getName().indexOf("_"));
				//needGetSinceidList.add(uid+"_"+sinceId);
				write(this.needGetSinceIdFilePath,uid+"_"+sinceId+"_"+realStatusNum);
				//
				/*判断缺失
				 * 如果用户实际微博数大于5000，则如果缺少大于300条微博则需要获取缺失
				 * 如果用户实际微博数小于等于5000，则如果缺少大于100条微博则需要获取缺失
				 */
				/*if(realStatusNum>5000)
				{
					if(realStatusNum - hasGetStatusNum >= 300)
					{
						uid = fileLevel1.getName().substring(0,fileLevel1.getName().indexOf("_"));
						//maxId = statusList.get(statusList.size()-1).getId();
						//needGetMaxidList.add(uid+"_"+maxId);
						write(this.needGetMaxIdFilePath,uid+"_"+maxId);
					}else
					{
						//mv 成功的文件
						commond = "mv " + fileLevel1.getPath() + " " + this.needNotGetMaxIdFilePath;			
						try {
							Process process = Runtime.getRuntime().exec(commond);
							process.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else
				{
					if(realStatusNum - hasGetStatusNum >= 100)
					{
						//记录要获取的need
						uid = fileLevel1.getName().substring(0,fileLevel1.getName().indexOf("_"));
						//maxId = statusList.get(statusList.size()-1).getId();
						write(this.needGetMaxIdFilePath,uid+"_"+maxId);
					}else
					{
						//mv 成功的文件
						commond = "mv " + fileLevel1.getPath() + " " + this.needNotGetMaxIdFilePath;			
						try {
							Process process = Runtime.getRuntime().exec(commond);
							process.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}*/
			}
			/*write(this.needGetMaxIdFilePath,needGetMaxidList);
			needGetMaxidList.clear();
			write(this.needGetSinceIdFilePath,needGetSinceidList);
			needGetSinceidList.clear();*/
			statusList.clear();
		}
	}
	static void write(String filepath,String str)
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

}
