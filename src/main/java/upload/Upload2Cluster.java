package upload;

import Util.MyFile;
import Util.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Teisei on 2015/4/2.
 */
public class Upload2Cluster {

    Date today = new Date();
    String today_str = "" + today.getYear() + "." + today.getMonth();
    private String totalDir = "";
    String outDir = "//58.198.176.83" + File.separator + "dingcheng" + File.separator + "weibo_data" + File.separator + today_str;
    private String remote_uploadToDataList = outDir + File.separator + "Data_uploaded_List.log";

    private String workDir = "." + File.separator + "data" + File.separator + "uid_task_Merged";
    private String mergedToDataList = "." + File.separator + "data" + File.separator + "uid_task_Merged" + File.separator + "Data_merged_list.log";
    private String uploadToDataList = "." + File.separator + "data" + File.separator + "uid_task_Merged" + File.separator + "Data_uploaded_List.log";


    Map<String, Integer> mergedFileMap = new HashMap<String, Integer>();
    Map<String, Integer> uploadedFileMap = new HashMap<String, Integer>();

    public void init() {
        workDir = totalDir + File.separator + "uid_task_Merged";
        mergedToDataList = totalDir + File.separator + "uid_task_Merged" + File.separator + "Data_merged_list.log";
        uploadToDataList = totalDir + File.separator + "uid_task_Merged" + File.separator + "Data_uploaded_List.log";

    }
    public Upload2Cluster(String totalDir) {
        this.totalDir = totalDir;
        init();
    }

    public void put2remote() {

        File fdir = new File(outDir);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        try {
            if (new File(mergedToDataList).exists()) {
                //读取已经合并的文件//读取已经上传的文件
                MyFile myFile = new MyFile(mergedToDataList, "utf-8");
                String line = myFile.readLine();
                while (line != null) {
                    if (line.startsWith("merged_"))
                        mergedFileMap.put(line, 1);
                    line = myFile.readLine();
                }
                myFile.close();
            }
        } catch (IOException e1) {
        } catch (NullPointerException e2) {
        }
        try {
            if (new File(uploadToDataList).exists()) {
                MyFile myFile1 = new MyFile(uploadToDataList, "utf-8");
                String line = myFile1.readLine();
                while (line != null) {
                    if(line.startsWith("merged_"))
                        uploadedFileMap.put(line, 1);
                    line = myFile1.readLine();
                }
                myFile1.close();
            }
        }catch (IOException e1) {
        } catch (NullPointerException e2) {
        }

        try {
            File dir = new File(workDir);
            for (File f : dir.listFiles()) {
                if (f.getName().startsWith("merged_")) {
                    if (mergedFileMap.containsKey(f.getName()) && !uploadedFileMap.containsKey(f.getName())) {
                        FileChannelCopy(f.getAbsolutePath(), outDir + File.separator + f.getName());
                        Tool.write(uploadToDataList, f.getName() + "\r\n", true, "utf-8");
                        Tool.write(remote_uploadToDataList, f.getName() + "\r\n", true, "utf-8");
                    } else {
                        Tool.deleteFile(f);
                    }
                }
            }
        } catch (IOException e) {

        } catch (NullPointerException e1) {
        } catch (Exception e2) {

        }
    }

    public static long FileChannelCopy(String inFile, String outFile) throws Exception {
        long begin = System.currentTimeMillis();
        File in = new File(inFile);
        File out = new File(outFile);
        FileInputStream fin = new FileInputStream(in);
        FileOutputStream fout = new FileOutputStream(out);
        FileChannel inc = fin.getChannel();
        FileChannel outc = fout.getChannel();
        int bufferLen = 2097152;
        ByteBuffer bb = ByteBuffer.allocateDirect(bufferLen);
        while (true) {
            int ret = inc.read(bb);
            if (ret == -1) {
                fin.close();
                fout.flush();
                fout.close();
                break;
            }
            bb.flip();
            outc.write(bb);
            bb.clear();
        }
        long end = System.currentTimeMillis();
        long runtime = 0;
        if (end > begin)
            runtime = end - begin;
        return runtime;

    }

    /**
     * io拷贝
     *
     * @param inFile  源文件
     * @param outFile 目标文件
     * @return
     * @throws Exception
     */
    public static long FileStraeamCopy(String inFile, String outFile) throws Exception {
        long begin = System.currentTimeMillis();

        File in = new File(inFile);
        File out = new File(outFile);
        FileInputStream fin = new FileInputStream(in);
        FileOutputStream fout = new FileOutputStream(out);

        int length = 2097152;//2m内存
        byte[] buffer = new byte[length];

        while (true) {
            int ins = fin.read(buffer);
            if (ins == -1) {
                fin.close();
                fout.flush();
                fout.close();
                break;

            } else
                fout.write(buffer, 0, ins);

        }
        long end = System.currentTimeMillis();
        long runtime = 0;
        if (end > begin)
            runtime = end - begin;
        return runtime;

    }

    static public void main(String args[]) throws Exception {

//        File file = new File("//58.198.176.83/dingcheng/weibo_data/hehe");
//        file.mkdirs();
//
        String inFile = "D:\\IntelliJ_Projects\\crawler\\data\\uid_task_Merged\\merged_0_-1587756265"; //源文件
        String outFile = "//58.198.176.83/dingcheng/weibo_data/merged_0_-1587756265"; //输出文件1
//        String outFile2 = "//58.198.176.83/dingcheng/weibo_data/mergedToDataList_1.log"; //输出文件2
        long runtime1, runtime2;
        runtime1 = FileChannelCopy(inFile, outFile);
//        runtime2 = FileStraeamCopy(inFile, outFile2);
//        System.out.println("FileChannelCopy running time:" + runtime1);
//        System.out.println("FileStraeamCopy running time:" + runtime2);


    }
}
