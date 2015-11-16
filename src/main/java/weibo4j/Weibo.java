package weibo4j;

import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.*;
import weibo4j.model.Paging;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

/**
 * @author sinaWeibo
 * 
 */

public class Weibo implements java.io.Serializable {

	private static final long serialVersionUID = 4282616848978535016L;
	public static String CONSUMER_KEY = "";
	public static String CONSUMER_SECRET = "";
	public static String REDIRECT_URI = "";
	public static HttpClient client = new HttpClient();

	/**
	 * Sets token information
	 * 
	 * @param token
	 */
	public synchronized void setToken(String token) {
		client.setToken(token);
	}
   
	public IDs getFriendsIDSByUserId(String userid,Integer count) throws WeiboException{
		return new IDs((Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "friendships/friends/ids.json",
				new PostParameter[] { new PostParameter("uid", userid) ,new PostParameter("count",count.toString())})),this);
	}
	
	public IDs getFriendsIDSByUserId(String uid
			, Integer count, Integer cursor) throws WeiboException{
		return new IDs((Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "friendships/friends/ids.json",
				new PostParameter[] { new PostParameter("uid",uid ) ,new PostParameter("count",count.toString()),new PostParameter("cursor", cursor.toString())})),this);
	}
	
	public IDs getFriendsIDSByUserId(String userid) throws WeiboException{
		return new IDs((Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "friendships/friends/ids.json",
				new PostParameter[] { new PostParameter("uid", userid) })),this);
	}
	public StatusWapper getFriendsTimeline3(String userid,String max_id) throws weibo4j.WeiboException, WeiboException {
		return Status.constructWapperStatus(Weibo.client.get(WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",new PostParameter[] {
			new PostParameter("uid", userid),
			new PostParameter("max_id", max_id) }));

	}
	
	public IDs getFollowsIDSByUserId(String userid,Paging paging) throws WeiboException{
			
			return new IDs((Weibo.client.get(
					WeiboConfig.getValue("baseURL") + "friendships/followers/ids.json",
					new PostParameter[] { new PostParameter("uid", userid) },paging)),this);
	}


    public User showUserByDomain(String domain) throws WeiboException, weibo4j.WeiboException {
        return new User(Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "users/domain_show.json",
                new PostParameter[] { new PostParameter("domain", domain) })
                .asJSONObject());
    }

	public User showUserByName(String uname) throws WeiboException, weibo4j.WeiboException {
		return new User(Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "users/show.json",
				new PostParameter[] { new PostParameter("screen_name", uname) })
				.asJSONObject());
	}
    public User showUserById(String uid) throws WeiboException, weibo4j.WeiboException {
        return new User(Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "users/show.json",
                new PostParameter[] { new PostParameter("uid", uid) })
                .asJSONObject());
    }
	
	public Response shorturlTolongurl(String shorturl) throws weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                        WeiboConfig.getValue("baseURL") + "short_url/expand.json",
                        new PostParameter[] {new PostParameter("url_short", shorturl)});
    }
	
	public Response getStatusesById(String mid) throws weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                        WeiboConfig.getValue("baseURL") + "statuses/show.json",
                        new PostParameter[] {new PostParameter("id", mid)});
    }
	
	public Response getPublicPlace(int count) throws WeiboException{
	    return Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "statuses/public_timeline.json",
                new PostParameter[] {new PostParameter("count", count)}
               );
    }
	
	public Response getFriendsTimeline2(String userid,String max_id) throws weibo4j.WeiboException, WeiboException {
		return Weibo.client.get(WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",new PostParameter[] {
			new PostParameter("uid", userid),
			new PostParameter("max_id", max_id)});
	}
		
	public Response getFriendsTimeline4(String userid,String since_id) throws weibo4j.WeiboException, WeiboException {
		return Weibo.client.get(WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",new PostParameter[] {
			new PostParameter("uid", userid),
			new PostParameter("since_id", since_id)});
	}


    public Response getUserBatch(String uid) throws weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "statuses/show_batch.json",
                new PostParameter[]{new PostParameter("ids", uid)});
    }

    /*
    通过微博的id来爬取其评论
     */
    public Response getComments(String id) throws  weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "comments/show.json",
                new PostParameter[] {new PostParameter("id", id)});
    }

    /*
    通过uid来获得该用户的微博
     */
	public Response getUserTimelineByUidTest(String uid, Paging page) throws weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                        WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",
                        new PostParameter[] {new PostParameter("uid", uid)},page);
    }
	
	public Response getUserTimelineByUidTest(String uid, Paging page,Long maxId) throws weibo4j.WeiboException, WeiboException {
        return Weibo.client.get(
                        WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",
                        new PostParameter[] {new PostParameter("uid", uid),new PostParameter("maxId", maxId)},page);
    }
	
	public StatusWapper getUserTimelineByUid(String uid, Paging page) throws weibo4j.WeiboException, WeiboException {
        return Status.constructWapperStatus(Weibo.client.get(
                        WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",
                        new PostParameter[] {new PostParameter("uid", uid)},page));
    }
	public StatusWapper getFriendsTimeline(String userid,String since_id) throws weibo4j.WeiboException, WeiboException {
			return Status.constructWapperStatus(Weibo.client.get(WeiboConfig.getValue("baseURL") + "statuses/user_timeline.json",new PostParameter[] {
				new PostParameter("uid", userid),
				new PostParameter("since_id", since_id)}));

	}
	public Response getPlaceByUid(String uid,Paging page) throws WeiboException{
	    return Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "place/user_timeline.json",
                new PostParameter[] {new PostParameter("uid", uid)},page);
    }
	public Response showIffriend(String source,String target) throws WeiboException{
	    return Weibo.client.get(
                WeiboConfig.getValue("baseURL") + "friendships/show.json",
                new PostParameter[] {new PostParameter("source_id", source),new PostParameter("target_id", target)});
    }

	public StatusWapper getRepostTimeline(String mid,Paging page)throws weibo4j.WeiboException, WeiboException
	{
	    return Status.constructWapperStatus(Weibo.client.get(
                WeiboConfig.getValue("baseURL")
                        + "statuses/repost_timeline.json",
                new PostParameter[] { new PostParameter("id", mid) }, page));
	}
	public Response getRepostTimeline1(String mid,Paging paging)throws weibo4j.WeiboException, WeiboException
    {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL")
                + "statuses/repost_timeline.json",
        new PostParameter[] { new PostParameter("id", mid) }, paging);
    }
	public Response getCommentTimeline1(String mid,Paging paging)throws weibo4j.WeiboException, WeiboException
    {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL")
                + "comments/show.json",
        new PostParameter[] { new PostParameter("id", mid) }, paging);
    }

    public Response getSearchUser(String keyword, int count) throws WeiboException {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL")
                        + "search/suggestions/users.json",
                new PostParameter[] { new PostParameter("q", keyword),new PostParameter("count",count) });
    }
    public Response getTopStatus(String keyword, int count) throws WeiboException {
        return Weibo.client.get(
                WeiboConfig.getValue("baseURL")
                        + "search/topics.json",
                new PostParameter[] { new PostParameter("q", keyword),new PostParameter("count",count) });
    }

}