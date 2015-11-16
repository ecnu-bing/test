package test;

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

public class UpdateStatusToNow {

    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String needGetUid = null;
    protected ArrayList<String> sinceidList = new ArrayList<String>();
    protected String completeuidPath = null;
    protected String outputDir = null;
    Weibo weibo = null;
    Date start = null;
    SimpleDateFormat inputFormat = null;
    static StringReaderOne readuid = null;
    Set<String> completeUidSet = null;
    Set<String> failedToken = new HashSet<String>();

    String record = null;

    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;
    int hasGetCount = 0;

    long startTime = 0l;
    int day = 1;
    List<String> dataList = new ArrayList<String>();

    public void GetSinceIdList() throws Exception {

    }

    /**
     * 初始化
     */
    protected void Init() {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = inputFormat.format((new Date()).getTime()).toString();
        startTime = new Date().getTime();
        System.out.println("start time : " + time);

        this.outputDir = "." + File.separator + "data" + File.separator + "status" + File.separator;
        File testFile = new File(this.outputDir);
        if (!testFile.exists()) {
            testFile.mkdir();
        }
        this.needGetUid = "." + File.separator + "data" + File.separator + "uid_status";
        this.completeuidPath = "." + File.separator + "data" + File.separator + "complete_status";
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

    /**
     * 运行爬数据程序
     */
    public void run() {
        Init();

        CrawlStatus();
    }

    /**
     * 读取Complete文件
     */
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

    /**
     * 如果是本季度第一次爬取微博，从sinceId开始，获取微博。
     *
     * @param weibo
     * @param writeStatus
     * @param uid
     * @param sinceId
     * @param page
     * @return
     */
    protected int getStatusBySinceId(Weibo weibo, myWriter writeStatus, String uid, String sinceId, int page) {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;

        //---------------------设置获取参数---------------------
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        if (sinceId.equals("0")) {
            return 0;
        }
        paging.setSinceId(Long.parseLong(sinceId));
        //---------------------设置获取参数---------------------
        System.out.println("page : " + page);


        while (true) {
            try {
                //res = weibo.getFriendsTimeline(uid, sinceId);
                //System.out.println("yes");


                res = weibo.getUserTimelineByUidTest(uid, paging);

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
                while ((this.nowStatusCount == 0 && statusList.size() == 0) || ((this.nowStatusCount - this.hisStatusCount) > 0 && (this.nowStatusCount - this.hisStatusCount) > this.getStatusCount && statusList.size() == 0)) {
                    ++i;
                    if (i == 3) {
                        break;
                    }
                    System.out.println("retry the " + i + "th times");
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                        tokenpack = tm.GetToken();
                    weibo.setToken(tokenpack.token);
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    if (!res.toString().equals("[]") && res != null) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        nowStatusCount = wapper.getTotalNumber();
                        System.out.println("nowStatusCount = "+nowStatusCount);
                    } else {
                        return 0;
                    }
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
                //将结果写回文件
                if (res.toString().length() > 0 && statusList.size() > 0) {
                    writeStatus.Write(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e) {
                if (e.getStatusCode() == 400 || e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                } else if (e.getStatusCode() == 403) {
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    try {
                        double a = Math.random() * 50000;
                        a = Math.ceil(a);
                        int randomNum = new Double(a).intValue();
                        System.out.println("sleep : " + randomNum / 1000 + "s");
                        Thread.sleep(randomNum);
                    } catch (InterruptedException e1) {
                        //e1.printStackTrace();
                    }


                }
                //e.printStackTrace();
            } catch (weibo4j.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            double a = Math.random() * 5000;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            System.out.println("sleep : " + randomNum / 1000 + "s");
            Thread.sleep(randomNum);
        } catch (InterruptedException e1) {
            //e1.printStackTrace();
        }
        if (statusList.size() > 0) {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);
        return ret;
    }

    /**
     * 通过MaxId来获得状态
     *
     * @param weibo
     * @param writeStatus
     * @param uid
     * @param maxId
     * @param page
     * @return
     */
    protected int getStatusByMaxId(Weibo weibo, myWriter writeStatus, String uid, String maxId, int page) {
        return 0;
    }

    /**
     * 如果本季度已经开始爬取微博，该次并非第一次。从SinceMaxId获得微博状态
     *
     * @param weibo
     * @param uid
     * @param sinceId
     * @param maxId
     * @param page
     * @return
     */
    protected int getStatusBySinceMaxId(Weibo weibo, String uid, String sinceId, String maxId, int page) {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;

        //-------------------设置爬取的参数--------------------
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        if (sinceId.equals("0")) {
            return 0;
        }
        paging.setSinceId(Long.parseLong(sinceId));
        paging.setMaxId(Long.parseLong(maxId));
        //-------------------设置爬取的参数--------------------
        System.out.println("page : " + page);


        while (true) {
            try {
                res = weibo.getUserTimelineByUidTest(uid, paging);
                try {
                    if (!res.toString().equals("[]") && res != null) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                    } else {
                        return 0;
                    }

                } catch (WeiboException e) {
                    e.printStackTrace();
                    break;
                }
                int i = 0;
                while (statusList.size() == 0 && nowStatusCount - hisStatusCount > getStatusCount + hasGetCount) {
                    ++i;
                    if (i == 3) {
                        break;
                    }
                    System.out.println("retry the " + i + "th times");
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                        tokenpack = tm.GetToken();
                    weibo.setToken(tokenpack.token);
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    if (!res.toString().equals("[]") && res != null) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                    } else {
                        return 0;
                    }
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
                //将结果暂时保存在dataList中
                if (res.toString().length() > 0 && statusList.size() > 0) {
                    dataList.add(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e) {
                if (e.getStatusCode() == 400 || e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                } else if (e.getStatusCode() == 403) {
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, uid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token)) {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    try {
                        double a = Math.random() * 50000;
                        a = Math.ceil(a);
                        int randomNum = new Double(a).intValue();
                        System.out.println("sleep : " + randomNum / 1000 + "s");
                        Thread.sleep(randomNum);
                    } catch (InterruptedException e1) {
                        //e1.printStackTrace();
                    }
                }
                //e.printStackTrace();
            } catch (weibo4j.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            double a = Math.random() * 5000;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            System.out.println("sleep : " + randomNum / 1000 + "s");
            Thread.sleep(randomNum);
        } catch (InterruptedException e1) {
            //e1.printStackTrace();
        }
        if (statusList.size() > 0) {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);
        return ret;
    }

    /**
     * 爬取微博
     */
    protected void CrawlStatus() {
        getStatusCount = 0;
        myWriter writeStatus = null;
        while (!readuid.IsOver()) {
            String newuid = null;
            String sinceId = null;
            String maxId = null;
            //计数器清空
            hasGetCount = 0;
            getStatusCount = 0;
            //每12小时更新一次token
            if (new Date().getTime() > startTime + day * 12 * 60 * 60 * 1000) {
                ++day;
                //从网络上更新token
                Tool.refreshToken();
                //更新代码使用token
                tm = new TokenManage();
            }

            //该条需要爬取的用户id和相关信息
            this.record = String.valueOf(readuid.GetStrictNewID());

            //recovery
            if (completeUidSet.contains(record)) {
                System.out.println(record + " has been crawled");
                readuid.idOK();
                continue;
            }
            String[] list = record.split("_");
            if (list.length < 4) {
                newuid = list[0];
                sinceId = list[1];
                hisStatusCount = Integer.parseInt(list[2]);
                nowStatusCount = 0;
            }

            //如果长度大于4：本次已经爬取了一部分数据，并进行了转移
            if (list.length > 4) {
                newuid = list[0];
                sinceId = list[1];
                maxId = list[2];
                hisStatusCount = Integer.parseInt(list[3]);
                nowStatusCount = Integer.parseInt(list[4]);
                hasGetCount = Integer.parseInt(list[5]);
            }
            if (sinceId.equals("0")) {
                System.out.println("sinceId error");
                readuid.idOK();
                continue;
            }

            //如果长度小于4：本次刚开始爬
            if (list.length < 4) {
                writeStatus = new myWriter(this.outputDir + newuid + "_" + sinceId + "_" + hisStatusCount + "_status", false);
            }

            //开始本次爬取数据
            System.out.println("Start getting " + newuid);

            for (int page = 1; ; page++) {
                tm.ChekState();
                tokenpack = tm.GetToken();

                while (tokenpack == null) {
                    tokenpack = tm.GetToken();
                }

                weibo = new Weibo();
                weibo.setToken(tokenpack.token);

                // -----------------------------------------------------------------
                int responsecount = 0;
                if (list.length == 3) {

                    responsecount = getStatusBySinceId(weibo, writeStatus, newuid, sinceId, page);
                } else {

                    responsecount = getStatusBySinceMaxId(weibo, newuid, sinceId, maxId, page);
                }
                // -----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
                if ((responsecount <= 30) || nowStatusCount - hisStatusCount <= getStatusCount + hasGetCount) {
                    break;
                }

            }
            if (list.length < 4) {
                writeStatus.closeWrite();
            } else {
                Tool.write(this.outputDir + newuid + "_" + sinceId + "_" + hisStatusCount + "_status.txt", dataList, true, "GBK");
                dataList.clear();
            }
            Tool.write(completeuidPath, record);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }

    public static void main(String[] arg) throws weibo4j.WeiboException,
            IOException {
        UpdateStatusToNow update = new UpdateStatusToNow();
        update.run();

    }


    static void moveFile(String source, String destination) {
        try {
            File file = new File(source);
            if (file.exists()) {
                InputStream fis;
                InputStreamReader fisr;
                BufferedReader br;
                try {
                    fis = new FileInputStream(file);
                    fisr = new InputStreamReader(fis);
                    br = new BufferedReader(fisr);
                    String rline = null;
                    while ((rline = br.readLine()) != null) {
                        Tool.write(destination, rline);
                    }
                    br.close();
                    fisr.close();
                    fis.close();
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("failed rm file ：" + source);
            e.printStackTrace();
        }
    }
}