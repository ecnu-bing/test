package GetComments;

import Util.*;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 给定用户id，爬取
 */
public class MyGetCommentsTest {
    TokenManage tm = null;
    Token tokenpack = null;
    int localIpcout;
    protected String needGetUid = null;
    protected ArrayList<String> sinceidList = new ArrayList<String>();

    protected String totalDir = null;
    protected String completeuidPath = null;
    protected String missinguidPath = null;

    /*
    没有评论的微博
     */
    protected String nocommentPath = null;

    /*
    输出数据的目录
     */
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

    /*
    过滤评论
     */
    String commentFilter[] = new String[]{
            "转发微博"
    };


    /**
     * 初始化
     * @param dir
     */
    protected void Init(String dir) {
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
        /*
        需要爬取评论的微博
         */
        this.needGetUid = this.totalDir + File.separator + "data" + File.separator + "sid_status";
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
        MyFile myFile = new MyFile(completeuidPath, "gbk");
        if (!myFile.isOpen) {
            return;
        }
        String line = null;
        try {
            line = myFile.readLine();
            while (line != null) {
                if (!completeUidSet.contains(line)) {
                    completeUidSet.add(line);
                }
                line = myFile.readLine();
            }
            myFile.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据一条微博的id， 爬取所有的微博评论
     * @param weibo 微博接口
     * @param writeStatus
     * @param sid 一条微博的 id
     * @param page 爬取评论集合的第 page 页
     * @return
     * @throws weibo4j.WeiboException
     */
    protected int getCommentsBySid(Weibo weibo, myWriter writeStatus, String sid, int page) throws weibo4j.WeiboException {
        System.out.println("\n\n通过微博id:{" + sid + "}来获得评论信息");

        //结果集
        List<Comment> commentsList = new ArrayList<Comment>();
        CommentWapper wapper = null;

        int ret = 0;
        Response res = null;
        Paging paging = new Paging();
        /*
        单页返回的数量
         */
        paging.setCount(50);
        paging.setPage(page);
        //paging.setMaxId(Long.parseLong(maxId));
        System.out.println("page : " + page);

        /**
         * 遍历每一个page
         */
        while (true) {
            try {
                /**
                 * 获得微博评论
                 */
                res = weibo.getCommentTimeline1(sid, paging);

                //判断学校的网是否出问题了，待补充
                //

                /**
                 * 判断是否爬取正确格式的文件
                 */
                try {
                    if (!res.toString().equals("[]")&&!res.toString().equals("{}") && res != null) {
                        wapper = Comment.constructWapperComments(res);
                        if (wapper == null) {
                            continue;
                        }
                        commentsList = wapper.getComments();
                        nowStatusCount = wapper.getTotalNumber();
                    } else {
                        return 0;
                    }
                } catch (WeiboException e) {
                    e.printStackTrace();
                    break;
                }

                /**
                 * 重新爬取三次，如果都没有爬到，就当做没有评论。
                 */
                int i = 0;
                while ((this.nowStatusCount == 0 && commentsList.size() == 0) || (this.nowStatusCount >= this.getStatusCount && commentsList.size() == 0)) {
                    ++i;
                    if (i == 3) {
                        break;
                    }
                    System.out.println("retry the " + i + "th times");
                    resetToken(1, sid);

                    /**
                     * 获得该 用户的 timeline
                     */
                    res = weibo.getCommentTimeline1(sid, paging);
                    System.out.println("爬到了：" + res.toString());
                    if (!res.toString().equals("[]")&&!res.toString().equals("{}") && res != null) {
                        wapper = Comment.constructWapperComments(res);
                        if (wapper == null) {
                            continue;
                        }
                        commentsList = wapper.getComments();
                        nowStatusCount = wapper.getTotalNumber();
                    } else {
                        return 0;
                    }

                    mysleep();
                }
                //将结果写回文件
                if (res.toString().length() > 0 && commentsList.size() > 0) {
                    System.out.println("成功将爬取内容写入文件！");
                    boolean flag = true;
                    for (Comment each : commentsList) {
                        for (String word : commentFilter) {
                            if (word.equals(each.getText())) {
                                flag = false;
                            }
                        }
                        if(flag)
                            System.out.println("*****:" + each.getUser().getName() + " 评论了: " + each.getText());
                        flag = true;
                    }
                    writeStatus.Write(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e) {
                e.printStackTrace();

                if (e.getStatusCode() == 400) {
                    break;
                } else if (e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);

                    resetToken(1, sid);

                } else if (e.getStatusCode() == 403) {

                    resetToken(1, sid);

                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());

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

        if (commentsList.size() > 0) {
            ret = commentsList.size();
            System.out.println("Succeded");
        }
        commentsList.clear();
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

            //recovery:已经爬取的数据
            if (completeUidSet.contains(newuid)) {
                System.out.println(newuid + " has been crawled");
                readuid.idOK();
                continue;
            }
            nowStatusCount = 0;

            writeStatus = new myWriter(this.outputDir + newuid + "_comments", false);

            System.out.println("Start getting " + newuid);
            getStatusCount = 0;
            /**
             * 开始爬取
             */
            for (int page = 1; ; page++) {
                resetToken(-1, "");

                // -----------------------------------------------------------------
                int responsecount = 0;
                responsecount = getCommentsBySid(weibo, writeStatus, newuid, page);
                // -----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;

                System.out.println("************* responsecount:" + responsecount + "  getStatusCount:" + getStatusCount + "  nowStatusCount:" + nowStatusCount);
                /*
                如果爬取完成，则结束。
                 */
                if ((responsecount <= 0) || getStatusCount == this.nowStatusCount) {
                    break;
                }
            }

            /**
             * 如果大于2000，表示该用户的微博爬取有断开的部分。
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

    /**
     * 批量获得微博的评论
     * @param args
     * @throws weibo4j.WeiboException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws weibo4j.WeiboException,
            IOException {
        MyGetCommentsTest getStatus = new MyGetCommentsTest();
        getStatus.Init(args[0]);
        getStatus.CrawlStatus();
    }

    public void getCommentsFromOneStatus(String status) {

    }

}
