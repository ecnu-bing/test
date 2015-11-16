package Util;

import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 在指定某用户的微博中个，去重复。
 */
public class Dataclean {

    private String encode = "gbk";

    //private String inputDir = "C:\\Users\\SEI-IMC\\Desktop\\";
    private String toDir = "F:";
    //private String toDir = null;
    private String fromDir = "F:";
    //private String fromDir = null;

    public Dataclean() {
    }

    public Dataclean(String fromDir, String toDir) {
        this.fromDir = fromDir;
        this.toDir = toDir;
    }

    private void run() throws IOException, weibo4j.model.WeiboException {
        init();
        dataclean();
    }

    private void init() {

    }

    public static void main(String[] args) throws IOException, weibo4j.model.WeiboException {
        Dataclean utog = new Dataclean(args[0], args[1]);
        //Dataclean utog = new Dataclean ();
        utog.run();
    }


    private void dataclean() throws IOException, weibo4j.model.WeiboException {
        File f = new File(fromDir);
        File[] fileList1 = f.listFiles();

        List<String> writeList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        for (File fileLevel1 : fileList1) {
            InputStream fis;
            InputStreamReader fisr;
            BufferedReader br;

            if (fileLevel1.getName().endsWith("_status.txt")) {

                MyFile myFile = new MyFile(f.getAbsolutePath(), encode);
                String rline = null;
                System.out.println(fileLevel1);
                int count = 0;
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
                    Tool.write(toDir, rline, true, "utf-8");
                    String id = rline.substring(rline.indexOf("mid") + 3, rline.indexOf("idstr") - 3);

                    Tool.write(toDir + File.separator + "merge", rline, true, "utf-8");
                    System.out.println(count);
                    if (!writeList.contains(rline) && !idList.contains(id)) {
                        idList.add(id);
                        writeList.add(rline);
                    }
                    Tool.write(toDir + File.separator + "merge", rline, true, "utf-8");
                }

                if (writeList != null) {
                    if (Tool.write(toDir + File.separator + "merge", writeList, true, "utf-8")) {
                        Tool.deleteFile(fileLevel1);
                    }
                }
                writeList.clear();
                idList.clear();
                myFile.close();
            }

        }
    }
}
