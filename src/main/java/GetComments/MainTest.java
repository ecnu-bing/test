package GetComments;

import java.io.File;

/**
 * 通过原始微博，爬取所有的评论。
 * Created by dingcheng on 2015/1/8.
 */
public class MainTest {
    public static void main(String args[]) throws Exception {

        String totalDir = args[0];
        String taskDir = totalDir + File.separator + "task";

        File taskDirFile = new File(taskDir);
        for (File f : taskDirFile.listFiles()) {
            doonefile(new String[]{args[0], f.getAbsolutePath()});
        }

//        getstatuscomments getcomments = new getstatuscomments();
//        getcomments.run(args);
//
//        GetCommentForCWL convert2CWL = new GetCommentForCWL();
//
//        convert2CWL.run(args[0] + File.separator + args[1] + "_comments" + File.separator + "data" + File.separator, args[1]);
    }

    /**
     * 处理一个任务
     * @param args
     * @throws Exception
     */
    public static void doonefile(String args[]) throws Exception {
        String dir = args[0];
        String inputPath = args[1];
        getstatuscomments getcomments = new getstatuscomments();
        getcomments.init(new String[]{inputPath, dir});
        getcomments.run(new String[]{inputPath});

//        GetCommentForCWL convert2CWL = new GetCommentForCWL();
//        String inputName = new File(inputPath).getName();
//        System.out.println("1:" + args[0] + File.separator + inputName + "_comments" + File.separator + "data" + File.separator + "   1:" + args[1]);
//
//
//        convert2CWL.run(dir + File.separator + inputName + "_comments" + File.separator + "data" + File.separator, inputName);
    }
}
