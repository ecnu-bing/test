package XML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import Util.Tool;

/**
 * dom4j框架学习： 读取并解析xml
 * 
 * 
 */
public class TestDom
{
    public static void main(String[] args) throws Exception
    {
        SAXReader saxReader = new SAXReader();
        
        File file =new File("X:\\平安项目数据\\门户网站\\网易公司公告\\merge2");
        int  count1=66573;
        
     
        File[]files=file.listFiles();
        
        for(File f:files){
        Document document = saxReader.read(f);

        // 获取根元素

        Element root = document.getRootElement();
        System.out.println(root.getName());
        }
       // Tool.write("D:\\xml1\\"+f.getName(), document.asXML(),true,"utf-8");
        
        // 获取所有子元素
        /*List<Element> childList = root.elements();
        System.out.println("total child count: " + childList.get(2));*/

                      
        /*List<Element> childList2 = root.elements("news");
        for(int i=0;i<childList2.size();i++){
        	for (Iterator iter = childList2.listIterator(); iter.hasNext();)
            {
        		 Document doc = DocumentHelper.createDocument();
        	    	Element createroot = doc.addElement("metadatas").addAttribute("version", "0.4").addAttribute("category","data").addAttribute("count", "1");
        	    
        		 Element e = (Element) iter.next();
        		 Element metadata = createroot.addElement("metadata").addAttribute("id", "i.003000000.140527."+count1+".1");
        		 Element title=metadata.addElement("title");
        		 title.setText(e.elementText("title"));
        		 Element keyword=metadata.addElement("keyword");
        		 keyword.setText(e.elementText("keyword"));
        		 Element description=metadata.addElement("description");
        		 Element abstract0=description.addElement("abstract");
        		 abstract0.setText(e.elementText("text"));
        		 Element time=description.addElement("time");
        		 time.setText(e.elementText("info"));
        		 Element type=metadata.addElement("type");
        		 type.setText("Text");
        		 Element format=metadata.addElement("format");
                 format.setText("xml");
                 Element size=metadata.addElement("size");
                 
                 Element date=metadata.addElement("date");
                 Element mdate=date.addElement("mdate");
                 mdate.setText("2014-05-27T22:48:11+8:00");
                 Element identifier=metadata.addElement("identifier").addAttribute("platform", "local");
                 Element path=identifier.addElement("path");
                 path.setText("/58.198.176.83/zhangqy/21cnnews/21cnnews_part1.xml");
                 Element id=identifier.addElement("id");
                 id.setText("i.003000000.140527."+count1+".1");
                 Element source=metadata.addElement("source");
                 source.setText(e.elementText("url"));
              
                 size.setText(String.valueOf(doc.asXML().getBytes().length/1024));
                 try {
     	       OutputFormat format1 = new OutputFormat("  ", true);
     	   format1.setEncoding("utf-8");
     	   // 可以把System.out改为你要的流。
     	       
     	       if(count1>76573){
     	   FileOutputStream fos = new FileOutputStream(new File("d:\\part2\\"+"i.003000000.140527."+count1+".1"+".xml"));
     	   OutputStreamWriter outwriter = new OutputStreamWriter(fos);
     	   XMLWriter xmlWriter = new XMLWriter(new PrintWriter(outwriter), format1);
     	   xmlWriter.write(doc);
     	   xmlWriter.close();
     	       }
     	  } catch (IOException e1) {
     	   e1.printStackTrace();
     	  }
              
                 count1++;
                 
                 if(count1>86574){
                	 return;
                 }
            }
        	
        }
           System.out.print(count1);
      
        // 迭代输出
        /*for (Iterator iter = root.elementIterator(); iter.hasNext();)
        {
            Element e = (Element) iter.next();
           Tool.write("X:\\平安项目数据\\上市公司\\id", e.elementText("id"));

        }

        System.out.println("用DOMReader-----------------------");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        // 注意要用完整类名
        org.w3c.dom.Document document2 = db.parse(new File("students.xml "));

        DOMReader domReader = new DOMReader();

        // 将JAXP的Document转换为dom4j的Document
        Document document3 = domReader.read(document2);

        Element rootElement = document3.getRootElement();

        System.out.println("Root: " + rootElement.getName());

    }*/
    }
}
