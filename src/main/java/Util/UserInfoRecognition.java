package Util;

import org.junit.Test;

/**
 * 识别一些用户信息
 * Created by dingcheng on 2014/12/25.
 */
public class UserInfoRecognition {
    /**
     * 检测该部分信息是否是用户id
     * @param info
     * @return
     */
    public String isID(String info) {
        if(info.matches("[0-9]+")){
            return info;
        }else {
            return "";
        }
    }

    /**
     * 判断该部分信息是否为用户的 域名，如果是，取出用户domain。
     * @param info
     * @return
     */
    public String isDomain(String info) {
        if (info.matches("http://weibo.com/[0-9a-zA-Z]+")) {
            return info.replace("http://weibo.com/", "");
        }else if(info.matches("http://weibo.com/u/[0-9a-zA-Z]+")){
            return info.replace("http://weibo.com/u/", "");
        }else {
            return "";
        }
    }

    public String readUserInfo(String info) {
        if (!isDomain(info).equals("")) {
            return isDomain(info);
        }else if (!isID(info).equals("")) {
            return isID(info);
        }else {
            return info;
        }
    }

    @Test
    public void testOne() {
        String info = "http://weibo.com/chinalawedu";
        System.out.println(readUserInfo(info));

    }
}
