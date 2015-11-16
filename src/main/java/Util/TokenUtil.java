package Util;

import weibo4j.Weibo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dingcheng on 2015/1/7.
 */
public class TokenUtil {
    public TokenManage tm = null;
    public Token tokenpack = null;
    Set<String> failedToken = new HashSet<String>();

    private void resetToken(Weibo weibo, int a, String uid) {

        if (a == 1) {
            tm.ChekState();
            tokenpack = tm.GetToken();
            tm.AddIPCount(a, uid);
            while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                System.out.println(tm.GetToken());
                tokenpack = tm.GetToken();
            }
            weibo.setToken(tokenpack.token);
        } else if (a == -1) {
            tokenpack = tm.GetToken();

            while (tokenpack == null) {
                tokenpack = tm.GetToken();
            }
            weibo = new Weibo();
            weibo.setToken(tokenpack.token);
        }
    }
}
