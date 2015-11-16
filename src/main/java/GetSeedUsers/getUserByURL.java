package GetSeedUsers;

import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.model.User;

/**
 * Created by dingcheng on 2014/12/23.
 */
public class getUserByURL {
    static Users UsersUtil = new Users();
    public static void main(String args[]) throws Exception {

        Weibo weiboUtil = new Weibo();
        

        String url = "http://weibo.com/zhonghejiaoyu";
        String UserId = "2466033285";
        User user = UsersUtil.showUserById(UserId);

        System.out.println(user.getName());
    }
}
