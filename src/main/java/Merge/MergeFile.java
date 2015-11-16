package Merge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Util.Tool;


public class MergeFile {
    protected String inputDir = null;
    protected String outputFile = null;
    protected File[] inputDirList = null;
    protected ArrayList<String> completeList = new ArrayList<String>();
    protected Long fileCount = 0l;

    public static void main(String[] args)
    {
        MergeFile mergeFile = new MergeFile(args[0],args[1]);
        //MergeResult mergeResult = new MergeResult();
        mergeFile.run();
    }
    
    public MergeFile(String inputFile,String outputFile)
    {
        this.inputDir = inputFile;
        this.outputFile = outputFile;
    }
    public MergeFile()
    {
    }
    public void run()
    {
        if(!init())
        {
            return;
        }
        for(File file:this.inputDirList)
        {
            merge(file);
        }
        
    }
    
    protected Boolean init()
    {
        readCompleteFile();  
        if(new File(this.inputDir).exists())
        {
            this.inputDirList = new File(this.inputDir).listFiles();
            return true;
        }else
        {
            System.out.println("the input file: "+this.inputDir +" does not exist!");
            return false;
        }
    }
    protected void readCompleteFile()
    {
        File file = new File(this.outputFile+"_complete");
        if(!file.exists())
        {
           return; 
        }
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf8");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                if (!completeList.contains(line))
                {
                    completeList.add(line);
                }
            }
            fis.close();
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    protected void merge(File file)
    {
        //遍历 2 层目录
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            System.out.println(file.getName());
            if(completeList.contains(file.getAbsolutePath()))
            {
                return;
            }
            File[] fileList = file.listFiles();
            
            for(File fileLevel1: fileList)
            {
                
                //File[] fileList2 = fileLevel1.listFiles();
                //for(File fileLevel2:fileList2)
                {
                    fileCount++;
                    fis = new FileInputStream(fileLevel1);
                    isr = new InputStreamReader(fis,"GBK");
                    br = new BufferedReader(isr);
                    while((line = br.readLine()) != null)
                    {
                        resultList.add(line);
                    }
                    br.close();                                                                             
                    isr.close(); 
                    fis.close();
                }
                
            }
            Tool.write(this.outputFile, resultList,true,"utf8");
            Tool.write(this.outputFile+"_complete", file.getAbsolutePath(),true,"utf8");
            resultList.clear();
        }catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
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
        System.out.println("file count: "+this.fileCount);
    }
}
