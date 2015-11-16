package GetComments;

import SN.WeiboCrawler;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;
import weibo4j.Weibo;
import weibo4j.http.Response;
import weibo4j.model.*;
import weibo4j.org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 给定用户id，爬取
 */
public class getstatuscomments extends WeiboCrawler {
    protected ArrayList<String> sinceidList = new ArrayList<String>();

    /*
    没有评论的微博
     */
    protected String nocommentPath = null;

    /*
    输出数据的目录
     */
    protected String outputDir = null;
    protected String eventName = null;
    protected String keywords = null;

    Date start = null;

    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;

    /*
    一个输入文件，一个输出路径，载入数据
     */
    protected void init(String args[]) {
        /*
        输入，工作目录，load data: sinceId, completeId
         */
        super.init(args);

        /*
        输出路径
         */
        if (args.length < 2) {
            outputDir = this.totalDir + File.separator + inputName + "_comments" + File.separator + "data" + File.separator;
        } else {
            outputDir = args[1] + File.separator+ inputName + "_comments" + File.separator + "data" + File.separator;
        }
        File testFile = new File(outputDir);
        if (!testFile.exists()) {
            testFile.mkdirs();
        }
        outputDir = testFile.getAbsolutePath();

        //结果以外的输出文件。
        completeuidPath = args[1] + File.separator+ inputName + "_comments" + File.separator  + inputName + "_complete";
        System.out.println("******************" + completeuidPath);
        missinguidPath = args[1] + File.separator+ inputName + "_comments" + File.separator +  inputName + "_missing";
        errorPath = args[1] + File.separator+ inputName + "_comments" + File.separator +  inputName + "_error";

    }

    /**
     * 根据一条微博的id， 爬取所有的微博评论.
     * @param weibo 微博接口
     * @param writeStatus
     * @param info 一条微博的某种信息
     * @param page 爬取评论集合的第 page 页
     */
    protected int getCommentsBySid(Weibo weibo, myWriter writeStatus, String info, int page) throws weibo4j.WeiboException, WeiboException {
        System.out.println(info);
        /*
        将微博信息写入文件
         */
//        writeStatus.Write(info);
        String sid = info;

        System.out.println("通过微博id:{" + sid + "}来获得评论信息");

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
                res = weibo.getCommentTimeline1(sid, paging);//获得微博评论

                //判断学校的网是否出问题了，待补充
                //

                //判断是否爬取正确格式的文件
                try {
                    if (!res.toString().equals("[]") && !res.toString().equals("{}") && res != null) {
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

                    // 获得该 用户的 timeline
                    res = weibo.getCommentTimeline1(sid, paging);
                    System.out.println("爬到了：" + res.toString());
                    if (!res.toString().equals("[]") && !res.toString().equals("{}") && res != null) {
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
        mysleep();

        if (commentsList.size() > 0) {
            ret = commentsList.size();
            System.out.println("Succeded");
        }
        commentsList.clear();
        System.out.println("ret : " + ret);
        return ret;
    }



    protected void CrawlStatus() throws weibo4j.WeiboException, WeiboException {
        getStatusCount = 0;
        myWriter writeStatus = null;
        while (!readuid.IsOver()) {
            //每12小时更新一次token
            updateToken();

            String line = String.valueOf(readuid.GetStrictNewID());

            //recovery:已经爬取的数据
            if (completeUidSet.contains(line)) {
                System.out.println(line + " has been crawled");
                readuid.idOK();
                continue;
            }

            nowStatusCount = 0;

            /**
             * 获得目前爬取的一条微博
             */
            Status a_status = null;
            try {
                a_status = new Status(line);
            } catch (JSONException e) {
                e.printStackTrace();
                Tool.write(errorPath, line);
                readuid.idOK();
                continue;
            }
            String sid = a_status.getMid();

            /*
            结果写出的路径。
             */
            writeStatus = new myWriter(this.outputDir + File.separator + sid + "_comments", false);
            writeStatus.Write(line);

            System.out.println("\n\nStart getting " + line);
            getStatusCount = 0;


            /**
             * 开始爬取
             */
            for (int page = 1; ; page++) {
                resetToken();

                // -----------------------------------------------------------------
                int responsecount = 0;
                responsecount = getCommentsBySid(weibo, writeStatus, sid, page);
                // -----------------------------------------------------------------

                tm.AddIPCount(1, line.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());

                getStatusCount += responsecount;
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
                Tool.write(missinguidPath, line);
            }
            writeStatus.closeWrite();

            Tool.write(completeuidPath, line);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }

    protected void run(String args[]) throws weibo4j.model.WeiboException, IOException, weibo4j.WeiboException, InterruptedException, JSONException {
        super.run();
        CrawlStatus();
        end();
    }
    /**
     * 批量获得微博的评论
     */
    public static void main(String[] args) throws weibo4j.WeiboException,
            IOException, JSONException, WeiboException, InterruptedException {
        getstatuscomments getStatus = new getstatuscomments();
        getStatus.init(args);

//        getStatus.Init(args[0], args[1]);
        getStatus.run(args);
    }
}
