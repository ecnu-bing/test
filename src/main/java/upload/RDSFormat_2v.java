package upload;

import Util.MyFile;
import Util.XmlUtil;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDSFormat_2v {

    static XmlUtil xmlUtil = new XmlUtil();

    protected String nameSec1 = "i.003000000.";
    protected String nameSec2 = "140815.";
    protected String nameType = "1.";
    protected long nameStartNum = 0;
    protected String nameSuffix = ".xml";

    /*
    图的平均大小
     */
    protected int size_big_p = 200;
    protected int size_mid_p = 100;
    protected int size_sma_p = 50;

    /**
     * 获得输出路径
     * @return
     */
    protected String getEachIdPrefix() {
        return  this.nameSec1 + this.nameSec2 + this.nameType;
    }

    protected String inputDir = null;
    protected String outputDir = null;
    protected File outputDirFile = null;
    SimpleDateFormat inputFormat = null;
    protected File[] flist = null;
    private boolean isInited = false;

    /**
     * 初始化，设置输入输出目录
     * @param inputDir
     * @param outputDir
     */
    public RDSFormat_2v(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    public RDSFormat_2v() {

    }

    public static void main(String[] args) throws Exception {
        if (args.length <= 1) {
            System.out.println("[Command]: RDSFormat.jar inputDir outputDir CrawlTime type\nExample:RDSFormat.jar inDir outDir 140816 1");
            //D:\data\上传项目数据\7_weibo\test_status_baoting_109_10.17\ D:\data\上传项目数据\7_weibo\test_output_baoting\ 140816 1
            //D:\\data\\上传项目数据\\7_weibo\\test_in\\ D:\\data\\上传项目数据\\7_weibo\\test_out\\ 140816 1
            return;
        }
        String in = "D:\\data\\上传项目数据\\7_weibo\\test_status_baoting_109_10.17\\";
        String out = "D:\\data\\上传项目数据\\7_weibo\\test_output_baoting\\";
        String crawlTime = "140816";
        String type = "1";

        in = args[0];
        out = args[1];
        crawlTime = args[2];
        type = args[3];

//        in = "D:\\data\\上传项目数据\\7_weibo\\test_in\\";
//        out = "D:\\data\\上传项目数据\\7_weibo\\test_out\\";
        RDSFormat_2v rds = new RDSFormat_2v();
        rds.Init(in, out, crawlTime, type);

        System.out.println("inputDir="+in);
        System.out.println("outputDir="+out);
        System.out.println("crawlerTime="+crawlTime);
        System.out.println("dataType="+type);


        long res = rds.run();
        System.out.println("totally number: " + res);

//        rds.run();
//        long start_id = 0;
//        rds.createXML("D:\\data\\上传项目数据\\7_weibo\\test_one_status\\weibo_status.txt", rds.outputDir + rds.nameSec1 + rds.nameSec2 + rds.nameType + rds.nameStartNum + rds.nameSuffix,start_id);

    }

    /**
     *
     * @throws Exception
     */
    public long run() throws Exception {
        long start_id = nameStartNum;
        for (File f : flist) {
            start_id = createXML(f.getAbsolutePath() ,start_id);
        }
        return start_id;
    }

    /**
     * 初始化
     * @param in
     * @param out
     * @param CrawlTime
     * @param type
     */
    public void Init(String in, String out,String CrawlTime, String type) {
        this.inputDir = in;
        this.outputDir = out;
        outputDirFile = new File(this.outputDir);

        this.nameSec2 = CrawlTime+".";
        this.nameType = type+".";

        /**
         * 时间转换格式
         */
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("CHINA"));
        //this.failedUidPath = "."+File.separator+"data"+File.separator+"FailedUid_"+fileName;
        //this.needGetSinceIdFilePath = "."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetSinceId";
        //this.filepath="."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetMaxId";
        File dir = new File(this.inputDir);
        if (!dir.exists() || !dir.isDirectory())
            return;
        flist = dir.listFiles();
        isInited = true;

    }

    public long createXML2(String path, long sinceId) {
//        System.out.println("to deal with file[" + path + "] sinceId:" + sinceId);
        List<Status> statusList = new ArrayList<Status>();
        List<String> recordList = new ArrayList<String>();
        boolean flag = false;
        StatusWapper wapper = null;
        Set<Long> midSet = new HashSet<Long>();
        Long mid = null;
        String uid = null;
        int realStatusNum = 0;

        ArrayList<String> resultList = new ArrayList<String>();

        int count = 0;
        Element elem_root = xmlUtil.createElement("metadatas");
        /**
         * 按照人解析
         */
        MyFile myFile = new MyFile(path, "GBK");
        try {
            String line = null;
            while ((line = myFile.readLine()) != null) {

                String status_content = "";//微博的文本内容
                String status_uid_time = null;
                String status_uid = null;//用户id
                String status_time = null;//时间
                int status_number_bigp = 0;//大图的数量
                int status_number_midp = 0;//中图的数量
                int status_number_smap = 0;//小图的数量
                int status_number_url = 0;//url
                int status_number_weibo = 0;//
                String status_mid = null;//那些微博

                //uid time\t小图个数|#|中图个数|#|大图个数|#|url|#|微博条数|#|mid mid mid
                String temp_content[] = line.split("|#|");
                status_uid_time = temp_content[0];
                status_uid = status_uid_time.split(" ")[0];
                status_time = status_uid_time.split(" ")[1];

                status_number_smap = Integer.parseInt(temp_content[1]);
                status_number_midp = Integer.parseInt(temp_content[2]);
                status_number_bigp = Integer.parseInt(temp_content[3]);
                status_number_url = Integer.parseInt(temp_content[4]);
                status_number_weibo = Integer.parseInt(temp_content[5]);
                status_mid = temp_content[6];


                /**
                 * 这条微博
//                 */
//                Status temp_status = statusList.get(i);

                String link = "http:\\/\\/[^\\s]*";
                String topic = "#[\\S\\s]*?#";
                Pattern p = Pattern.compile(topic);
                Pattern p1 = Pattern.compile(link);

                Matcher matcher = p.matcher(status_content);
                Matcher matcher1 = p1.matcher(status_content);


                /**
                 * 微博计数
                 */
                count++;
                /**
                 * 创建一条微博的root
                 */
                long id = sinceId + count;  //本微博 id

                Element root = xmlUtil.createElement("metadata").addAttribute("id", "" + getEachIdPrefix() + id);
//                    Element root = doc.addElement("metadata").addAttribute("catergory", "data");
                //Element status = root.addElement("status").addAttribute("sn", "0"+String.valueOf(i+1));

                /**
                 * 标题：
                 */
                Element title = root.addElement("title");
                title.setText(status_uid_time);

                /**
                 * 用户id和用户名
                 */
                Element author = root.addElement("author");
                author.setText(status_uid);

                /**
                 * keyword：微博
                 */
                Element keyword = root.addElement("keyword");
                keyword.setText("微博");
//                if (matcher.find()) {
//                    keyword.setText("");
//                } else {
//                    keyword.setText("");
//                }

                /**
                 * 这段时间，该用户所有微博的MID
                 */
                Element description = root.addElement("description");


                root.addElement("type").setText("Text");

                Element format = root.addElement("format");
                format.setText("microblog");


                /**
                 * 该用户这段时间微博总量：文本+图片（小图、大图、头像）
                 */
                Element sizeElem = root.addElement("size");
                int status_size = size_big_p * status_number_bigp + size_mid_p * status_number_midp + size_sma_p * status_number_smap;
                sizeElem.setText("" + status_size);


                /**
                 * 1月31号，2月。。。每月一次。
                 */
                Element dateElem = root.addElement("date");
                dateElem.addElement("mdate").setText(inputFormat.format(status_time));

                Element identElem = root.addElement("identifier").addAttribute("platform", "local");
                identElem.addElement("path").setText("//58.198.176.83//haixinma//status");
                identElem.addElement("id").setText(this.getEachIdPrefix() + id);

                Element sourceElem = root.addElement("source");
                sourceElem.setText("iPad客户端");


                /**
                 * 转发
                 */
                Element reference = root.addElement("reference");
                reference.setText("haha.testReference");

//                if (temp_status.getRetweetedStatus() != null) {
//                    if (temp_content.contains("http")) {
//                        Element reference = root.addElement("reference");
//                        reference.setText(temp_status.getText().substring(temp_status.getText().indexOf("http")));
//                    }
//                }
//                if (matcher1.find()) {
//                    if (temp_content.contains("http")) {
//                        Element reference = root.addElement("reference");
//                        reference.setText(temp_status.getText().substring(temp_status.getText().indexOf("http")));
//                    }
//                }
//                if (temp_status.getBmiddlePic() != null) {
//                    Element reference = root.addElement("reference");
//                    String temp_str = temp_status.getBmiddlePic();
//                    if (temp_str.equals("")) {
//                        temp_str = "  ";
//                    }
//                    reference.setText(temp_str);
//                }

                /**
                 * description.abstract：所有的用户mid
                 */
                Element abstractElem = description.addElement("abstract");
                abstractElem.setText(status_mid);

                /**
                 * contributor
                 */
                Element contributor = root.addElement("contributor");
                contributor.setText("Sina");


                /**
                 * 把此条微博加入到 xml 文件中
                 */
                elem_root.add(root);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myFile.close();


        try {
            long firstId = sinceId + 1;
            String outputPath = outputDirFile.getPath()+"\\" + this.nameSec1 + this.nameSec2 + this.nameType + firstId + this.nameSuffix;
//            System.out.println("output = " + outputPath);
            elem_root.addAttribute("version","0.4");
            elem_root.addAttribute("category", "data");
            elem_root.addAttribute("count", "" + count);

            xmlUtil.writeXMLDom(DocumentHelper.createDocument(elem_root), outputPath, "utf-8");

            sinceId += count;

        } catch (Exception e) {
            System.out.println(e);
        }
        return sinceId;
    }


    /**
     * 一片微博文件，包含个人的多条微博
     * @param path
     * @throws java.io.IOException
     * @throws weibo4j.model.WeiboException
     */
    public long createXML(String path, long sinceId) throws IOException, weibo4j.model.WeiboException {
//        System.out.println("to deal with file[" + path + "] sinceId:" + sinceId);
        List<Status> statusList = new ArrayList<Status>();
        List<String> recordList = new ArrayList<String>();
        boolean flag = false;
        StatusWapper wapper = null;
        Set<Long> midSet = new HashSet<Long>();
        Long mid = null;
        String uid = null;
        int realStatusNum = 0;

        ArrayList<String> resultList = new ArrayList<String>();

        /**
         * 得到每条微博
         */
        List<String> weibo_status = Util.Tool.readFile(path, "GBK");

        //清空计数
        realStatusNum = 0;

        int count = 0;

        Element elem_root = xmlUtil.createElement("metadatas");

        for (String line : weibo_status) {
            //文件解析错误则跳过
            if (!line.endsWith("}") || !line.startsWith("{")) {
                continue;
            }
            resultList.add(line);
            try {
                wapper = Status.constructWapperStatus(line);
                statusList = wapper.getStatuses();

                /**
                 * 每一条记录（微博）
                 */
                for (int i = 0; i < statusList.size(); i++) {
                    /**
                     * 这条微博
                     */
                    Status temp_status = statusList.get(i);

                    String link = "http:\\/\\/[^\\s]*";
                    String topic = "#[\\S\\s]*?#";
                    Pattern p = Pattern.compile(topic);
                    Pattern p1 = Pattern.compile(link);

                    Matcher matcher = p.matcher(temp_status.getText());
                    Matcher matcher1 = p1.matcher(temp_status.getText());


                    /**
                     * 微博计数
                     */
                    count++;
                    /**
                     * 创建一条微博的root
                     */
                    long id = sinceId + count;  //本微博 id
                    String temp_content = temp_status.getText();    //本微博内容

                    Element root = xmlUtil.createElement("metadata").addAttribute("id", "" + getEachIdPrefix() + id);
//                    Element root = doc.addElement("metadata").addAttribute("catergory", "data");
                    //Element status = root.addElement("status").addAttribute("sn", "0"+String.valueOf(i+1));

                    Element title = root.addElement("title");
                    title.setText(temp_status.getId() + String.valueOf(temp_status.getCreatedAt()));

                    Element author = root.addElement("author");
                    author.setText(temp_status.getUser().getId() + temp_status.getUser().getName());

                    Element keyword = root.addElement("keyword");
                    /**
                     * keyword
                     */
                    if (matcher.find()) {
                        keyword.setText("");
                    }else{
                        keyword.setText("");
                    }

                    /*
                    这段时间，该用户所有微博的MID
                     */
                    Element description = root.addElement("description");


                    root.addElement("type").setText("Text");

                    Element format = root.addElement("format");
                    format.setText("microblog");


                    /*
                    该用户这段时间微博总量：文本+图片（小图、大图、头像）
                     */
                    Element sizeElem = root.addElement("size");
                    sizeElem.setText("49600");


                    /*
                    1月31号，2月。。。每月一次。
                     */
                    Element dateElem = root.addElement("date");
                    if (temp_status.getCreatedAt() != null) {
                        dateElem.addElement("mdate").setText(inputFormat.format(temp_status.getCreatedAt()));
                    }

                    Element identElem = root.addElement("identifier").addAttribute("platform","local");
                    identElem.addElement("path").setText("//58.198.176.83//haixinma//status");
                    identElem.addElement("id").setText(this.getEachIdPrefix() + id);

                    Element sourceElem = root.addElement("source");
                    sourceElem.setText("iPad客户端");


                    /**
                     * 转发
                     */
                    if (temp_status.getRetweetedStatus() != null) {
                        if (temp_content.contains("http")) {
                            Element reference = root.addElement("reference");
                            reference.setText(temp_status.getText().substring(temp_status.getText().indexOf("http")));
                        }
                    }
                    if (matcher1.find()) {
                        if (temp_content.contains("http")) {
                            Element reference = root.addElement("reference");
                            reference.setText(temp_status.getText().substring(temp_status.getText().indexOf("http")));
                        }
                    }
                    if (temp_status.getBmiddlePic() != null) {
                        Element reference = root.addElement("reference");
                        String temp_str = temp_status.getBmiddlePic();
                        if (temp_str.equals("")) {
                            temp_str = "  ";
                        }
                        reference.setText(temp_str);
                    }
                    String temp_content_1 = matcher1.replaceAll("");
                    Element abstractElem = description.addElement("abstract");
                    if (temp_content_1 == null || temp_content_1.equals("")) {
                        abstractElem.setText("此微博为空！");
                    }else{
                        int temp_length = temp_content_1.length();
                        if (temp_length >= 5) {
                            abstractElem.setText(temp_content_1.substring(0, 5));
                        }else{
                            abstractElem.setText(temp_content_1.substring(0, temp_length-1));
                        }
                    }
                    Element contributor = root.addElement("contributor");
                    contributor.setText("Sina");

                    /**
                     * 把此条微博加入到 xml 文件中
                     */
                    elem_root.add(root);
                }
                statusList.clear();

            } catch (NumberFormatException e1) {
                // System.out.println(fileLevel2.getAbsolutePath());
                continue;

            } catch (Exception e) {
                System.out.println("^^^^^^^^^^^^"+e);
            }
        }
        try {
            long firstId = sinceId + 1;
            String outputPath = outputDirFile.getPath()+"\\" + this.nameSec1 + this.nameSec2 + this.nameType + firstId + this.nameSuffix;
//            System.out.println("output = " + outputPath);
            elem_root.addAttribute("version","0.4");
            elem_root.addAttribute("category", "data");
            elem_root.addAttribute("count", "" + count);

            xmlUtil.writeXMLDom(DocumentHelper.createDocument(elem_root), outputPath, "utf-8");

            sinceId += count;

        } catch (Exception e) {
            System.out.println(e);
        }
        return sinceId;
    }

    protected Element convertToElement(Status status) {
        return null;
    }
}