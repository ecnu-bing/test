package userinfo;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class FileProcesser {
	public void saveLog(String outPath,String content , String charset) throws IOException{
		File out = new File(outPath);
		if(!out.exists())
			out.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(
				outPath,true);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				fileOutputStream,charset);
		BufferedWriter bw = new BufferedWriter(outputStreamWriter);
		bw.append(content + "\n");
		bw.close();
	}
	public void save(String outPath,String content , String charset) throws IOException{
		File out = new File(outPath);
		if(!out.exists())
			out.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(
				outPath,false);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				fileOutputStream,charset);
		BufferedWriter bw = new BufferedWriter(outputStreamWriter);
		bw.append(content + "\n");
		bw.close();
	}
	public void saveXML(String outPath,Document dom,String charset) throws IOException, FileNotFoundException{
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(charset);
		XMLWriter writer = new XMLWriter(new FileOutputStream(outPath),
				format);// outputPath + num + ".xml"
		writer.write(dom);
		writer.flush();
	}
}
