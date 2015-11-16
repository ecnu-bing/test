package GetFailed;

import Util.Tool;
import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.*;
import java.util.*;

/**
 * 定期将数据转移到Move文件夹。
 * Created by Teisei on 2015/4/2.
 */
public class UpdataMaxId {
    private String totalDir;
    private String inputDir = "." + File.separator + "data" + File.separator + "uid_task_Status";
    private String outputPath = "." + File.separator + "data" + File.separator + "uid_task_failed_status";
    private String outputPath1 = "." + File.separator + "data" + File.separator + "uid_task_Maxid_status";
    private String tag = null;
    protected String outputDataDir =  "." + File.separator + "data" + File.separator + "uid_task_Moved";

    //private String movedDataList =  "." + File.separator + "data"+ File.separator +"uid_task_Moved"+ File.separator +"movedDataList.log";

    public UpdataMaxId(String tag, String ouputDataDir) {
        this.tag = tag;
        this.outputDataDir = ouputDataDir;
        File fdir = new File(this.outputDataDir);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
    }

    //    public UpdataMaxId(String tag) {
//        this.tag = tag;
//    }
    private void init() {
        inputDir = totalDir + File.separator + "uid_task_Status";
        outputPath = totalDir + File.separator + "uid_task_failed_status";
        outputPath1 = totalDir + File.separator + "uid_task_Maxid_status";
        outputDataDir =  totalDir + File.separator + "uid_task_Moved";
    }
    public UpdataMaxId(String totalDir) {
        this.totalDir = totalDir;
        init();
        File fdir = new File(this.outputDataDir);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
    }

    public static void main(String[] args) {
        args = new String[]{
                "maxId",
                "./dingcheng_111111"
        };
        if (args.length != 2) {
            System.out.println("enter the type and output path,if you want to move data,type is move; if you want to " +
                    "get the maxId to crawl the complete data, type is maxId");
            return;
        }
        UpdataMaxId updataMaxId = new UpdataMaxId(args[0], args[1]);

        updataMaxId.run();
    }

    public void TemporalMoveData() {
        this.tag = "move";
        run();
    }

    public void MoveData() {
        this.tag = "maxId";
        run();
    }

    public void run() {
        printConfig();
        if (this.tag.equals("move")) {
            analysisData();
        } else if (this.tag.equals("maxId")) {
            getMaxId();
        }
        analysisData();
    }

    public void printConfig() {
        String inputDir = totalDir + File.separator + "uid_task_Status";
        String outputPath = totalDir + File.separator + "uid_task_failed_status";
        String outputPath1 = totalDir + File.separator + "uid_task_Maxid_status";
        String outputDataDir =  totalDir + File.separator + "uid_task_Moved";
        if (inputDir != null) {
            System.out.println("uid_task_Status: " + inputDir);
        }
        if (outputPath != null) {
            System.out.println("uid_task_failed_status: " + outputPath);
        }
        if (outputPath1 != null) {
            System.out.println("uid_task_Maxid_status: " + outputPath1);
        }
        if (outputDataDir != null) {
            System.out.println("uid_task_Moved: " + outputDataDir);
        }
    }

    private void getMaxId() {
        File filedir = new File(inputDir);
        File[] fileList = filedir.listFiles();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line;
        StatusWapper wapper;
        List<Status> statusList;
        int count;
        String maxId;
        int hisStatusCount;
        long nowStatusCount;
        boolean flag;
        ArrayList<String> resultList = new ArrayList<String>();
        //String deleteFilepath = null;
        try {
            if(fileList!=null)
                for (File file : fileList) {
                    System.out.println(file.getName());
                    //***初始化变量***
                    maxId = null;
                    count = 0;
                    //hisStatusCount = 0;
                    nowStatusCount = 0;
                    flag = false;
                    String[] list = file.getName().split("_");
                    hisStatusCount = Integer.parseInt(list[2]);
                    fis = new FileInputStream(file);
                    isr = new InputStreamReader(fis, "GBK");
                    br = new BufferedReader(isr);
                    while ((line = br.readLine()) != null) {
                        if (!line.endsWith("}") || !line.startsWith("{")) {
                            continue;
                        }
                        resultList.add(line);
                        try {
                            wapper = Status.constructWapperStatus(line);
                        } catch (weibo4j.model.WeiboException e) {
                            // TODO Auto-generated catch block
                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                            //e.printStackTrace();
                        } catch (WeiboException e) {
                            // TODO Auto-generated catch block

                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                            //e.printStackTrace();
                        }
                        if (wapper == null) {
                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                        }

                        if (!flag) {
                            nowStatusCount = wapper.getTotalNumber();
                            flag = true;
                        }
                        statusList = wapper.getStatuses();
                        if (statusList.size() > 0) {
                            maxId = statusList.get(statusList.size() - 1).getMid();
                        }
                        count += statusList.size();
                        statusList.clear();
                    }
                    br.close();
                    isr.close();
                    fis.close();
                    //如果获取数据比需要获取的数据少则得到maxId继续爬数据。
                    //System.out.println(maxId);
                    //long needGetCount = nowStatusCount - hisStatusCount;
                    long missStatusCount = nowStatusCount - hisStatusCount - count;
                    //缺失大于2%，则需要继续获取
                    if (maxId != null && flag && missStatusCount > (double) nowStatusCount * 10 / 100 && missStatusCount >= 50) {
                        //写出文件内容，uid_sinceId_maxId_oldStatus_newStatus_getStatusThisTime
                        String record = list[0] + "_" + list[1] + "_" + maxId + "_" + hisStatusCount + "_" + nowStatusCount + "_" + count;
                        //System.out.println(record);
                        Tool.write(this.outputPath1, record);
                    }
                    //获取数据差少于2%条 或者 总量差值 小于 20，则算成功
                    if (flag && (missStatusCount <= (double) nowStatusCount * 10 / 100 || missStatusCount < 50)) {
                        //已成功再删除
                        if (Tool.write(this.outputDataDir + File.separator + file.getName(), resultList)) {
                            Tool.deleteFile(file);
                        } else {
                            System.out.println("write file error");
                            return;
                        }

                    }
                    if (!flag) {
                        if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                            Tool.deleteFile(file);
                        }
                    }
                    resultList.clear();
                }
        } catch (FileNotFoundException e) {

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
    private void analysisData() {
        File filedir = new File(inputDir);
        File[] fileList = filedir.listFiles();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line;
        StatusWapper wapper;
        List<Status> statusList;
        int count;
        int hisStatusCount;
        long nowStatusCount;
        boolean flag;
        ArrayList<String> resultList = new ArrayList<String>();
        //String deleteFilepath = null;
        try {
            if(fileList!=null)
                for (File file : fileList) {
                    //***初始化变量***
                    count = 0;
                    //hisStatusCount = 0;
                    nowStatusCount = 0;
                    flag = false;
                    String[] list = file.getName().split("_");
                    hisStatusCount = Integer.parseInt(list[2]);
                    fis = new FileInputStream(file);
                    isr = new InputStreamReader(fis, "GBK");
                    br = new BufferedReader(isr);
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("{") || !line.endsWith("}")) {
                            continue;
                        }
                        resultList.add(line);
                        try {
                            wapper = Status.constructWapperStatus(line);
                        } catch (weibo4j.model.WeiboException e) {
                            // TODO Auto-generated catch block
                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                            //e.printStackTrace();
                        } catch (WeiboException e) {
                            // TODO Auto-generated catch block

                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                            //e.printStackTrace();
                        }
                        if (wapper == null) {
                            System.err.println("reconstruct the json error");
                            if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                                Tool.deleteFile(file);
                            }
                            continue;
                        }
                        if (!flag) {
                            nowStatusCount = wapper.getTotalNumber();
                            flag = true;
                        }

                        statusList = wapper.getStatuses();
                        count += statusList.size();
                        statusList.clear();
                    }
                    br.close();
                    isr.close();
                    fis.close();
                    //获取数据差少于1%条，则算成功
                    if (flag && (nowStatusCount - hisStatusCount) <= (count + (double) nowStatusCount / 100)) {
                        //已成功再删除
                        if (Tool.write(this.outputDataDir + File.separator + file.getName(), resultList)) {
                            Tool.deleteFile(file);
                        } else {
                            System.out.println("write file error");
                            return;
                        }

                    }
                    if (!flag) {
                        if (Tool.write(this.outputPath, list[0] + "_" + list[1] + "_" + list[2])) {
                            Tool.deleteFile(file);
                        }
                    }
                    resultList.clear();
                }
        } catch (FileNotFoundException e) {

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
}
