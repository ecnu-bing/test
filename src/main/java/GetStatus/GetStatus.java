package GetStatus;

import SN.WeiboCrawler;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 给定用户id，爬取
 */
public class GetStatus extends WeiboCrawler {
    protected String taskName = "GetStatus";

    int localIpcout;

    protected ArrayList<String> sinceidList = new ArrayList<String>();

    protected String outputDir = null;

    Date start = null;
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;

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
            outputDir = totalDir + File.separator + inputName + "_Status";
        } else {
            outputDir = args[1];
        }

        File testFile = new File(this.outputDir);
        if (!testFile.exists()) {
            testFile.mkdirs();
        }
        outputDir = testFile.getAbsolutePath();

        boolean flag = false;
    }



    /**
     * 通过用户的id，来批量获取用户的微博。
     */
    protected int getStatusByUid(Weibo weibo, myWriter writeStatus, String uid, int page) {
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

        while (true) {
            try {
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
                    if (i == 3) break;//如果爬取三次都是失败，就当做爬完了。
                    System.out.println("retry the " + i + "th times");
                    resetToken(1, uid);
                    /**
                     * 获得该 用户的 timeline
                     */
                    res = weibo.getUserTimelineByUidTest(uid, paging);
                    if (!res.toString().equals("[]") && res != null) {
                        //如果微博是可以解析的。
                        wapper = Status.constructWapperStatus(res.toString());
                        if (wapper == null) {
                            continue;
                        }
                        statusList = wapper.getStatuses();
                        nowStatusCount = wapper.getTotalNumber();
                    } else {
                        //不可以解析，返回0
                        return 0;
                    }
                    mysleep();
                }
                //将结果写回文件。如果是0，则不写入。
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
     * 批量爬取微博
     */
    protected void CrawlStatus() throws weibo4j.WeiboException {
        getStatusCount = 0;
        myWriter writeStatus = null;
        while (!readuid.IsOver()) {
            //每12小时更新一次token
            updateToken();

            String newuid = String.valueOf(readuid.GetStrictNewID());

            //recovery
            if (completeUidSet.contains(newuid)) {
                System.out.println(newuid + " has been crawled");
                readuid.idOK();
                continue;
            }
            nowStatusCount = 0;

            writeStatus = new myWriter(this.outputDir + File.separator + newuid + "_status", false);

            System.out.println("\n\nStart getting {" + newuid + "}");
            getStatusCount = 0;

            for (int page = 1; ; page++) {
                resetToken();

                // ------------------------------- 爬取一次 -----------------------
                int responsecount = 0;
                responsecount = getStatusByUid(weibo, writeStatus, newuid, page);
                // ----------------------------------------------------------------

                tm.AddIPCount(1, newuid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
                if ((responsecount <= 0) || getStatusCount == this.nowStatusCount) {
                    break;
                }
            }
            if (this.nowStatusCount > 2000) {
                Tool.write(missinguidPath, newuid);
            }
            writeStatus.closeWrite();

            Tool.write(completeuidPath, newuid);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }

    public void run() throws weibo4j.WeiboException, InterruptedException, IOException, WeiboException {
        super.run();
        CrawlStatus();
    }

    public static void main(String[] args) throws weibo4j.WeiboException,
            IOException, WeiboException, InterruptedException {
        GetStatus getStatus = new GetStatus();
        getStatus.init(args);
        getStatus.run();
    }
}
