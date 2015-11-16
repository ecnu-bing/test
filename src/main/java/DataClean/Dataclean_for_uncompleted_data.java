package DataClean;

import Util.MyFile;
import Util.Tool;
import org.junit.Test;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * 在指定某用户的微博中个，去重复。
 */
public class Dataclean_for_uncompleted_data {

    private String input_encode = "gbk";
    private String output_encode = "utf-8";
    private int isDelte = 0;//不删除原来数据
    private String log_path = "./complete.log";

    String totalDir;
    String outputPath;


    public Dataclean_for_uncompleted_data() {

    }

    private void run() {
        dataclean();
    }

    private boolean init(String args[]) {
        totalDir = args[0];
        outputPath = args[1];
        return true;
    }

    private void dataclean(){
        File f = new File(totalDir);
        /* 解析每个微博文件 */
        for (File fileLevel1 : f.listFiles()) {
            if (fileLevel1.getName().startsWith("status_")) {

                dealEveryWeiboSet(new File(fileLevel1.getAbsolutePath() + File.separator + "data" + File.separator + "status"));
                if (isDelte == 1) {
                    Tool.deleteFile(fileLevel1);
                }
            }
        }
    }
    /**
     * 将每个存放微博的文件merge到一个文件。
     */
    public void dealEveryWeiboSet(File f) {
        List<String> writeList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        /* 解析每个微博文件 */
        for (File fileLevel1 : f.listFiles()) {

            if (fileLevel1.getName().endsWith("_status.txt")) {

                MyFile myFile = new MyFile(fileLevel1.getAbsolutePath(), input_encode);
                String rline;
                System.out.println("clean: " + fileLevel1.getName());
                try {
                    while ((rline = myFile.readLine()) != null) {

                        try {
                            StatusWapper wapper = Status.constructWapperStatus(rline);

                        } catch (weibo4j.model.WeiboException e) {
                            continue;
                        } catch (weibo4j.WeiboException e) {
                            continue;
                        } catch (Exception e) {
                            continue;
                        }
                        String id = rline.substring(rline.indexOf("mid") + 3, rline.indexOf("idstr") - 3);
                        if (!writeList.contains(rline) && !idList.contains(id)) {
                            idList.add(id);
                            writeList.add(rline);
                        }
                    }
                } catch (IOException eio) {

                }
                myFile.close();
                if (writeList != null) {
                    if (Tool.write(outputPath, writeList, true, output_encode)) {
                        if (isDelte == 1) {
                            Tool.deleteFile(fileLevel1);
                        }
                    }
                }
                writeList.clear();
                idList.clear();

                if (isDelte == 1) {
                    Tool.deleteFile(fileLevel1);
                }
            }
        }

    }

    @Test
    public void testOne() {
        String path = "D:\\data\\weibo\\test\\zhutao2\\data\\asdf.txt";
        File f = new File(path);
        Tool.deleteFile(f);
    }


    public static void main(String[] args) {
        Dataclean_for_uncompleted_data utog = new Dataclean_for_uncompleted_data();
//        args = new String[]{
//            "H:\\weibo\\task",
//            "H:\\weibo\\hello_out",
//        };
        boolean flag = utog.init(args);
        if (!flag) {
            return;
        }
        utog.run();
    }
}
