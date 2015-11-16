package test;


import Util.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.model.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 通过用户的id或者domain，来获得用户info
 */
public class CrawlTest {
    Weibo weibo = null;

    /**
     * 微博Token
     */
    TokenManage tm = null;
    Token tokenpack = null;

    String dataStr = null;
    String outStr = null;
    String outDir = null;

    static Rootuid ioControl = null;
    boolean isok = true;
    Date start = null;
    static SimpleDateFormat inputFormat = new SimpleDateFormat(
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
     * @param input
     * @param output
     */
    public void SetIO(String input, String output) {
        this.dataStr = input;
        this.outStr = output;
        File temp_file = new File(this.outStr);
        this.outDir = temp_file.getParent();
        isok = true;
    }

    void init() {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        listSet = new HashSet<String>();

        try {
            Tool.refreshToken();
            tm = new TokenManage();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        start = new Date();
        System.out.println("Start Crawling at "
                + inputFormat.format(start.getTime()));
    }

    public void end() {
        System.out.println("All data get succeeded at "
                + inputFormat.format(new Date().getTime()));

        System.out.println("Using "
                + ((new Date()).getTime() - start.getTime()) / 60000
                + " minutes");
    }


    /**
     * 通过用户的某一个属性来获得当前用户的UserInfo
     * @param weibo
     * @param IdOrElse
     * @return
     * @throws weibo4j.model.WeiboException
     * @throws WeiboException
     */
    public String GetUserInfo(Weibo weibo, String IdOrElse) throws weibo4j.model.WeiboException, WeiboException {
        String ret = null;
        try {
            System.out.println(IdOrElse);
            //ret = weibo.showUser(uid).toString();

            String userIndo = userInfoRecognitionUtil.readUserInfo(IdOrElse);

            //通过user id来获得 user
//            User user = weibo.showUserByName(uid);
            //通过domain来获得user
            User user = weibo.showUserByDomain(userIndo);

            //ret = user.getName() + "\t" + user.getFollowersCount() + "\t"
            //	+ user.getFriendsCount() + "\t" + user.getStatusesCount()+"\t"+user.getFavouritesCount();
            ret = user.getId() + "|#|" + user.getFollowersCount() + "|#|" + user.getbiFollowersCount() + "|#|" + user.getStatusesCount() + "|#|" + user.getFriendsCount() + "|#|" + user.getGender() + "|#|" + user.getLocation();
            //System.out.println(ret);
        } catch (weibo4j.model.WeiboException e1) {
            Tool.write("D:\\RWork\\教育新浪微博\\error1", IdOrElse);
        }
        return ret;
    }

    /**
     * @throws weibo4j.model.WeiboException
     * @throws java.io.IOException
     * @throws WeiboException
     * @throws InterruptedException
     */
    public void MainCrawl() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br = null;
        String line = null;

        File file = new File(this.dataStr);
        //File file = new File("Z:\\code\\data\\report\\RetweetTree1\\part-00000");

        fis = new FileInputStream(file);
        isr = new InputStreamReader(fis, "gbk");
        br = new BufferedReader(isr);

        System.out.println("input: " + this.dataStr + "   output: " + this.outStr);
        /**
         * 开始爬取数据
         */
        while ((line = br.readLine()) != null) {
            tokenpack = tm.GetToken();

            if (tokenpack == null)
                continue;

            weibo = new Weibo();
            // System.out.println(tokenpack.token+'\t'+tokenpack.secret);
            weibo.setToken(tokenpack.token);


            /**
             * 获得某个用户的info
             */
            String info = GetUserInfo(weibo, line);
            if (info != null) {
                Tool.write(this.outStr, line + "|#|" + info);
            }

            /**
             * 睡眠时间
             */
            double a = Math.random() % 10 * 10000;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            System.out.println("sleep : " + randomNum / 1000 + "s");
            Thread.sleep(randomNum);
        }

    }

    public void run() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        if (!isok) {
            System.out.println("input and output file not set, use function SetIO to set");
            return;

        }
        init();

        MainCrawl();

        end();
    }

    public static void main(String[] args) throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        String input = "F:\\nankai\\政治\\error";
        input = "D:\\RWork\\教育新浪微博\\microblogId.txt";
        String output = "F:\\nankai\\政治\\userinfo.txt";
        output = "D:\\RWork\\教育新浪微博\\userinfo.txt";

        CrawlTest getuserThread = new CrawlTest();
        //getuserThread.setStartfrom(x); //skip from data before x
        getuserThread.SetIO(input, output);
        getuserThread.run();
    }

}
