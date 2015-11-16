package Util;

import org.junit.Test;

/**
 * 工具类，导入文件，成为数组或者矩阵
 * Created by dingcheng on 2014/10/29.
 */
public class ImportUtil {

    static String _read_encode_ = "gbk";
    static String _write_encode_ = "gbk";

    public static String get_read_encode_() {
        return _read_encode_;
    }

    public static void set_read_encode_(String _read_encode_) {
        ImportUtil._read_encode_ = _read_encode_;
    }

    public static String get_write_encode_() {
        return _write_encode_;
    }

    public static void set_write_encode_(String _write_encode_) {
        ImportUtil._write_encode_ = _write_encode_;
    }

    static FileUtil fileUtil = new FileUtil();

    /**
     * 从输入文件导入矩阵
     * @param inputpath
     * @param r1
     * @param r2
     * @return
     */
    public String[][] getMatrix(String inputpath, String r1, String r2) {
        return getMatrix(inputpath, r1, r2, _read_encode_);
    }
    public String[][] getMatrix(String inputpath,String r1, String r2, String encode){

        String[][] temp_matrix = null;
        String temp_src = fileUtil.read(inputpath,encode);

        String[] temp_list = temp_src.split(r1);
        temp_matrix = new String[temp_list.length][];
        for (int i = 0; i < temp_list.length; i++) {
            temp_matrix[i] = temp_list[i].split(r2);
        }
        return temp_matrix;
    }

    /**
     * 获得矩阵的第 i 行， 第 i 列
     * @param m
     * @param i
     * @return
     */
    public String[] getRow(String[][] m, int i){
        return m[i];
    }
    public String[] getCol(String[][] m, int j){
        String[] res = new String[m.length];
        for(int u=0;u<m.length;u++){
            res[u] = m[u][j];
        }
        return res;
    }

    /**
     * 矩阵和字符串之间的互相转换。
     * @param str
     * @param r1
     * @param r2
     * @return
     */
    public String[][] str2arr(String str, String r1, String r2) {
        if (str == null || str.equals("")) {
            return null;
        }
        String res[][] = null;
        String rows[] = str.trim().split(r1);

        res = new String[rows.length][];
        for (int uu = 0; uu < rows.length; uu++) {
            res[uu] = rows[uu].trim().split(r2);
        }
        return res;
    }

    public String arr2str(String arr[][], String r1, String r2) {
        String res = "";
        for (int i = 0; i < arr.length; i++) {
            String temp = "";
            for (int j = 0; j < arr[i].length; j++) {
                temp += arr[i][j] + r2;
            }
            temp = temp.substring(0, temp.length() - r1.length());
            res += temp + r1;
        }
        res = res.substring(0, res.length() - r2.length());
        return res;
    }

    public String arr2str(String arr[], String r1) {
        String res = "";
        for (int i = 0; i < arr.length; i++) {
            res += arr[i] + r1;
        }
        res = res.substring(0, res.length() - r1.length());
        return res;
    }

    private void Print2dArr(String[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void testStr2Arr() {
        String str = fileUtil.read("D:\\RWork\\教育新浪微博\\test\\教育_screen_info", "gbk");
        String res[][] = this.str2arr(str, "\r\n", "\\|\\#\\|");
        Print2dArr(res);

        String temp = this.arr2str(res, "\r\n", "----");
        fileUtil.write("D:\\RWork\\教育新浪微博\\test\\教育_screen_info_1", temp, false, "gbk");
    }
}
