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
public class Dataclean_v2 {

    private String input_encode = "gbk";
    private String output_encode = "utf-8";
    private int isDelte = 0;//不删除原来数据
    private String log_path = "./complete.log";

    /**
     * 成功move数据的记录。
     */
    private String movedDataList ="." + File.separator + "data"+ File.separator + "uid_task_Moved"+ File.separator + "movedDataList.log";



    /**
     * 来源文件夹中成功merge的小文件。
     */
    private String mergedFromDataList = "." + File.separator + "data"+ File.separator + "uid_task_Moved"+ File.separator + "mergedFromDataList.log";
    private Map<String, Integer> mergedFromDataMap = new HashMap<String, Integer>();
    private String totalDir;
    private String workDir = "." + File.separator + "data"+ File.separator + "uid_task_Merged";
    /**
     * 成功merge数据的记录
     */
    private String mergedToDataList = "." + File.separator + "data"+ File.separator + "uid_task_Merged"+ File.separator + "Data_merged_list.log";
    private String mergedToDataMovedList = "." + File.separator + "data"+ File.separator + "uid_task_Merged"+ File.separator + "mergedToDataMovedList.log";


    //private String inputDir = "C:\\Users\\SEI-IMC\\Desktop\\";
    private String toDir = null;
    private String fromDir = null;
    private String toName = "merge_00";
    private String inputDir = "." + File.separator + "data"+ File.separator + "uid_task_Moved";
    private String outputDir = "." + File.separator + "data"+ File.separator + "uid_task_Merged";


    private void init() {
        mergedFromDataList = totalDir+ File.separator + "uid_task_Moved"+ File.separator + "mergedFromDataList.log";
        workDir = totalDir + File.separator + "uid_task_Merged";
        mergedToDataList = totalDir+ File.separator + "uid_task_Merged"+ File.separator + "Data_merged_list.log";
        mergedToDataMovedList =totalDir+ File.separator + "uid_task_Merged"+ File.separator + "mergedToDataMovedList.log";
        inputDir = totalDir+ File.separator + "uid_task_Moved";
        outputDir = totalDir + File.separator + "uid_task_Merged";
    }

    public Dataclean_v2(String totalDir) {
        this.totalDir = totalDir;
        init();
        File fdir = new File(workDir);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
    }

    private void run() throws IOException, weibo4j.model.WeiboException {
        dataclean();
    }

    private boolean init(String args[]) {
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
            isDelte = Integer.parseInt(args[2]);
        }else {
            return false;
        }
        File f = new File(toDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return true;
    }





    public void DataClean() {
        boolean flag = init(new String[]{inputDir, outputDir});
        if (!flag) {
            return;
        }
        datacleanOneDir();
    }



    /**
     * 将待clean的文件转移到目标文件夹，然后merge并记录。
     */
    private void datacleanOneDir() {
        File fdir = new File(fromDir);

        try {
            //merged的输出文件名
            toName = getToNameFromLog();
            //获得所有已经merged过的文件名
            GetMergedFromDataMap();
            dealEveryWeiboSet(fdir);
            Tool.write(mergedToDataList, toName + "\r\n", true, "utf-8");
        }catch (FileNotFoundException e) {

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

    private void GetMergedFromDataMap() {
        try {
            if (new File(mergedFromDataList).exists()) {
                MyFile myFile = new MyFile(mergedFromDataList, "utf-8");
                String line = myFile.readLine();
                while (line != null) {
                    mergedFromDataMap.put(line, 1);
                    line = myFile.readLine();
                }
                myFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * 获得本次merge文件的名字
     */
    private String getToNameFromLog() {
        int res = 0;
        try {
            if (new File(mergedToDataList).exists()) {
                MyFile myFile = new MyFile(mergedToDataList, "utf-8");
                String line = myFile.readLine();
                while (line != null) {
                    res++;
                    line = myFile.readLine();
                }
                myFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
        return "merged_" + res + "_" + new Random().nextInt();
    }

    private void dataclean() throws IOException, weibo4j.model.WeiboException {
        File f = new File(fromDir);
        /* 解析每个微博文件 */
        for (File fileLevel1 : f.listFiles()) {
            if (fileLevel1.getName().startsWith("status_")) {
                dealEveryWeiboSet(fileLevel1);
                if (isDelte == 1) {
                    Tool.deleteFile(fileLevel1);
                }
            }
        }
    }
    /**
     * 将每个存放微博的文件merge到一个文件。
     */
    public void dealEveryWeiboSet(File f) throws IOException {
        List<String> writeList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();

        /* 解析每个微博文件 */
        for (File fileLevel1 : f.listFiles()) {

            if (fileLevel1.getName().endsWith("_status.txt")) {
                //如果merge过该文件
                if (mergedFromDataMap.containsKey(fileLevel1.getName())) {
                    System.out.println("this file has been merged, delete it");
                    Tool.deleteFile(fileLevel1);
                    continue;
                }

                MyFile myFile = new MyFile(fileLevel1.getAbsolutePath(), input_encode);
                String rline;
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

                if (isDelte == 1) {
                    Tool.deleteFile(fileLevel1);
                }
                Tool.write(mergedFromDataList, f.getName() + "\r\n", true, "utf-8");
            }
        }
        Tool.write(log_path, f.getName(), true, output_encode);

    }

    @Test
    public void testOne() {
        String path = "D:\\data\\weibo\\test\\zhutao2\\data\\asdf.txt";
        File f = new File(path);
        Tool.deleteFile(f);
    }


    public static void main(String[] args) throws IOException, weibo4j.model.WeiboException {
        Dataclean_v2 utog = new Dataclean_v2("./data/");
//        args = new String[]{
//            "D:\\RWork\\weibo_crawler\\input",
//            "D:\\RWork\\weibo_crawler\\out",
//            "1"
//        };
        boolean flag = utog.init(args);
        if (!flag) {
            return;
        }
        utog.run();
    }
}
