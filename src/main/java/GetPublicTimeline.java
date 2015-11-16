

import java.util.ArrayList;
import java.util.List;

import ly.Token.Token;
import ly.Token.TokenManage;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class GetPublicTimeline {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TokenManage.refreshToken();
		
		Weibo weibo = new Weibo();
		Response res = new Response();
		
		TokenManage tm = new TokenManage();
		Token tokenPack = tm.GetToken();
		
		tm.setMaxCount(100);
		weibo.setToken(tokenPack.token);
		
		if(tm.maxTokenCount(tokenPack)){
			try {
				res = weibo.getPublicPlace(1);
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			tokenPack = tm.GetNextToken();
			weibo.setToken(tokenPack.token);
		}
		
		//System.out.println(res.toString());
		
		List<Status> list = new ArrayList<Status>();
		long iw=0;
		try {
			if(!res.toString().equals("[]") && res!=null){
				StatusWapper wapper = Status.constructWapperStatus(res.toString());
				iw=wapper.getTotalNumber();
				if(wapper!=null)
					list = wapper.getStatuses();
			}
		} catch (weibo4j.WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
			System.out.println(iw);
			System.out.println(list.size());
	}

}
