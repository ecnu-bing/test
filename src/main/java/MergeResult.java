
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Util.Tool;

public class MergeResult {
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    public static void main(String[] args)
    {
        /*MergeResult mergeResult = new MergeResult();
        mergeResult.merge2Day();*/
    }
    public MergeResult()
    {
        
    }
    public List<String> readFile(String inputDir)
    {
        List<String> dataList = new ArrayList<String>();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        File[] flist = new File(inputDir).listFiles();
        try {
            for(File f:flist)
            {
                fis = new FileInputStream(f);
                isr = new InputStreamReader(fis,"utf8");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                    dataList.add(line);
                }
                br.close();
                isr.close();
                fis.close();
            }
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
        return dataList;
    }
    public void merge2Day()
    {
        Map<String,Integer> timeCountMap = new HashMap<String,Integer>();
        List<String> dataList = readFile("Z:\\code\\data\\motionwordStatis20130112\\TimeTweetCount");
        try {
            for(String data:dataList)
            {
                String[] list = data.split("\t");
                if(list.length>=2)
                {
                    String time = inputFormat.format(inputFormat.parse(list[0]));
                    int count = Integer.parseInt(list[1]);
                    if(timeCountMap.containsKey(time))
                    {
                        int temp = timeCountMap.get(time);
                        temp += count;
                        timeCountMap.put(time, count);
                    }else
                    {
                        timeCountMap.put(time, new Integer(count));
                    }
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //write to file
        List<String> resultList = new ArrayList<String>();
        Iterator iterator = timeCountMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<String,Integer> entry =  (Entry<String, Integer>) iterator.next();
            String key = entry.getKey();
            int value = entry.getValue();
            resultList.add(key+"\t"+value);
        }
        Collections.sort(resultList);
        Tool.write(".\\data\\motion\\day.txt", resultList,true,"utf8");
    }
    public void merge2Week()
    {
        
    }
    public void merge2Month()
    {
        
    }
}
