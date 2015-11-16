package GetComments;

import Util.*;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 给定用户id，爬取
 */
public class MyGetStatus {
    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String needGetUid = null;
    protected ArrayList<String> sinceidList = new ArrayList<String>();

    protected String totalDir = null;
    protected String completeuidPath = null;
    protected String missinguidPath = null;
    protected String outputDir = null;
    Weibo weibo = null;
    Date start = null;
    SimpleDateFormat inputFormat = null;
    static StringReaderOne readuid = null;
    Set<String> completeUidSet = null;
    Set<String> failedToken = new HashSet<String>();
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;

    long startTime = 0l;
    int day = 1;

    public void GetStatus() throws Exception {

    }

    /**
     * 初始化
     * @param dir
     * @param input
     * @param output
     */
    protected void Init(String dir, String input, String output) {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = inputFormat.format((new Date()).getTime()).toString();
        startTime = new Date().getTime();
        System.out.println("start time : " + time);

        /**
         * 全局路径
         */
        this.totalDir = dir;

        this.outputDir = this.totalDir + File.separator + "data" + File.separator + "status" + File.separator;

        File testFile = new File(this.outputDir);
        if (!testFile.exists()) {
            testFile.mkdir();
        }
        this.needGetUid = this.totalDir + File.separator + "data" + File.separator + "uid_status";
        this.completeuidPath = this.totalDir + File.separator + "data" + File.separator + "complete_status";
        this.missinguidPath = this.totalDir + File.separator + "data" + File.separator + "missing_status";
        //read sinceidList;
        readuid = new StringReaderOne(needGetUid);
        //read completeList;
        completeUidSet = new HashSet<String>();
        //getStatus
        readCompleteUid();
        boolean flag = false;
        try {
            Tool.refreshToken();
            tm = new TokenManage();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    void readCompleteUid() {
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

    protected int getStatusByUid(Weibo weibo, myWriter writeStatus, String uid, int page) throws weibo4j.WeiboException {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        //paging.setMaxId(Long.parseLong(maxId));
        System.out.println("page : " + page);


        /**
         * 遍历每一个page
         */
        while (true) {
            try {
                //res = weibo.getUserTimelineByUidTest(uid);
                //System.out.println("yes");
                /**
                 * ?????????????????，遍历每一个page
                 */
                System.out.println("weibo.getUserBatch(uid)");

                res = weibo.getUserBatch(uid);


                //System.out.println(res.toString());
                //判断学校的网是否出问题了，待补充
                //
                //判断是否爬取正确格式的文件
                try {
                    if (!res.toString().equals("[]") && res != null) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        nowStatusCount = wapper.getTotalNumber();
                    } else {
                        return 0;
                    }

                } catch (WeiboException e) {
                    e.printStackTrace();
                    break;
                }
                int i = 0;
                while ((this.nowStatusCount == 0 && statusList.size() == 0) || (this.nowStatusCount >= this.getStatusCount && statusList.size() == 0)) {
                    ++i;
                    if (i == 3) {
                        break;
                    }


                    System.out.println("retry the " + i + "th times");
                    resetToken(1, uid);



                    /**
                     * 获得该 用户的 timeline
                     */
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    if (!res.toString().equals("[]") && res != null) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        nowStatusCount = wapper.getTotalNumber();
                    } else {
                        return 0;
                    }


                    mysleep();

                }
                //将结果写回文件
                if (res.toString().length() > 0 && statusList.size() > 0) {
                    writeStatus.Write(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e) {

                if (e.getStatusCode() == 400) {
                    break;
                } else if (e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);

                    resetToken(1, uid);

                } else if (e.getStatusCode() == 403) {

                    resetToken(1, uid);

                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    /**
                     * 睡眠
                     */
                    mysleep();


                }
                //e.printStackTrace();
            } catch (weibo4j.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * 睡眠
         */
        mysleep();

        if (statusList.size() > 0) {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);


        return ret;
    }

    protected void CrawlStatus() throws weibo4j.WeiboException {
        getStatusCount = 0;
        myWriter writeStatus = null;
        while (!readuid.IsOver()) {
            //每12小时更新一次token
            if (new Date().getTime() > startTime + day * 12 * 60 * 60 * 1000) {
                ++day;
                //从网络上更新token
                Tool.refreshToken();
                //更新代码使用token
                tm = new TokenManage();
            }
            String newuid = String.valueOf(readuid.GetStrictNewID());

            //recovery
            if (completeUidSet.contains(newuid)) {
                System.out.println(newuid + " has been crawled");
                readuid.idOK();
                continue;
            }
            nowStatusCount = 0;

            writeStatus = new myWriter(this.outputDir + newuid + "_status", false);

            System.out.println("Start getting " + newuid);
            getStatusCount = 0;
            for (int page = 1; ; page++) {


                resetToken(-1, "");
//                tm.ChekState();
//
//                tokenpack = tm.GetToken();
//
//                while (tokenpack == null) {
//                    tokenpack = tm.GetToken();
//                }
//                weibo = new Weibo();
//                weibo.setToken(tokenpack.token);

                // -----------------------------------------------------------------
                int responsecount = 0;
                responsecount = getStatusByUid(weibo, writeStatus, newuid, page);


                // -----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
                if ((responsecount <= 0) || getStatusCount == this.nowStatusCount) {
                    break;
                }


            }

            /**
             * 如果大于2000
             */
            if (this.nowStatusCount > 2000) {
                Tool.write(missinguidPath, newuid);
            }
            writeStatus.closeWrite();

            Tool.write(completeuidPath, newuid);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }

    /**
     * 重新设置token
     */
    protected void resetToken(int a, String uid) {
        if (a == 1) {
            tm.ChekState();
            tokenpack = tm.GetToken();
            tm.AddIPCount(a, uid);
            while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                System.out.println(tm.GetToken());
                tokenpack = tm.GetToken();
            }
            weibo.setToken(tokenpack.token);
        }else if (a == -1) {
            tokenpack = tm.GetToken();

            while (tokenpack == null) {
                tokenpack = tm.GetToken();
            }
            weibo = new Weibo();
            weibo.setToken(tokenpack.token);
        }
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
    public static void main(String[] args) throws weibo4j.WeiboException,
            IOException {
        MyGetStatus getStatus = new MyGetStatus();
        getStatus.Init(args[0], args[1], args[2]);
        getStatus.CrawlStatus();

    }

}
