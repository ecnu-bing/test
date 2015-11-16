import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


import ly.Token.Token;
import ly.Token.TokenManage;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class GetDataByUid {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//TokenManage.refreshToken();
		String path = args[0];
		String userPath = path+File.separator+"user.txt";
		
        File testFile = new File(path+File.separator);
        if (!testFile.exists()) {
            testFile.mkdirs();
        }
        
		Weibo weibo = new Weibo();
		String uid="2328516855";
		
		TokenManage tm = new TokenManage();
		Token tokenPack = tm.GetToken();
		
		tm.setMaxCount(100);
		weibo.setToken(tokenPack.token);
		
		Response res;
		
		User user = null;
		try {
			 user=weibo.showUserById(uid);
			
		} catch (weibo4j.WeiboException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (WeiboException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userPath)));
			out.write(user.getFriendsCount()+"\t");
			out.write(user.getFollowersCount()+"\t");
			out.write(user.getCreatedAt()+"\t");
			int StatusesCount=user.getStatusesCount();
			out.write(StatusesCount);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				if(out != null){
					out.flush();
				}
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		//关注数
		System.out.println(user.getFriendsCount());
		
		//粉丝数
		System.out.println(user.getFollowersCount());
		
		//开通时间
		System.out.println(user.getCreatedAt());
		
		//活跃粉丝数
		
		//微博等级
		
		//微博数
		System.out.println(user.getStatusesCount());
		
		//粉丝中大v数
		
		
		//根据maxid不断爬取微博
		
		int statusesCount = user.getStatusesCount();
		int pageNum =statusesCount/20; 
		long maxid=3879457733275422l;
		Paging page = new Paging(1,100);
		page.setMaxId(maxid);
		int num=0;
		StatusWapper wapper;
		List<Status> list = new ArrayList<Status>();
		for(int i=0;;i++){
			if(tm.maxTokenCount(tokenPack)){
				try {
					res = weibo.getUserTimelineByUidTest(uid,page);
					wapper=Status.constructWapperStatus(res.toString());
					if(wapper!=null)
						list = wapper.getStatuses();
			
					System.out.println(wapper.getTotalNumber());
					maxid=list.get(list.size()-1).getId();
					page.setMaxId(maxid);
					num=num+list.size();
					System.out.println("第 "+i+" 次爬取"+"\tmaxid:"+maxid+"\t已爬数量："+num);
					sleep();
				} catch (weibo4j.WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}else{
				tokenPack = tm.GetNextToken();
				weibo.setToken(tokenPack.token);
			}
			if(list.size()<10){
				break;
			}
			
		}
//		if(tm.maxTokenCount(tokenPack)){
//			try {
//				res = weibo.getUserTimelineByUidTest(uid,page);
//				wapper=Status.constructWapperStatus(res.toString());
//				if(wapper!=null)
//					list = wapper.getStatuses();
//		
//			} catch (weibo4j.WeiboException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (WeiboException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//		}else{
//			tokenPack = tm.GetNextToken();
//			weibo.setToken(tokenPack.token);
//		}
		
		//System.out.println(res.toString());



		
			

		
	
		
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
			System.out.println(list.size());
	}
	public static void sleep(){
		double sleep=Math.random()*5000;
		sleep=Math.ceil(sleep);
		int time=new Double(sleep).intValue();
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
