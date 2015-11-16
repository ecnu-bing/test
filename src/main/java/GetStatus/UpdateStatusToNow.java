package GetStatus;

import DataClean.Dataclean_v2;
import GetFailed.UpdataMaxId;
import SN.WeiboCrawler;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;
import upload.Upload2Cluster;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateStatusToNow extends WeiboCrawler {
//    protected String taskName = "UpdateStatusToNow";

    protected ArrayList<String> sinceidList = new ArrayList<String>();
    protected String outputDir = null;
    protected String total_log = null;

    String record = null;

    long nowStatusCount = 0;//应爬到的微博数
    int hisStatusCount = 0;//某用户总共的微博数
    int getStatusCount = 0;
    int hasGetCount = 0;

    int day = 1;
    List<String> dataList = new ArrayList<String>();

    /*
    一个输入文件，一个输出路径
    */
    protected void init(String args[]) {
        /*
        输入，工作目录，load data: sinceId, completeId
         */
        super.init(args);//输入文件，工作目录等。

        /*
        输出路径
         */
        if (args.length < 2) {
            outputDir = totalDir + File.separator + "uid_task_Status";
        } else {
            outputDir = args[1];
        }

        File testFile = new File(this.outputDir);
        if (!testFile.exists()) {
            testFile.mkdirs();
        }
        outputDir = testFile.getAbsolutePath();
        total_log = totalDir + File.separator + "total.log";
    }

    /**
     * 运行爬数据程序
     */
    public void run() throws InterruptedException, WeiboException, weibo4j.WeiboException, IOException {
        super.run();
        CrawlStatus();
    }

    /**
     * 如果是本季度第一次爬取微博，从sinceId开始，获取微博。
     */
    protected int getStatusBySinceId(Weibo weibo, myWriter writeStatus, String uid, String sinceId, int page) {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper;
        int ret = 0;
        Response res;
        //---------------------设置获取参数---------------------
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        if (sinceId.equals("0")) {
            return 0;
        }
        paging.setSinceId(Long.parseLong(sinceId));
        //---------------------设置获取参数---------------------

        System.out.println("------page : " + page + " ------");
        while (true) {
            try {
                //res = weibo.getFriendsTimeline(uid, sinceId);
                //System.out.println("yes");

                //获得本次爬取微博数据的结果
                res = weibo.getUserTimelineByUidTest(uid, paging);

                //System.out.println(res.toString());
                //判断学校的网是否出问题了，待补充
                //
                //判断是否爬取正确格式的文件
                try {
                    //res!=null:    always true
                    if (!res.toString().equals("[]")) {
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
                //-----------------------看有没有拿到微博，[] list为空------------------------
                int i = 0;
                while ((this.nowStatusCount == 0 && statusList.size() == 0) || ((this.nowStatusCount - this.hisStatusCount) > 0 && (this.nowStatusCount - this.hisStatusCount) > this.getStatusCount && statusList.size() == 0)) {
                    ++i;
                    if (i == 3) {//如果尝试三次还是失败，则退出
                        break;
                    }
                    System.out.println("retry the " + i + "th times");
                    resetToken(1, uid);
                    /**
                     *
                     */
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    //res!=null:    always true
                    if (!res.toString().equals("[]")) {
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
                e.printStackTrace();
                if (e.getStatusCode() == 400) {
                    Tool.write(errorPath, uid);
                    break;
                } else if (e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    resetToken(1, uid);

                } else if (e.getStatusCode() == 403) {
                    resetToken(1, uid);
                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    mysleep();
                }
            } catch (weibo4j.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mysleep();
        if (statusList.size() > 0) {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);
        return ret;
    }

    /**
     * 如果本季度已经开始爬取微博，该次并非第一次。从SinceMaxId获得微博状态
     */
    protected int getStatusBySinceMaxId(Weibo weibo, String uid, String sinceId, String maxId, int page) {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper;
        int ret = 0;
        //StatusWapper res;
        Response res;
        Paging paging = new Paging();
        paging.setCount(100);
        paging.setPage(page);
        if (sinceId.equals("0")) {
            return 0;
        }
        paging.setSinceId(Long.parseLong(sinceId));
        paging.setMaxId(Long.parseLong(maxId));
        System.out.println("------page : " + page + " ------");
        while (true) {
            try {
                res = weibo.getUserTimelineByUidTest(uid, paging);
                try {
                    //res!=null:    always true
                    if (!res.toString().equals("[]")) {
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
                    resetToken(1, uid);
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    //res!=null:    always true
                    if (!res.toString().equals("[]")) {
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                    } else {
                        return 0;
                    }
                    mysleep();
                }
                //将结果暂时保存在dataList中
                if (res.toString().length() > 0 && statusList.size() > 0) {
                    dataList.add(res.toString());
                }
                break;
            }
            //判断微博返回错误
            catch (WeiboException e) {
                e.printStackTrace();
                if (e.getStatusCode() == 400) {
                    Tool.write(errorPath, uid);
                    break;
                } else if (e.getStatusCode() == 401) {
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    resetToken(1, uid);

                } else if (e.getStatusCode() == 403) {
                    resetToken(1, uid);
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
        mysleep();
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


        /**
         * 只转移一次数据
         */
        checkData();
//        cleanData();
//        uploadData();
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

            //每24小时转移一次数据
            if (new Date().getTime() > startTime + day * 24 * 60 * 60 * 1000) {
                checkData();
            }

            //每个24小时clean一次数据
            if (new Date().getTime() > startTime + day * 24 * 60 * 60 * 1000) {
                cleanData();
            }

            //每个24小时上传一次数据
            if (new Date().getTime() > startTime + day * 24 * 60 * 60 * 1000) {
                cleanData();
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
                //1000045284_3362737527421049_551
                newuid = list[0];
                sinceId = list[1];
                hisStatusCount = Integer.parseInt(list[2]);
                nowStatusCount = 0;
            }

            //如果长度大于4：本次已经爬取了一部分数据，并进行了转移
            if (list.length > 4) {
                //1012755275_3362701063932032_3651496786444404_5150_20838_1987
                newuid = list[0];
                sinceId = list[1];
                maxId = list[2];
                hisStatusCount = Integer.parseInt(list[3]);
                nowStatusCount = Integer.parseInt(list[4]);
                hasGetCount = Integer.parseInt(list[5]);
            }
            if(sinceId!=null)
                if (sinceId.equals("0")) {
                    System.out.println("sinceId error");
                    readuid.idOK();
                    continue;
                }

            //如果长度小于4：本次刚开始爬
            if (list.length < 4) {
                writeStatus = new myWriter(this.outputDir+File.separator + newuid + "_" + sinceId + "_" + hisStatusCount + "_status", false);
            }

            //开始本次爬取数据
            System.out.println("\n***************************************************\nStart getting " + newuid);

            for (int page = 1; ; page++) {
                resetToken();

                // -----------------------------------------------------------------
                int responsecount;

                //length==3 表示第一次爬该uid的数据
                if (list.length == 3) {
                    responsecount = getStatusBySinceId(weibo, writeStatus, newuid, sinceId, page);
                } else {
                    //之前未爬完该uid 的数据
                    responsecount = getStatusBySinceMaxId(weibo, newuid, sinceId, maxId, page);
                }
                // -----------------------------------------------------------------

                if(newuid!=null)
                    tm.AddIPCount(1, newuid);
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
                if ((responsecount <= 30) || nowStatusCount - hisStatusCount <= getStatusCount + hasGetCount) {
                    break;
                }

            }
            if (list.length < 4) {
                if(writeStatus!=null)
                    writeStatus.closeWrite();
            } else {
                Tool.write(this.outputDir + File.separator + newuid + "_" + sinceId + "_" + hisStatusCount + "_status.txt", dataList, true, "GBK");
                dataList.clear();
            }
            Tool.write(completeuidPath, record);
            readuid.idOK();
        }
        System.out.println("=========completed==============");

        /**
        转移数据
         */
        moveData();
        /**
        清理并合并数据
         */
        cleanData();
        /**
         * 上传数据
         */
        uploadData();

    }

    /**
     * 转移数据；生成FailedStatus和MaxId。
     */
    public void moveData() {
        System.out.println("\n\n************************* Move Data *****************************");
        Tool.write(total_log, "每一次爬去循环结束后转移一次数据\r\n", true, "utf-8");
        UpdataMaxId updataMaxId = new UpdataMaxId(totalDir);
        updataMaxId.MoveData();
        /*
        删除complete_status和uid_status，并将MaxId更名为uid_status
         */
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);
        int hour  = c.get(Calendar.YEAR);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        String deleteDir = totalDir + File.separator + "delete" + File.separator + month + "_" + day+ "_" + hour+ "_" + minute+ "_" + second;
        File deleteDirFile = new File(deleteDir);
        if (!deleteDirFile.exists()) {
            deleteDirFile.mkdirs();
        }
        File oldFile = new File(needGetUid);
        File fnew = new File(deleteDir + File.separator + oldFile.getName());
        oldFile.renameTo(fnew);
        oldFile = new File(completeuidPath);
        fnew = new File(deleteDir + File.separator + oldFile.getName());
        oldFile.renameTo(fnew);
        oldFile = new File(totalDir + File.separator + "uid_Maxid_status");
        fnew = new File(needGetUid);
        oldFile.renameTo(fnew);
    }

    /**
     * 定期check一下data，将爬号的数据放入cleaned_status，更新maxid，
     */
    public void checkData() {
        System.out.println("\n\n************************* Check Data *****************************");
        Tool.write(total_log, "每24小时转移一次数据\r\n", true, "utf-8");
        UpdataMaxId updataMaxId = new UpdataMaxId(totalDir);
        updataMaxId.TemporalMoveData();
    }

    /**
     * 定期clean一次数据，准备好数据上传。
     */
    public void cleanData() {
        System.out.println("************************* Clean Data *****************************");
        Tool.write(total_log, "每一段时间 清理 一次数据，merge\r\n", true, "utf-8");
        Dataclean_v2 clean = new Dataclean_v2(totalDir);
        clean.DataClean();
    }

    /**
     * 定期upload一次数据。
     */
    public void uploadData() {
        System.out.println("************************* upload Data *****************************");
        Tool.write("./helloword.log", "每一段时间 上传 一次数据，merge\r\n", true, "utf-8");
        Upload2Cluster upload = new Upload2Cluster(totalDir);
        upload.put2remote();
    }

    public static void main(String[] args) throws weibo4j.WeiboException,
            IOException, WeiboException, InterruptedException {
        UpdateStatusToNow update = new UpdateStatusToNow();
        update.init(args);
        update.run();
    }

}