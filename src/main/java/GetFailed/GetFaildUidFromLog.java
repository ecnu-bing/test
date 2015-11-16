/**
 * WeiboCrawler2 
 * @date Jul 1, 2011
 * @author haixinma
 */
package GetFailed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class GetFaildUidFromLog {
    private String inputDir = "."+File.separator+"logs";
    private String outputPath = "."+File.separator+"data"+File.separator+"uid_needGet";
    private String dataDir = "."+File.separator+"data"+File.separator+"uid";
    public GetFaildUidFromLog()
    {
        
    }
    public static void main(String[] args)
    {
        GetFaildUidFromLog getFaildlist = new GetFaildUidFromLog();
        getFaildlist.run();
    }
    public void run()
    {
        analysisLog();
    }
    private void analysisLog()
    {
        File filedir = new File(inputDir);
        String[] fileList = filedir.list();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        Set<Long> uidSet = new HashSet<Long>();
        String line = null;
        Long uid = null;
        int getCount = 0;
        int realCount = 0;
        String deleteFilepath = null;
        try {
            for(String file:fileList)
            {
                fis = new FileInputStream(inputDir+File.separator+file);
                isr = new InputStreamReader(fis,"GBK");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    if(line.startsWith("missing"))
                    {
                        uid = Long.parseLong(line.substring(line.indexOf(":")+2));
                        if(!uidSet.contains(uid))
                        {
                            uidSet.add(uid);
                            deleteFilepath = dataDir + File.separator + file.substring(0,file.indexOf("runninglog"))+File.separator+uid+"_status.txt";
                            System.out.println(deleteFilepath);
                            deleteFile(new File(deleteFilepath));
                        }
                    }  
                }
                br.close();
                isr.close();
                fis.close();
                deleteFile(new File(inputDir+File.separator+file));
            }
            write(outputPath, uidSet);
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
    }
    private void write(String filepath,Set<Long> list)
    {
        FileWriter fileWriter = null;
        BufferedWriter bw= null;
        try {
            fileWriter = new FileWriter(filepath,true);
            bw = new BufferedWriter(fileWriter);
            for(Long s:list)
            {
                bw.append(s+"");
                bw.newLine();
            }
            bw.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    static void deleteFile(File file)
    {
        try
        {
            if (file.exists())
            {
                file.delete();
                System.out.println("delete："+file.getName());
            }
        }
        catch (Exception e)
        {
            System.out.println("delete failed："+file.getName());
            e.printStackTrace();
        }
    }

}
