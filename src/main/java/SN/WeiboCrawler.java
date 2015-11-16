package SN;

import Util.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dingcheng on 2015/1/7.
 */
public abstract class WeiboCrawler {

    protected TokenManage tm = null;
    protected Token tokenpack = null;
    protected Weibo weibo = null;

    protected String totalDir = null;//本任务的路径（默认情况为./）
    protected String inputName = null;//输入文件（爬取任务名）

    protected String needGetUid = null;//爬取任务的绝对路径。
    protected String errorPath = null;//error保存路径。
    protected String completeuidPath = null;//已经完成的路径。
    protected String missinguidPath = null;//错过的任务路径。

    protected StringReaderOne readuid = null;//读任务列表工具类。
    protected Set<String> completeUidSet = null;//已经完成任务。
    protected Set<String> failedToken = new HashSet<String>();//失败的token列表。


    protected Date start = null;
    protected long startTime = 0l;
    protected int day = 1;
    protected SimpleDateFormat inputFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");


    protected void updateToken() {
        //每12小时更新一次token
        if (new Date().getTime() > startTime + day * 12 * 60 * 60 * 1000) {
            ++day;
            //从网络上更新token
            Tool.refreshToken();
            //更新代码使用token
            tm = new TokenManage();
        }
    }

    /*
    refresh Token: 每12小时刷新一次Token
     */
    protected void refreshToken() {
        try {
            Tool.refreshToken();
            tm = new TokenManage();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    protected void resetToken() {
        tokenpack = tm.GetToken();

        while (tokenpack == null) {
            tokenpack = tm.GetToken();
        }
        weibo = new Weibo();
        weibo.setToken(tokenpack.token);
        System.out.println("finish resetToken and weibo.setToken");
    }
    protected void resetToken(int a, String uid) {
        tm.ChekState();
        tokenpack = tm.GetToken();
        tm.AddIPCount(a, uid);
        while (tokenpack == null || failedToken.contains(tokenpack.token)) {
            System.out.println(tm.GetToken());
            tokenpack = tm.GetToken();
        }
        weibo.setToken(tokenpack.token);
    }

    protected static void mysleep() {
        try {
            double a = Math.random() * 5000;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            System.out.println("sleep : " + randomNum / 1000 + "s");
            Thread.sleep(randomNum);
        } catch (InterruptedException e1) {
            //e1.printStackTrace();
        }
    }

    protected void init(String args[]) {
        System.out.println("******************************\nThe task init()......");

        inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        start = new Date();
        System.out.println("Start Crawling at "
                + inputFormat.format(start.getTime()));
        startTime = new Date().getTime();

        //输入文件
        if (args.length < 1) {
            needGetUid = "." + File.separator + "data" + File.separator + "uid_task";
        } else {
            needGetUid = args[0];
        }

        //工作目录,任务名称
        totalDir = new File(needGetUid).getParent();//文件的目录
        System.out.println("totalDir: " + totalDir);
        inputName = new File(needGetUid).getName();//任务名称

        //结果以外的输出文件。
        completeuidPath = totalDir + File.separator + inputName + "_complete";
        System.out.println("completeuidPath: " + completeuidPath);
        missinguidPath = totalDir + File.separator + inputName + "_missing";
        System.out.println("missinguidPath: " + missinguidPath);
        errorPath = totalDir + File.separator + inputName + "_error";
        System.out.println("errorPath: " + errorPath);

//        //read sinceidList;
//        readuid = new StringReaderOne(needGetUid);
//        //read completeList;
//        completeUidSet = new HashSet<String>();
//        //getStatus
//        readCompleteUid();
        refreshToken();
    }


    protected void run() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException{
        //read sinceidList;
        readuid = new StringReaderOne(needGetUid);
        //read completeList;
        completeUidSet = new HashSet<String>();
        //getStatus
        readCompleteUid();
    }

    protected void end() {
        System.out.println("All data get succeeded at "
                + inputFormat.format(new Date().getTime()));

        System.out.println("Using "
                + ((new Date()).getTime() - start.getTime()) / 60000
                + " minutes");
    }


    protected void readCompleteUid() {
        File file = new File(completeuidPath);
        if (!file.exists()) {
            return;
        }
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "GBK");
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (!completeUidSet.contains(line)) {
                    completeUidSet.add(line);
                }
            }
            fis.close();
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
