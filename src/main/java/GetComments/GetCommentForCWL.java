package GetComments;

import Util.FileUtil;
import Util.MyFile;
import Util.XmlUtil;
import domain.WeiboComments;
import domain.WeiboEvent;
import org.dom4j.Element;
import weibo4j.WeiboException;
import weibo4j.http.Response;
import weibo4j.model.Comment;
import weibo4j.model.CommentWapper;
import weibo4j.model.Status;
import weibo4j.org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 例：解析一条微博。
 */
public class GetCommentForCWL {
    static FileUtil fileUtil = new FileUtil();

    static SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 输出文件路径。
     */
    static String outDir = null;
    static String outPath = null;


    public static void run(String dirName, String info) throws Exception {
        /**
         * 读取微博文件
         */
        String dir = dirName;

        /**
         * 将此文件夹中所有的文件（微博和评论），变成XML格式
         */
        dealOneDir2XML(dir,info);
    }

    public static void dealOneDir2XML(String dir, String info) throws Exception {
        Element root = XmlUtil.createElement("events");

        String eventName = "";
        String eventKeywords = "";
        List<WeiboComments> wCommentList = new ArrayList<WeiboComments>();


        File dirFile = new File(dir);
        outDir = dirFile.getParent();
        outPath = outDir + File.separator + info + ".xml";

        /*
        从文件夹名获取信息
         */
        String templist[] = info.split("###");
        eventName = templist[0];
        for (int i = 1; i < templist.length - 1; i++) {
            eventKeywords += templist[i]+"_";
        }
        eventKeywords = eventKeywords.substring(0, eventKeywords.length() - "_".length());
        /*
        每一个文件对应一个微博和评论。
         */
        for (File f : dirFile.listFiles()) {
            System.out.println("dealOneFile2Element: " + f.getAbsolutePath());
            WeiboComments one_weibo_comments = dealOneFile2Elem(f.getAbsolutePath());
            wCommentList.add(one_weibo_comments);
        }
        root.add(new WeiboEvent(eventName, eventKeywords, wCommentList).createElement());

        XmlUtil.writeXMLDom(root, outPath, "gbk");
    }


    public static String dealOneFile(File f) throws WeiboException, weibo4j.model.WeiboException {
        String res = "";

        /*
        原微博的id
         */
        String sid = f.getName().split("_")[0];

        res += "|#|" + sid + "\r\n";

        List<String> strList = fileUtil.read2Arr(f, "gbk");
        for (String line : strList) {
            List<Comment> commentList = dealOneComment(line);
            for (Comment each : commentList) {
                System.out.println(each.getUser().getName() + " 评论了 " + each.getText());
                res += "|$|" + each.getText() + "\r\n";
            }
        }
        res = res.substring(0, res.length() - "\r\n".length());
        fileUtil.write(outDir + File.separator + sid + "_comments_cwl.txt", res, false, "gbk");

        return "";
    }


    /**
     * 第一行，为微博数据，格式为json
     * 第二行开始，为评论数据，格式为json
     */
    public static WeiboComments dealOneFile2Elem(String path) throws WeiboException, weibo4j.model.WeiboException, IOException, JSONException {

        /*
        原微博的id
         */
        MyFile myFile = new MyFile(path, "gbk");
        Status originalStatus = null;
        String id = "";
        String text = "";

        /**
         * 第一行微博解析
         */
        originalStatus = new Status(myFile.readLine());

        id = originalStatus.getMid();
        text = originalStatus.getText();


        /**
         * 第二行开始，评论
         */
        List<String> commentsList = new ArrayList<String>();
        String line = myFile.readLine();
        while (line != null) {
            List<Comment> temp_list = dealOneComment(line);
            for (Comment one_comment : temp_list) {
                commentsList.add(one_comment.getText());
            }
            line = myFile.readLine();
        }
        WeiboComments res = new WeiboComments(id, text, commentsList);

        return res;
    }


    /**
     * 一行评论数据（json），转换成Comment。
     */
    public static List<Comment> dealOneComment(String line) throws weibo4j.model.WeiboException, WeiboException {
        List<Comment> commentList = new ArrayList<Comment>();
        try {
            if (line.startsWith("{")) {
                CommentWapper wapper = Comment.constructWapperComments(new Response(line));
                if (wapper != null) {
                    commentList = wapper.getComments();
                }
            }
            if (line.startsWith("[")) {
                commentList = Comment.getComments(new Response(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
            commentList = new ArrayList<Comment>();
        }
        return commentList;
    }


    public static void main(String[] args) throws WeiboException, weibo4j.model.WeiboException, Exception {
        if (args.length < 1) {
            System.out.println("Convert Stauts and Comments to XML format\nUsage: ***.jar inputDir");
        }
        /**
         * 读取微博文件
         */
        String dir = "input/weibo_status_test.txt";
        dir = args[0];
        String info = "";
        info = args[1];

        /**
         * 将此文件夹中所有的文件（微博和评论），变成XML格式
         */
        dealOneDir2XML(dir,info);

    }
}
