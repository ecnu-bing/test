package DataClean;

import Util.MyFile;
import Util.Tool;
import org.junit.Test;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 在指定某用户的微博中个，去重复。
 */
public class Dataclean_v1 {

    private String input_encode = "gbk";
    private String output_encode = "utf-8";
    private int isDelte = 0;//不删除原来数据


    //private String inputDir = "C:\\Users\\SEI-IMC\\Desktop\\";
    private String toDir = null;
    private String fromDir = null;
    private String toName = "merge_00";

    public Dataclean_v1() {
    }

    private void run() throws IOException, weibo4j.model.WeiboException {
        dataclean();
    }

    private boolean init(String args[]) {
        boolean flag = true;
        if (args.length == 0) {
            fromDir = "." + File.separator + "data" + File.separator + "movedData";
            toDir = "." + File.separator + "data" + File.separator + "movedData_cleaned";
        } else if (args.length == 1) {
            fromDir = "." + File.separator + "data" + File.separator + "movedData";
            toDir = args[0];
        } else if (args.length == 2) {
            fromDir = args[0];
            toDir = args[1];
        } else if (args.length == 3) {
            fromDir = args[0];
            toDir = args[1];
            toName = args[2];
//            isDelte = Integer.parseInt(args[2]);
        }else {
            return false;
        }
        File f = new File(toDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return flag;
    }

    public static void main(String[] args) throws IOException, weibo4j.model.WeiboException {
        Dataclean_v1 utog = new Dataclean_v1();
//        String input = "D:\\RWork\\weibo_crawler\\status_kongyangxin_109_10.12";
//        String output = "D:\\RWork\\weibo_crawler\\output";
//
//        args = new String[]{input, output,"merge_01"};
        boolean flag = utog.init(args);
        if (!flag) {
            return;
        }
        utog.run();
    }


    private void dataclean() throws IOException, weibo4j.model.WeiboException {
        File f = new File(fromDir);

        List<String> writeList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        for (File fileLevel1 : f.listFiles()) {

            if (fileLevel1.getName().endsWith("_status.txt")) {

                MyFile myFile = new MyFile(fileLevel1.getAbsolutePath(), input_encode);
                String rline = null;
                System.out.println("clean: " + fileLevel1.getName());
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

                myFile.close();
                if (writeList != null) {
                    if (Tool.write(toDir + File.separator + toName, writeList, true, output_encode)) {
                        if (isDelte == 1) {
                            Tool.deleteFile(fileLevel1);
                        }
                    }
                }
                writeList.clear();
                idList.clear();
            }
        }
    }

    @Test
    public void testOne() {
        String path = "D:\\data\\weibo\\test\\zhutao2\\data\\asdf.txt";
        File f = new File(path);
        Tool.deleteFile(f);
    }
}
