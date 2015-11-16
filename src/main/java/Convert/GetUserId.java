package Convert;

import Util.FileUtil;
import Util.ImportUtil;

/**
 * 从一个用户信息文件（二位数组）中，取出每一行的第一位，在存到文件。
 * Created by dingcheng on 2014/12/25.
 */
public class GetUserId {
    static FileUtil fileUtil = new FileUtil();
    static ImportUtil importUtil = new ImportUtil();

    /**
     *
     * @param path
     * @param output
     * @param split 分隔符
     */
    public void process(String path, String output,String split,String encode) {
        System.out.println("split:" + split);
        split = "\\|#\\|";
        String temp[][] = importUtil.getMatrix(path, "\r\n", split, encode);
        System.out.println(temp[0]);

        String id[] = importUtil.getCol(temp, 0);
        String res = importUtil.arr2str(id, "\r\n");
        fileUtil.write(output, res, true, encode);
    }

    public static void main(String args[]) {
        if (args.length < 4) {
            System.out.println("args not enough!");
            return;
        }
        GetUserId getUserId = new GetUserId();
        String path = args[0];
        String output = args[1];
        String split = args[2];
        String encode = args[3];
        getUserId.process(path, output, split, encode);
    }
}
