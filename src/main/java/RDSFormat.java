import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import weibo4j.WeiboException;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDSFormat {
	
	protected String inputDir = null;
	protected String outputFile = null;
	SimpleDateFormat inputFormat = null;
	protected File[] flist = null;
	private boolean isInited = false;
	
	public RDSFormat(String inputDir,String outputFile)
    {
	    this.inputDir = inputDir;
	    this.outputFile = outputFile;	    
    }
	
	public RDSFormat()
    {
	        
    }
	
	public static void main(String[] args) throws Exception
	{
	   // if(args.length<=1)
	   // {
	     //   System.out.println("enter the inputDir and outputFile");
	     //   return;
	   // }
	    RDSFormat rds = new RDSFormat("D:\\data\\上传项目数据\\7_weibo\\test_status_baoting_109_10.17\\","D:\\data\\上传项目数据\\7_weibo\\test_status_baoting_109_10.17\\");
		rds.run();
	}
	
	public void run() throws Exception
	{
		Init();
		createXML();
	}
	
	public void Init()
	{
		this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("CHINA"));
		//this.failedUidPath = "."+File.separator+"data"+File.separator+"FailedUid_"+fileName;
		//this.needGetSinceIdFilePath = "."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetSinceId";
		//this.filepath="."+File.separator+"data"+File.separator+"updatelist"+File.separator+"needGetMaxId";
		File dir = new File(this.inputDir);
		if (!dir.exists() || !dir.isDirectory())
			return;
		flist = dir.listFiles();
		isInited = true;
	}
	
   public void createXML() throws IOException, weibo4j.model.WeiboException {

//		if (!isInited)
//			Init();
//		if (flist == null)
//			return;
	
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		List<Status> statusList = new ArrayList<Status>();
		List<String> recordList = new ArrayList<String>();
		boolean flag = false;
		StatusWapper wapper = null;
		Set<Long> midSet = new HashSet<Long>();
		Long mid = null;
		String uid = null;
		int realStatusNum = 0;
		long sinceId = 0l;
		String line = null;
		//for (File file : flist)
		//{
			
		//	File[] flist1 = file.listFiles();
			//for(File fileLevel1 : flist1)
			//{ 
			  //  File[] flist2 = fileLevel1.listFiles();
			  //  for(File fileLevel2: flist2)
			 // {
		 ArrayList<String> resultList = new ArrayList<String>();
		             File f=new File("D:\\1.txt");
	                fis = new FileInputStream(f);
	                isr = new InputStreamReader(fis,"GBK");
	                br = new BufferedReader(isr);
	                
	                //清空计数
	                realStatusNum = 0;
    
	                sinceId = 0l;
	                Date date = null;
	                int count=0;
	                while((line = br.readLine()) != null )
	                {
	                    //文件解析错误则跳过
	                	 if(!line.endsWith("}")||!line.startsWith("{"))
	                     {
	                         continue;
	                     }
	                	  resultList.add(line);
	                    try
	                    {
	                    wapper = Status.constructWapperStatus(line);
	                    statusList = wapper.getStatuses();

	                    for (int i = 0; i<statusList.size();i++)
	                    {
	                    	
	                    	String link = "http:\\/\\/[^\\s]*";    
	                    	String topic = "#[\\S\\s]*?#"; 
	                    	Pattern p = Pattern.compile(topic);
	                        Pattern p1 = Pattern.compile(link);
	                        Matcher matcher = p.matcher(statusList.get(i).getText());
	                        Matcher matcher1 = p1.matcher(statusList.get(i).getText());
	                    	
	                    	Document doc = DocumentHelper.createDocument();
	                    	Element root = doc.addElement("metadata").addAttribute("catergory", "data");
	                    	//Element status = root.addElement("status").addAttribute("sn", "0"+String.valueOf(i+1));
	                    	Element title = root.addElement("title");
	                    	Element author = root.addElement("author");
	                    	Element description = root.addElement("description");
	                    	Element contributor = root.addElement("contributor");
	                    	Element format = root.addElement("format");
	                    	
	                    	title.setText(statusList.get(i).getId()+String.valueOf(statusList.get(i).getCreatedAt()));
	                    	author.setText(statusList.get(i).getUser().getId()+statusList.get(i).getUser().getName());
	                    	//
	                    	contributor.setText("Sina");
	                    	format.setText("microblog");
	                    	                     
	                        
	                        if(matcher.find())
	                        {
	                        	Element keyword = root.addElement("keyword");
	                        	keyword.setText("kkkkkkkkkkkk");
	                        }
	                        
	                        if(statusList.get(i).getRetweetedStatus()!=null){
	                        	Element reference = root.addElement("reference");
	                        	reference.setText(statusList.get(i).getText().substring(statusList.get(i).getText().indexOf("http")));
	                        }
	                        if(matcher1.find()){
	                        	Element reference = root.addElement("reference");
	                        	reference.setText(statusList.get(i).getText().substring(statusList.get(i).getText().indexOf("http")));
	                        }
	                        if(statusList.get(i).getBmiddlePic()!=null){
	                        	Element reference = root.addElement("reference");
	                        	reference.setText(statusList.get(i).getBmiddlePic());
	                        }
	                        description.setText(matcher1.replaceAll(""));
	                        try {
	                        	   OutputFormat format1 = new OutputFormat("  ", true);
	                        	  // format1.setEncoding("gb2312");
	                        	   // 可以把System.out改为你要的流。
	                        	   FileOutputStream fos = new FileOutputStream(new File("d:\\211\\descriptions\\zqy.xml"));
	                        	   OutputStreamWriter outwriter = new OutputStreamWriter(fos);
	                        	   XMLWriter xmlWriter = new XMLWriter(new PrintWriter(outwriter), format1);
	                        	   xmlWriter.write(doc);
	                        	   xmlWriter.close();
	                        	  } catch (IOException e) {
	                        	   e.printStackTrace();
	                        	  }
	                    }
	                  
	                    statusList.clear();
	                            
	                }catch (WeiboException e)
	                {
	                    
	                }
	                catch(NumberFormatException e1)
	                {
	                   // System.out.println(fileLevel2.getAbsolutePath());
	                    continue;
	                
	                }
	                }
	                br.close();
	                isr.close();
	                fis.close();
	                }
}
	              
			 //   }
			  
			//}
			
		//}
	
	   
	   

  

 //}
//}