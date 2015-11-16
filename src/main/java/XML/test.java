package XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Util.Tool;

public class test {
	public static void main(String[] args) throws Exception
	{
		Map<String,String>map=new HashMap<String,String>();
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		File f=new File("X:\\平安项目数据\\门户网站\\网易公司公告\\merge1");
      
       File[]files=f.listFiles();
        int count=1;
       for(File file:files){
        	fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf-8");
            br = new BufferedReader(isr);
            String line="";
        	
            while((line = br.readLine()) != null )
            {
            	if(line.startsWith("</")||!line.contains("</")){
            		Tool.write("X:\\平安项目数据\\门户网站\\网易公司公告\\merge2\\"+file.getName(),line,true,"utf-8");
            	}else{
            		String start=line.substring(0,line.indexOf(">")+1);
            		String end=line.substring(line.lastIndexOf("</"));
            		String s=line.substring(line.indexOf(">")+1,line.lastIndexOf("</"));
            		s=s.replaceAll("&(.*?);","");
            		s=s.replaceAll("&","&amp;");
            		s=s.replaceAll("<","&lt;");
            		s=s.replaceAll(">","&gt;");
            		s=start+s+end;
            		Tool.write("X:\\平安项目数据\\门户网站\\网易公司公告\\merge2\\"+file.getName(), s,true,"utf-8");
            	}
            }
       }
        	/*fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf-8");
            br = new BufferedReader(isr);
            String id="d.003000000.130319.1."+count;
            System.out.println(id);
            String line="";
            Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "<metadatas version = \"0.4\" category = \"set\" count = \"100\">",true,"utf-8");
            Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "<metadata id = \""+id+"\">",true,"utf-8");
            while((line = br.readLine()) != null )
            {
            	if(line.contains("xml")||line.contains("class=\"data\"")){
            		continue;
            	}
            	if(line.contains("d.300.")){
            		System.out.println("<id>"+id+"</id>");
            		Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "<id>"+id+"</id>",true,"utf-8");
            	}
            	else if(line.contains("description")){
            		String des=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
            		Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "<description><abstract>"+des+"</abstract></description>",true,"utf-8");
            	}
            	else if(line.contains("</metadata>")){
            		Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "<size>1</size>",true,"utf-8");
            		Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", line,true,"utf-8");
            	}
            	else {
            		Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", line,true,"utf-8");
            	}
            }
            
            Tool.write("I:\\RDS_Weibo\\status\\new_data\\"+id+".xml", "</metadatas>",true,"utf-8");
            count++;*/
        //}
        /*Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
       	  java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
       	  
       	  Tool.write("F:\\chanyedai\\data\\result\\merge.txt", entry.getKey()+"");
       	
      	  }*/
        
	}

}
