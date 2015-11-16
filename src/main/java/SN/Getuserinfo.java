package SN;

import Util.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 通过用户一部分信息（id或者domain，得到用户所有信息）
 */
public class Getuserinfo extends WeiboCrawler{

    protected String taskName = "GetUserInfo";
    protected String outPath = null;


    static Rootuid ioControl = null;
    boolean isok = true;
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

    protected void init(String args[]) {
        /*
        输入，工作目录，load data: sinceId, completeId
         */
        super.init(args);

        /*
        输出路径
         */
        if (args.length < 2) {
            outPath = totalDir + File.separator + inputName + "_result";
        }else {
            outPath = args[1];
        }
        boolean flag = false;
    }


    /**
     * 通过用户的某一个属性来获得当前用户的UserInfo
     */
    public String GetUserInfo(Weibo weibo, String info) {
        String res = null;
        //通过user id来获得user
        System.out.println("user info: " + info);
        try {
//            User user = weibo.showUserById(info);
            User user = weibo.showUserByName(info);
            res = user.getId() + "|#|" + user.getScreenName() + "|#|" + user.getName() + "|#|" + user.getFollowersCount() + "|#|" + user.getbiFollowersCount() + "|#|" + user.getStatusesCount() + "|#|" + user.getFriendsCount() + "|#|" + user.getGender() + "|#|" + user.getLocation();
        } catch (weibo4j.model.WeiboException e) {
            if (e.getStatusCode() == 400) {
                Tool.write(errorPath, info);
            }
            e.printStackTrace();
        } catch (WeiboException e) {
            e.printStackTrace();
        }
        mysleep();
        return res;
    }
    /**
     * 通过每一行一个用户的id，爬取所有用户的具体信息。
     */
    public void MainCrawl() throws IOException, WeiboException, InterruptedException {
        myWriter writeStatus = null;
        writeStatus = new myWriter(outPath , false);

        while (!readuid.IsOver()) {
            //每12小时更新一次token
            updateToken();

            String line = String.valueOf(readuid.GetStrictNewID());

            //recovery
            if (completeUidSet.contains(line)) {
                System.out.println(line + " has been crawled");
                readuid.idOK();
                continue;
            }

            System.out.println("\n\nStart getting {" + line + "}");
            resetToken();

            // ------------------------------- 爬取一次 -----------------------
            String res = GetUserInfo(weibo, line);
            // ----------------------------------------------------------------
            tm.AddIPCount(1, line.toString());
            System.out.println("ipcount: " + TokenManage.getIpcount());

            if (res != null) {
                writeStatus.Write(line + "|#|" + res);
                Tool.write(completeuidPath, line);
            }
            readuid.idOK();
        }
        writeStatus.closeWrite();
        System.out.println("=========completed==============");
    }

    public void run() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        super.run();
        if (!isok) {
            System.out.println("input and output file not set, use function SetIO to set");
            return;
        }
        MainCrawl();
        end();
    }

    public static void main(String[] args) throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        String input = "F:\\nankai\\政治\\error";
        input = "D:\\RWork\\教育新浪微博\\爬取微博用户信息\\user_screenName";
//        input = args[0];
        String output = "F:\\nankai\\政治\\userinfo.txt";
        output = "D:\\RWork\\教育新浪微博\\爬取微博用户信息\\user_id";
//        output = args[1];

        Getuserinfo getuserThread = new Getuserinfo();
        //getuserThread.setStartfrom(x); //skip from data before x
        args = new String[]{input, output};

        getuserThread.init(args);
        getuserThread.run();
    }
}
