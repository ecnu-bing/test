package Util;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件处理类
 * @author teisei
 * @since ecnu knowledge graph
 * @version 1.0
 */
public class FileUtil {

    private static String _read_encode_ = "gbk";
    private static String _write_encode_ = "gbk";

    public static String get_read_encode_() {
        return _read_encode_;
    }

    public static void set_read_encode_(String _read_encode_) {
        FileUtil._read_encode_ = _read_encode_;
    }

    public static String get_write_encode_() {
        return _write_encode_;
    }

    public static void set_write_encode_(String _write_encode_) {
        FileUtil._write_encode_ = _write_encode_;
    }

    /**
     * 读文件
     * @param filepath
     * @return
     */
    public String read(String filepath) {
		return read(filepath, _read_encode_);
	}

	public String read(File docDir) {
		return read(docDir, _read_encode_);
	}

	public String read(String filepath, String encode) {
		File docDir = new File(filepath);
		return read(docDir, encode);
	}

	public String read(File docDir, String encode) {
		FileInputStream fis;
		InputStreamReader isr;// 读流
		BufferedReader br;// 读字符串
		String line = null;
		String temp = null;
		StringBuffer buff = new StringBuffer();// 字符串缓存，存读入进来的字符串的
		try {
			fis = new FileInputStream(docDir);
			isr = new InputStreamReader(fis, encode);
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// line = line.substring(line.indexOf("\t") + 1);
				// System.out.println(line);
				buff.append(line);
				buff.append("\r\n");
			}
			br.close();
			isr.close();
			fis.close();

			temp = buff.toString();
			buff.delete(0, buff.length());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return temp;
	}



    public List<String> read2Arr(String filepath, String encode) {
        File docDir = new File(filepath);
        return read2Arr(docDir, encode);
    }
    /**
     * 读入数组
     * @param docDir
     * @param encode
     * @return
     */
    public List<String> read2Arr(File docDir, String encode) {
        List<String> res = new ArrayList<String>();
        FileInputStream fis;
        InputStreamReader isr;// 读流
        BufferedReader br;// 读字符串
        String line = null;
        String temp = null;
        StringBuffer buff = new StringBuffer();// 字符串缓存，存读入进来的字符串的
        try {
            fis = new FileInputStream(docDir);
            isr = new InputStreamReader(fis, encode);
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                res.add(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return res;
    }


    /**
     * 写文件。
     * @param file
     * @param str
     * @param isAppend
     * @param encode
     * @return
     */
	// 将字符串写入到filepath的路径下
	public static boolean write(File file, String str, boolean isAppend,
			String encode) {
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(file, isAppend);
			osw = new OutputStreamWriter(fileos, encode);
			bw = new BufferedWriter(osw);
			if (!str.equals("")) {
				bw.append(str);
				//bw.newLine();
			}

			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

    public static boolean writeError(String filepath, String info) {
        return write(filepath, info, true, "gbk");
    }

    // 将字符串写入到filepath的路径下
	public static boolean write(String filepath, String str, boolean isAppend,
			String encode) {
		File file = new File(filepath);
		return write(file, str, isAppend, encode);
	}
	// 默认编码方式为GBK
	public static boolean write(String filepath, String str, boolean isAppend) {
		return write(filepath, str, isAppend, _write_encode_);
	}
	// 默认为追加模式
	public static boolean write(String filepath, String str, String encode) {
		return write(filepath, str, true, encode);
	}

	// 将字符串写入到filepath的路径下,默认为追加，默认为“GBK”模式
	public static boolean write(String filepath, String str) {
		return write(filepath, str, true);
	}

	// 将string的一个数组list书序写入到filepath的文件中，换行
	public static boolean write(String filepath, List<String> list,
			boolean isAppend, String encode) {
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(filepath, isAppend);
			osw = new OutputStreamWriter(fileos, encode);
			bw = new BufferedWriter(osw);
			for (String s : list) {
				bw.append(s);
				bw.newLine();
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	//
	public static boolean write(String filepath, List<String> list,
			boolean isAppend) {
		return write(filepath, list, isAppend, _write_encode_);
	}

	// 将string的一个数组list书序写入到filepath的文件中，换行
	public static boolean write(String filepath, List<String> list) {
		return write(filepath, list, true);
	}

	public static boolean write(String filepath, Map resultMap) {
		Iterator iterator = resultMap.entrySet().iterator();
		ArrayList<String> recordList = new ArrayList<String>();
		List<String> resultList = new ArrayList<String>();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry<String, Long>) iterator.next();
			resultList.add(entry.getKey() + "\t" + entry.getValue());
			// write(".\\data\\twitterHashtag",key+"\t"+value);
		}
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(filepath, true);
			osw = new OutputStreamWriter(fileos, _write_encode_);
			bw = new BufferedWriter(osw);
			for (String s : resultList) {
				if (!s.equals("")) {
					bw.append(s);
					bw.newLine();
				}
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteFile(File file) {
		try {
			if (file.exists()) {
				file.delete();
				System.out.println("delete：" + file.getName());
				return true;
			}
		} catch (Exception e) {
			System.out.println("delete failed：" + file.getName());
			e.printStackTrace();
		}
		return false;
	}

    public static boolean split(String path,String outDir, String encode, int number) {
        System.out.println("split the file into " + number + " parts");
        File temp_dir = new File(outDir);
        if (!temp_dir.exists()) {
            temp_dir.mkdirs();
        }
        MyFile myFile = new MyFile(path, encode);
        int number_line = 0;
        try {
            while (myFile.readLine() != null) {
                number_line++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myFile.close();
        System.out.println("finish counting the numbers of lines in this file!");

        if (number_line == 0) {
            System.out.println("count number of lines failed!");
            return false;
        }
        int number_line_each = number_line / number + 1;

        myFile = new MyFile(path, encode);

        try {
            for (int i = 1; i <= number; i++) {
                System.out.println("doing " + i + " part");
                String output = outDir + File.separator + new File(path).getName() + "_part" + i;
                System.out.println("output: " + output);
                String res = "";

                int temp_i = 0;
                String line = null;
                while ((line = myFile.readLine()) != null && temp_i < number_line_each) {
//                    System.out.println("read a line:" + line);
                    System.out.println("part_" + i + ":" + temp_i + "/" + number_line_each);
                    res += line + "\r\n";
                    temp_i++;
                }
                write(output, res, true, encode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myFile.close();
        return true;

    }
    public static boolean split(String path,String encode ,int number) {
        return split(path, ".", encode, number);
    }

    public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("Usage: path [outDir] encode split_number");
        }
        if (args.length == 3) {
            String path = args[0];
            String encode = args[1];
            int number = Integer.parseInt(args[2]);
            split(path, encode, number);
        }
        if (args.length == 4) {
            String path = args[0];
            String outDir = args[1];
            String encode = args[2];
            int number = Integer.parseInt(args[3]);
            split(path, outDir, encode, number);
        }
    }

    @Test
    public void testOne() {
        String path = "D:\\data\\上传项目数据\\8_weibo\\test\\part-00000";
        String encode = "gbk";
        int number = 30;
        split(path, encode, number);
    }

    @Test
    public void testTwo() {
        String outpath = "//58.198.176.83/dingcheng/weibo_data/halo";
        write(outpath, "nihao a a a", true, "utf-8");
    }

}
