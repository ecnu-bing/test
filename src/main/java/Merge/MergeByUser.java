package Merge;

/**
 * WeiboCrawler2 
 * @date Jul 14, 2011
 * @author haixinma
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Util.Tool;


public class MergeByUser{

	private String NewDir =null;
	private String oldDir =null;
	private String successList =null;
	private String failList= null;
	private String toDir =null;
	List<String> SuccessList = new ArrayList<String>(); 
 
    public MergeByUser()
    {
        
    }
    public MergeByUser(String oldDir,String NewDir,String failList,String successList,String toDir)
    {
        this.NewDir=NewDir;
        this.oldDir=oldDir;
        this.successList=successList;
        this.failList=failList;
        this.toDir=toDir;
    }
    public static void main(String[] args) throws IOException
    {
    	MergeByUser mergeFile = new MergeByUser();
    	//MergeByuser mergeFile = new MergeByuser(args[0],args[1],args[2],args[3],args[4]);
        mergeFile.run();
    }
    public void run() throws IOException
    {
    	init();
        merge();
    	
    }
    public void init() throws IOException
    {
    	
    	SuccessList=ReadFile(successList,"GBK");
    			
    }
    
    private void merge() throws IOException
    {
    	File f = new File(oldDir);
    	File f1 = new File(NewDir);
		String[] fileList1 = f.list();
		
		List<String> writeList = new ArrayList<String>();
		List<String> writeList1 = new ArrayList<String>();
			
		for(String fileLevel1 : fileList1)
		{			
		   FileInputStream fis;
		   InputStreamReader isr;
		   BufferedReader br;
		
		   //System.out.println(fileLevel1);
           String line = null;
           final String uid=fileLevel1.substring(0,fileLevel1.indexOf("."));
           //System.out.println(uid);
	
            if(!SuccessList.contains(toDir + File.separator +fileLevel1)){

            	//找到新文件是否存在
            	String[] fns = f1.list(new FilenameFilter() {
        			public boolean accept(File dir, String name) {		
        			if (name.substring(0,name.indexOf("_")).equals(uid)) {
        			return true;
        			} else {
        			return false;
        			}
        			}
        			});
            	
            if(fns.length==0){
            	Tool.write(failList, fileLevel1);//如果没有在新爬的目录找到该用户，那么将其记录记在失败记录中
            	
            }
            else{
            	System.out.println(fns.length);
            	for(int i=0;i<fns.length;i++){
            		// System.out.println(NewDir + File.separator +fileLevel1);
            		
            	    writeList=ReadFile(NewDir + File.separator +fns[i],"GBK");
            		writeList1=ReadFile(oldDir + File.separator +fileLevel1,"GBK");
            		//System.out.println(writeList);
            		//System.out.println(writeList1);
          		    Tool.write(toDir + File.separator +fileLevel1, writeList, true, "GBK");
          		    Tool.write(toDir + File.separator +fileLevel1, writeList1, true, "GBK");
          		    Tool.write(successList, toDir + File.separator +fileLevel1);
            	}
                
               
            	
       		
              
            }
            	
           
           
            }
    //}
            writeList.clear();
         writeList1.clear();
		}
		
    }

    private List<String> ReadFile(String filePath,String encode) throws IOException{
    	 FileInputStream fis;
		 InputStreamReader isr;
		 BufferedReader br;
		 String line = null;
		   
		 List<String> readList = new ArrayList<String>();
		 
		 try {
		 fis = new FileInputStream(filePath);
         isr = new InputStreamReader(fis,encode);                      
         br = new BufferedReader(isr);
        
         while((line = br.readLine()) != null)
         {
         	//System.out.print(line);
				
			if(!readList.contains(line)){	
			
				readList.add(line);
				}
				
         
         	}
		 }catch (FileNotFoundException e) {
         
         // TODO Auto-generated catch block
			File myFile = new File(filePath);
			myFile.createNewFile();
     } catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         System.err.println(e.getMessage());
         e.printStackTrace();
     }  
     catch (IOException e) {
         // TODO Auto-generated catch block
         System.err.println(e.getMessage());
         e.printStackTrace();
     }
    	//System.out.print(readList);
		 return readList;
    }

}

