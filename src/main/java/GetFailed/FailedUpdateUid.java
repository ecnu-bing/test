package GetFailed;

import Util.Tool;
import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FailedUpdateUid {
    private String inputDir = null;
    private String outputPath = null;
    private String outputPath1 = null;
    private String totalDir = null;
    protected String outputDataDir = null;

    public FailedUpdateUid() {

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("enter the type and output path,if you want to move data,type is move; if you want to " +
                    "get the maxId to crawl the complete data, type is maxId");
            return;
        }
        FailedUpdateUid failedUpdateUid = new FailedUpdateUid();
        /*
        args = new String[]{inputDir, outputDir};
         */
        failedUpdateUid.init(args);
        failedUpdateUid.run();
    }

    /**
     * input_outputDir
     */
    public void init(String args[]) {
        if (args.length == 1) {
            inputDir = "." + File.separator + "data" + File.separator + "status";
            outputDataDir = args[0];
        } else if (args.length == 2) {
            inputDir = args[0];
            outputDataDir = args[1];
        }

        File outputDataDirFile = new File(outputDataDir);
        if (!outputDataDirFile.exists()) {
            outputDataDirFile.mkdirs();
        }

        totalDir = new File(inputDir).getParent();
        outputPath = totalDir + File.separator + "uid_failed_status";
        outputPath1 = totalDir + File.separator + "uid_Maxid_status";

    }

    public void run() {
        getMaxId();
        analysisData();
    }

    /**
     * 如果获取数据比需要获取的数据少则得到maxId继续爬数据。
     */
    private void getMaxId() {
        File filedir = new File(inputDir);
        File[] fileList = filedir.listFiles();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        StatusWapper wapper = null;
        List<Status> statusList = new ArrayList<Status>();
        int count = 0;
        String maxId = null;
        int hisStatusCount = 0;
        long nowStatusCount = 0;
        boolean flag = false;
        ArrayList<String> resultList = new ArrayList<String>();
        String deleteFilepath = null;
        try {
            for (File file : fileList) {
                System.out.println(file.getName());
                //***初始化变量***
                maxId = null;
                count = 0;
                hisStatusCount = 0;
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
                long needGetCount = nowStatusCount - hisStatusCount;
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
        String line = null;
        StatusWapper wapper = null;
        List<Status> statusList = new ArrayList<Status>();
        int count = 0;
        int hisStatusCount = 0;
        long nowStatusCount = 0;
        boolean flag = false;
        ArrayList<String> resultList = new ArrayList<String>();
        String deleteFilepath = null;
        try {
            for (File file : fileList) {
                //***初始化变量***
                count = 0;
                hisStatusCount = 0;
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
