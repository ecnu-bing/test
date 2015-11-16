package Trends;


import weibo4j.Trend;
import weibo4j.Weibo;
import weibo4j.model.Trends;
import weibo4j.model.WeiboException;

import java.util.List;

public class GetTrendsDaily {
	public static void main(String[] args) {
		String access_token = "2.00dcoq1BmNsuOD893728189bzq4OJB";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Trend tm = new Trend();
		List<Trends> trends = null;
		try {
			trends = tm.getTrendsDaily("1963525982",5,1);
			for(Trends ts : trends){
				System.out.print(ts.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}


}
