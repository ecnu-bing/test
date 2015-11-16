package myUtil;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/*
 * 将数据写入文件
 */
public class writer {

	private String url;//文件位置
	
	public writer(){
		
	}
	public writer(String url){
		this.url=url;
	}
	
	
	public void write(long[]ids){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url,true)));
			for(long id : ids){
				bw.write(String.valueOf(id));
				bw.write("\r\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} catch (IOException eio){
			eio.printStackTrace();
		}finally{
			try{
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
	}
	
	public void write(long[]ids,String sourceId){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url,true)));
			for(long id : ids){
				bw.write(sourceId+"\t"+String.valueOf(id));
				bw.write("\r\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} catch (IOException eio){
			eio.printStackTrace();
		}finally{
			try{
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
}
