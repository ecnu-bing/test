import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import myUtil.writer;
import ly.Token.Token;
import ly.Token.TokenManage;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.IDs;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class crawUserNetWork {

	private List<String>uids;//原始的uid
	

	private String urlSource;//原始uid的地址
	private String urlOnly;//存放仅有爬下来的uid的文件位置
	private String urlPair;//存放原uid和爬下来的uid对，用制表符隔开
	
	private writer writer;
	private Weibo weibo; 
	private Response res;
	private TokenManage tm;
	

	
	/*
	 * 初始化一些值，并读取原始uid
	 */
	public void init(){
	    this.writer = new writer();
	    
	    this.weibo = new Weibo();
		this.res = new Response();
		
		TokenManage.refreshToken();
		this.tm = new TokenManage();
		this.tm.setMaxCount(100);
		
		//读入原始uid
		this.uids = new LinkedList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(this.urlSource)));
			String temp = "";
			while((temp = br.readLine()) != null){
				this.uids.add(temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(urlOnly.trim().equals("")||urlOnly.isEmpty()){
			System.err.println("输出文件的位置为空");
			System.exit(0);
		}
		if(urlPair.trim().equals("")||urlPair.isEmpty()){
			System.err.println("输出文件的位置为空");
			System.exit(0);
		}
		//输出文件不存在则创建
		File f = new File(urlOnly);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File f2 = new File(urlPair);
		if(!f2.exists()){
			try {
				f2.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 一共爬取两次，共三层
	 */
	public void run(){

		List<String> one = getUserFriends(this.uids);

		getUserFriends(one);
	}
	
	/*
	 * 根据uid列表抓取其关注列表，返回抓取到的列表
	 */
	public List<String> getUserFriends(List<String> uidlist){
			
		long[]uidsLong;//每个uid抓取后的返回结果到一个long数组中
		List<String> uids = new LinkedList<String>();//返回的List
			
		Token token = this.tm.GetToken();
		this.weibo.setToken(token.token);
		
		for(String uid:uidlist){
			while(true){
				if(this.tm.maxTokenCount(token)){
					try {
						//爬取
						IDs ids = this.weibo.getFriendsIDSByUserId(uid);
						uidsLong = ids.getIDs();
					
						//写数据
						this.writer.setUrl(this.urlOnly);
						this.writer.write(uidsLong);
						this.writer.setUrl(this.urlPair);
						this.writer.write(uidsLong, uid);
						List<String> temp = new LinkedList<String>();
						for(long id:uidsLong){
							temp.add(String.valueOf(id));
						}
						uids.addAll(temp);
						break;
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
		                {
		                    System.out.println("token invalid, change token");
		                    //failedToken.add(tokenpack.token);

		                }else if(e.getStatusCode() == 403)
		                {

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
		               
						e.printStackTrace();
					}
				}else{
					token = this.tm.GetNextToken();
					this.weibo.setToken(token.token);
				}
			}

		
			try {
				double time = Math.random()*5000;
				time = Math.ceil(time);
				int randomNum = new Double(time).intValue();
				Thread.sleep(randomNum);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return uids;
		
		
	}
	
	public String getUrlSource() {
		return urlSource;
	}

	public void setUrlSource(String urlSource) {
		this.urlSource = urlSource;
	}

	public String getUrlOnly() {
		return urlOnly;
	}

	public void setUrlOnly(String urlOnly) {
		this.urlOnly = urlOnly;
	}

	public String getUrlPair() {
		return urlPair;
	}

	public void setUrlPair(String urlPair) {
		this.urlPair = urlPair;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		crawUserNetWork cun =  new crawUserNetWork();
		String urlSource = "D:\\data\\uid.txt";
		String urlOnly = "D:\\data\\uidOnly.txt";
		String urlPair = "D:\\data\\uidPair.txt";
		cun.setUrlSource(urlSource);
		cun.setUrlOnly(urlOnly);
		cun.setUrlPair(urlPair);
		cun.init();
		cun.run();
	}
}
