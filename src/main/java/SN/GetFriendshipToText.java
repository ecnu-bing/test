package SN;

import Util.*;
import org.junit.Test;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.model.IDs;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 给定种子用户，爬取关注网络。
 */
public class GetFriendshipToText extends WeiboCrawler {

    protected String taskName = "GetFriendshipToText";
    protected String outPath = null;

    static Rootuid ioControl = null;
    boolean isok = true;
    Date start = null;

    static LogManager lMgr = LogManager.getLogManager();
    static String thisName = "WeiboLog";
    static Logger log = Logger.getLogger(thisName);
    static MyLogger mylogger = null;
    int startfrom = -1;

    /**
     * 每个用户之间的分隔符
     */
    static String _user_split_ = "\r\n";
    public static void set_user_split_(String _user_split_) {
        GetFriendshipToText._user_split_ = _user_split_;
    }

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
            outPath = totalDir + File.separator + inputName + "_result.txt";
        }else {
            outPath = args[1];
        }
        listSet = new HashSet<String>();

        boolean flag = false;
    }

    /**
     * 通过user id爬取user 的关注网络
     * @param weibo
     * @param uid
     */
    public String GetUserInfo(Weibo weibo, String uid) throws weibo4j.model.WeiboException, WeiboException, InterruptedException {
        IDs ids = null;
        String ret = "";
        String temp = "";
        while (true) {
            try {
                //ret = weibo.showUser(uid).toString();
                /**
                 * 获得该用户的关注用户id
                 */
                ids = weibo.getFriendsIDSByUserId(uid, 5000);
                for (long id : ids.getIDs()) {
                    ret += String.valueOf(id) + _user_split_;
                }
                temp = uid + _user_split_ + ret.substring(0, ret.lastIndexOf(_user_split_));
            } catch (weibo4j.model.WeiboException e1) {
                if (e1.getStatusCode() == 400) {
                    Tool.write(errorPath, uid);
                    break;
                } else if (e1.getStatusCode() == 400 || e1.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");

                    resetToken(localIpcout,uid);
                    continue;
                } else if (e1.getStatusCode() == 403) {
                    System.out.println("request too many times , sleep 5~45s");
                    mysleep();
                    resetToken(localIpcout, uid);
                    continue;
                } else {
                    mysleep();
                    continue;
                }
                //WriteFailLog();
                //e1.printStackTrace();
            }
            break;
        }
        mysleep();
        return temp;
    }

    public void MainCrawl() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException {
        myWriter writeStatus = null;
        writeStatus = new myWriter(outPath , false);
        while (!readuid.IsOver()) {
            //每12小时更新一次token
            updateToken();

            String line = String.valueOf(readuid.GetStrictNewID());
            String uid = line.split("\\|#\\|")[0];

            //recovery
            if (completeUidSet.contains(uid)) {
                System.out.println(line + " has been crawled");
                readuid.idOK();
                continue;
            }

            System.out.println("\n\nStart getting {" + uid + "}");
            resetToken();
            // ------------------------------- 爬取一次 -----------------------
            String res = GetUserInfo(weibo, uid);
            // ----------------------------------------------------------------
            tm.AddIPCount(1, line.toString());
            System.out.println("ipcount: " + TokenManage.getIpcount());

            if (res != null) {
                writeStatus.Write(res);
                Tool.write(completeuidPath, uid);
            }else {
                Tool.write(errorPath, uid);
            }
            readuid.idOK();
        }
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
        String input = "D:\\RWork\\教育新浪微博\\test\\教育_用户id_其他信息";
        String output = "D:\\RWork\\教育新浪微博\\test\\教育_关注网络";


        GetFriendshipToText getuserThread = new GetFriendshipToText();
        //getuserThread.setStartfrom(x); //skip from data before x
        getuserThread.init(args);
        getuserThread.run();
    }

    @Test
    public void getUidFile() {
        ImportUtil importUtil = new ImportUtil();
        String path = "D:\\RWork\\教育新浪微博\\test\\教育_uid_info";
        String temp[][] = importUtil.getMatrix(path, "\r\n", "\\|#\\|", "gbk");
        String uid[] = importUtil.getCol(temp, 0);
        for (int i = 0; i < uid.length; i++) {
            System.out.println(uid[i]);
        }
    }
}
