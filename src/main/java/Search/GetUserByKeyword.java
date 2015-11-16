package Search;

import Util.*;
import weibo4j.Search;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 给定关键词，取得和这些关键词相关的用户id（前50个）。
 * Created by dingcheng on 2014/12/25.
 */
public class GetUserByKeyword {
    /**
     * 微博搜索工具类
     */
    static Search search = new Search();
    Weibo weibo = null;

    /**
     * 微博Token
     */
    TokenManage tm = null;
    Token tokenpack = null;

    String keyword = null;
    String outStr = null;
    String outDir = null;

    static Rootuid ioControl = null;
    boolean isok = true;
    Date start = null;
    static SimpleDateFormat queryFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * 检查一个user info是userId还是userDomain
     */
    static UserInfoRecognition userInfoRecognitionUtil = new UserInfoRecognition();
    static LogManager lMgr = LogManager.getLogManager();
    static String thisName = "WeiboLog";
    static Logger log = Logger.getLogger(thisName);
    static MyLogger mylogger = null;
    int startfrom = -1;

    public int getStartfrom() {
        return startfrom;
    }

    public void setStartfrom(int startfrom) {
        this.startfrom = startfrom;
    }

    static Set<String> listSet;

    int localIpcout;


    /**
     * 设置输入输出文件。
     *
     * @param query
     * @param output
     */
    public void SetIO(String query, String output) {
        this.keyword = query;

        this.outStr = output;
        File temp_file = new File(this.outStr);
        this.outDir = temp_file.getParent();

        isok = true;
    }

    void init() {
        queryFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        listSet = new HashSet<String>();

        try {
            Tool.refreshToken();
            tm = new TokenManage();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        start = new Date();
        System.out.println("Start Crawling at "
                + queryFormat.format(start.getTime()));
    }

    public void end() {
        System.out.println("All data get succeeded at "
                + queryFormat.format(new Date().getTime()));

        System.out.println("Using "
                + ((new Date()).getTime() - start.getTime()) / 60000
                + " minutes");
    }


    /**
     * 使用关键词进行搜索，获得用户id。并返回。
     * 通过用户的某一个属性来获得当前用户的UserInfo
     *
     * @param weibo
     * @return
     * @throws weibo4j.model.WeiboException
     * @throws weibo4j.WeiboException
     */
    public String[] GetSearchResult(Weibo weibo, String keyword) throws weibo4j.model.WeiboException {
        String ret[] = null;
        JSONArray res = search.searchSuggestionsUsers(keyword, 100);
        ret = new String[res.length()];

        System.out.println("************** size = " + res.length() + " **************");
        for (int i = 0; i < res.length(); i++) {

            System.out.println("\n\n****** one user ******");
            try {
                /**
                 * 获得第一用户的screen_name
                 */
                JSONObject tempObject = res.getJSONObject(i);
                System.out.println(tempObject.toString());
                String screen_name = tempObject.getString("screen_name");
                ret[i] = screen_name;

                //通过screen_name来获得user infomation
                weibo4j.model.User user = weibo.showUserByName(screen_name);
                String ans =user.getId()  + "|#|"+screen_name + "|#|"+ user.getFollowersCount() + "|#|" + user.getbiFollowersCount() + "|#|" + user.getStatusesCount() + "|#|" + user.getFriendsCount() + "|#|" + user.getGender() + "|#|" + user.getLocation();

                ret[i] = ans;
                /**
                 * 睡眠时间
                 */
                ThreadSleep();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (WeiboException e) {
                e.printStackTrace();
            }

        }
        return ret;
    }


    /**
     * @throws weibo4j.model.WeiboException
     * @throws java.io.IOException
     * @throws WeiboException
     * @throws InterruptedException
     */
    public void MainCrawl() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException {
        System.out.println("query: " + this.keyword + "   output: " + this.outStr);

        boolean flag = true;
        /**
         * 爬取
         */
        while (flag) {
            /**
             * 创建token
             */
            tokenpack = tm.GetToken();

            if (tokenpack == null)
                continue;
            /**
             * 创建微博工具类
             */
            weibo = new Weibo();
            // System.out.println(tokenpack.token+'\t'+tokenpack.secret);
            /**
             * 设置token
             */
            weibo.setToken(tokenpack.token);

            /**
             * 爬取一次数据。根据关键词爬取结果，并保存到文件。
             */
            String info[] = GetSearchResult(weibo, this.keyword);
            for (int i = 0; i < info.length; i++) {
                Tool.write(this.outStr, info[i]);
            }
            /**
             * 进程睡眠
             */
            ThreadSleep();
            flag = false;

        }

    }

    /**
     * 进程睡眠
     *
     * @throws InterruptedException
     */
    public void ThreadSleep() throws InterruptedException {
        /**
         * 睡眠时间
         */
        double a = Math.random() % 10 * 10000;
        a = Math.ceil(a);
        int randomNum = new Double(a).intValue();
        System.out.println("sleep : " + randomNum / 1000 + "s");
        Thread.sleep(randomNum);
    }

    public void run() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException {
        if (!isok) {
            System.out.println("query and output file not set, use function SetIO to set");
            return;

        }
        init();

        MainCrawl();

        end();
    }

    public static void main(String[] args) throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException {
        String query = "F:\\nankai\\政治\\error";
        query = "教育";
        query = args[0];
        String output = "F:\\nankai\\政治\\userinfo.txt";
        output = args[1];
        output = output + query;


        GetUserByKeyword getuserThread = new GetUserByKeyword();
        //getuserThread.setStartfrom(x); //skip from data before x
        getuserThread.SetIO(query, output);
        getuserThread.run();
    }

}
