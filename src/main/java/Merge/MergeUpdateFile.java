package Merge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import weibo4j.WeiboException;
import weibo4j.model.Status;

public class MergeUpdateFile {
	
	String originalFile = "." + File.separator + "data" + File.separator+ "updatelist" + File.separator + "needGetSinceId";
	String completeUpdate = "." + File.separator + "data" + File.separator+ "updatelist" + File.separator + "completeUpdate";
	String successFile = "." + File.separator + "data" + File.separator+ "updatelist" + File.separator + "success";
	String failFile = "." + File.separator + "data" + File.separator+ "updatelist" + File.separator + "fail";
	
	public void MergeFile() throws IOException, WeiboException, weibo4j.model.WeiboException{
		
		List<Status> statusList = new ArrayList<Status>();
		long maxId=0l;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		File[] flist = null;
		File dir = new File(completeUpdate);
		flist = dir.listFiles();
		
		File[] flist1 = null;
		File dir1 = new File(originalFile);
		flist1 = dir1.listFiles();
		
		for (File file : flist) {
			
			boolean flag=false;
			
			String[] info = file.getName().substring(0,file.getName().indexOf(".")).split("_");
			
			if(!info[3].equals("-1")&&!info[3].equals("0")&&(Float.valueOf(info[6])>Float.valueOf(info[5])||(Float.valueOf(info[6])/Float.valueOf(info[5]))>0.8)){
				
			final String uid=info[0];	
			
			for(File file1 : flist1){
				
				if(file1.getName().startsWith(uid)){
					
					fis = new FileInputStream(file1);
					isr = new InputStreamReader(fis);
					br = new BufferedReader(isr);
					flag=true;
					break;
				}
			
		}
	
			String line="";
			
			if(flag){
			while ((line = br.readLine()) != null)
			{
				if(line.startsWith("["))
				{
				statusList=Status.constructStatuses(line);
				if(statusList.size()>1){
				maxId = statusList.get(statusList.size()-1).getId();
				if(Long.valueOf(info[2])>maxId){
				write(completeUpdate+"\\"+file.getName(),line);
				}
				}}
				else{
					continue;
				}
				
			}
			
			String newFileName = successFile +"\\"+info[0]+"_status.txt";
			statusList.clear();
			
			fis.close();
			br.close();
       
			File des = new File(newFileName);
			file.renameTo(des);
			}
        }
			else{
				String newFileName = failFile +"\\"+file.getName();
				File des = new File(newFileName);
				file.renameTo(des);
			}
			
		}
		
	}
	
	static void write(String filepath, String str) {
		FileWriter fileWriter = null;
		BufferedWriter bw = null;
		try {
			fileWriter = new FileWriter(filepath, true);
			bw = new BufferedWriter(fileWriter);
			bw.append(str);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] arg) throws weibo4j.WeiboException,IOException, weibo4j.model.WeiboException {
          MergeUpdateFile merge = new MergeUpdateFile();
          merge.MergeFile();

}

}
