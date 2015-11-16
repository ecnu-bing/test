import Util.Tool;
import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 例：解析一条微博。
 */
public class GetWeiboData {
    static Tool tool = new Tool();
    static SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws WeiboException, weibo4j.model.WeiboException, Exception {
        /**
         * 读取微博文件
         */
        String path = "input/weibo_status_test.txt";
        path = "input/weibo_old_test_fromLiYe.txt";
        List<String> strList = tool.readFile(path, "utf-8");

        for (String line : strList) {
            List<Status> statusList = dealOneWeibo(line);

            doWithOneStatus(statusList);
        }



    }

    /**
     * 解析一条微博
     * @param line
     */
    public static List<Status> dealOneWeibo(String line) throws Exception{
        List<Status> statusList = new ArrayList<Status>();
        if (line.startsWith("{")) {
            StatusWapper wapper = Status.constructWapperStatus(line);
            if (wapper != null) {
                statusList = wapper.getStatuses();
            }
        }
        if (line.startsWith("[")) {
            statusList = Status.constructStatuses(line);
        }

        return statusList;


    }

    /**
     * 处理一条微博
     * @param statusList
     */
    public static void doWithOneStatus(List<Status> statusList) {
        //System.out.println(statusList.size());
        if (statusList == null)
            return;
        for (Status status : statusList) {
            System.out.println(status.getUser().getName()+"--"+inputFormat.format(status.getCreatedAt())+"--"+status.getText());
//            if (status.getUser() == null || status.getCreatedAt() == null) {
//                continue;
//            }
//            String time = inputFormat.format(status.getCreatedAt());
//            String text = status.getText();
//            String mid = status.getMid();
//            User user = status.getUser();
//            System.out.println("111:" + text);
//
//            Status rtStatus;
//            String rtText;
//            String rtMid;
//            User rtUser;
//            String rtTime;
//
//            if (status.getRetweetedStatus() != null) {
//                rtStatus = status.getRetweetedStatus();
//                rtText = rtStatus.getText();
//                rtMid = rtStatus.getMid();
//                rtUser = rtStatus.getUser();
//                rtTime = inputFormat.format(rtStatus.getCreatedAt());
//                if (rtStatus.getUser() == null || rtStatus.getCreatedAt() == null) {
//                    continue;
//                }
//            } else {
//                rtText = "";
//                rtMid = "";
//                rtUser = null;
//                rtTime = "";
//            }
//
//            System.out.println("222:" + rtText);
        }
    }

}
