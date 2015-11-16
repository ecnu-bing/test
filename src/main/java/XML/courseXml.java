package XML;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Util.Tool;











import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NodeList;


public class courseXml {
	
	protected String inputDir = null;
	protected String outputFile = null;
	SimpleDateFormat inputFormat = null;
	protected File[] flist = null;
	private boolean isInited = false;
	
	public courseXml(String inputDir,String outputFile)
    {
	    this.inputDir = inputDir;
	    this.outputFile = outputFile;	    
    }
	
	public courseXml()
    {
	        
    }
	
	public static void main(String[] args) throws Exception
	{
		courseXml rds = new courseXml();
		rds.run();
	}
	
	public void run() throws Exception
	{
	
		//Init();
		createXML();
	}
	
	public void Init()
	{
		
		File dir = new File(this.inputDir);
		if (!dir.exists() || !dir.isDirectory())
			return;
		flist = dir.listFiles();
		isInited = true;
	}
	
   public void createXML() throws IOException, weibo4j.model.WeiboException 
   {
	   Document doc = DocumentHelper.createDocument();
   	   Element root = doc.addElement("CourseData").addAttribute("class", "review");
   	   Element course = root.addElement("review");
   	   Element Cname = course.addElement("course");
   	   Element link = course.addElement("link");
   	   Element items = course.addElement("items");
   	
       Element item = items.addElement("item");
       Element name = item.addElement("name");
       Element time = item.addElement("time");
       Element general = item.addElement("general");
       Element detail = item.addElement("detail");
      
  
       
       
       Cname.setText("An Introduction to Interactive Programming in Python");
       link.setText("http://www.knollop.com/course/coursera-an-introduction-to-interactive-programming-in-python");
       name.setText("iain-geddes");
       time.setText("Wed Aug 21 22:21:47 UTC 2013");
       general.setText("Learning through play!");
       detail.setText("As other reviewers have noted, this is a really good introduction to Python and graphical programming. Whilst some exposure to Python or other programming languages can only help, the most important prerequisite is an open mind. Believe that you can do it and Scott, Joe and the team will demonstrate that you can.<br><br>How any course can take a student with no prior knowledge and have them developing an Asteroids type game within 8 weeks is still a mystery to me ... and yet this is exactly what was delivered. The pace is pretty fast and sometimes little details can be missed, but overall it's nothing short of remarkable - not to mention entertaining!");
       
       
       try {
    	   OutputFormat format1 = new OutputFormat("", true);
    	  // format1.setEncoding("gb2312");
    	   // 可以把System.out改为你要的流。
    	   FileOutputStream fos = new FileOutputStream(new File("E:\\平安\\test1.xml"));
    	   OutputStreamWriter outwriter = new OutputStreamWriter(fos);
    	   XMLWriter xmlWriter = new XMLWriter(new PrintWriter(outwriter), format1);
    	   xmlWriter.write(doc);
    	   xmlWriter.close();
    	  } catch (IOException e) {
    	   e.printStackTrace();
    	  }
   }
   
   
}
	 
	   
	   

  

 //}
//}