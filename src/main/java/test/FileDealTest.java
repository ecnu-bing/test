package test;

import java.io.File;

/**
 * Created by dingcheng on 2015/2/5.
 */
public class FileDealTest {
    public static void main(String args[]) {
        String needGetUid = "D:\\test\\uid_task - 副本";
        String newPaht = "D:\\test\\uid_task";
        String deleteDir = "D:\\test\\uid_task";
        File oldFile = new File(needGetUid);
        File fnew = new File(newPaht);
        oldFile.renameTo(fnew);
    }
}
