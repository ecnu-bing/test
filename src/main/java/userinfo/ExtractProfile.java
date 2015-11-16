package userinfo;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import Util.Tool;

public class ExtractProfile {
    public profile profile = new profile();

    public static void main(String[] args) throws IOException
    {
        ExtractProfile extract = new ExtractProfile();
        extract.extract();
    }
    
    public void extract() throws IOException
    {
        Map<String,Integer> shortCountMap = new HashMap<String,Integer>();
        TreeMap<String,String> basicInfo;
        TreeMap<String,String> eduInfo;
        TreeMap<String,String> workInfo;
        ArrayList<String> recordList = new ArrayList<String>();
        Map<String,String> UserMap = new HashMap<String,String>();
        
        FileInputStream fis0;
        InputStreamReader isr0;
        BufferedReader br0;
        String line0 = null;
        fis0 = new FileInputStream(new File("F:\\1886188037\\total.txt"));
        isr0 = new InputStreamReader(fis0,"GBK");
        br0 = new BufferedReader(isr0);
        while((line0=br0.readLine())!=null){
        	UserMap.put(line0, "");
        }
        
        
        
        
        String basic = "";
        String edu = "";
        String work = "";
        String other = "";
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        int userCount = 0;
        int sum = 0;
        
        try { 
            File[] flist = new File("Z:\\userInfo\\UTF-8").listFiles();
            for(File file: flist)
            {
                System.out.println(file.getName());
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis,"utf-8");
                br = new BufferedReader(isr);
                while((line = br.readLine()) != null)
                {
                	Tool.write("Z:\\userInfo\\HTML2\\"+file.getName()+".txt",line,true,"gbk");
                    /*int basicStart = line.indexOf("【其他信息】<br/>互联网:http://weibo.com/u/") + ("【其他信息】<br/>互联网:http://weibo.com/u/").length();
                    if(line.length() < basicStart)
                    {
                        continue;
                    }
                    //System.out.println(line);
                    String subline;
                    subline = line.substring(basicStart);
                    int basicEnd = subline.indexOf("<br/>手机版");
                    if (basicStart > -1 && basicEnd > -1) {
                        basic = subline.substring(0, basicEnd);
                        if(UserMap.containsKey(basic)){
                        	Tool.write("F:\\1886188037\\userinfo1.txt", line);
                        }
                    }else
                    {
                        continue;
                    }*/

                    
                    
                    /*int eduStart = line.indexOf("【学习经历】") + ("【学习经历】").length();
                    int eduEnd = line.indexOf("<br/>他的校友:");
                    if (eduStart > -1 && eduEnd > -1) {
                        edu = line.substring(eduStart, eduEnd);
                        parseEdu(edu);
                    }

                    int workStart = line.indexOf("【工作经历】") + ("【工作经历】").length();
                    int workEnd = line.indexOf("<br/>他的同事:");
                    if (workStart > -1 && workEnd > -1) {
                        work = line.substring(workStart, workEnd);
                        parseWork(work);
                    }

                    int otherStart = line.indexOf("【其他信息】") + ("【其他信息】").length();
                    int otherEnd = line.indexOf("<br/><a href=\"album.php?");
                    if (otherStart > -1 && otherEnd > -1) {
                        other = line.substring(otherStart, otherEnd);
                        parseOther(other);
                    }
                    
                    String record = "";
                    basicInfo = profile.getBasicInfo();
                    eduInfo = profile.getEduInfo();
                    workInfo = profile.getWorkInfo();
                    if(basicInfo.size()<=0)
                    {
                        continue;
                    }
                    ++userCount;
                    sum = 0;
                    if(basicInfo.containsKey("地区"))
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    if(basicInfo.containsKey("生日"))
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    if(basicInfo.containsKey("简介"))
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    if(basicInfo.containsKey("标签"))
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    if(eduInfo.size()>0)
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    if(workInfo.size()>0)
                    {
                        record += "1"+"\t";
                        ++sum;
                    }else
                    {
                        record += "0"+"\t";
                    }
                    record+=sum;
                    if(shortCountMap.containsKey(record))
                    {
                        int temp = shortCountMap.get(record);
                        shortCountMap.put(record, ++temp);
                    }else
                    {
                        shortCountMap.put(record, new Integer(1));
                    }
                    profile.clear();*/
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
        //Tool.write(".\\data\\result",recordList,true,"utf8");
        Iterator iterator = shortCountMap.entrySet().iterator();
         while(iterator.hasNext())
        {
            Entry<String,Integer> entry = (Entry<String,Integer>)iterator.next();
            String key = entry.getKey();
            int value = entry.getValue();
            System.out.println(key+"\t"+value);
        }
        System.out.println(userCount);
    }
    public void parseBasic(String basic) {
        String[] items = basic.split("<br/>");
        for (String s : items) {
            String[] item = s.split(":");
            if (item.length >= 2) {
                profile.basicInfo.put(item[0].trim(), item[1].replaceAll(
                        "<a([^<]*)>([^<]*)</a>", "$2").replaceAll("&nbsp", "")
                        .replaceAll("更多&gt;&gt;", "").trim());
            }
        }
    }

    public void parseEdu(String edu) {
        String[] items = edu.split("<br/>");
        for (String s : items) {
            s = s.replaceAll("\\s*·<a([^<]*)>([^<]*)</a>(.*)", "$2$3")
                    .replaceAll("&nbsp\\s*", "").replaceAll("--", "");
            if (s.contains(";")) {
                String[] item = s.split(";");
                profile.eduInfo.put(item[0].trim(), item[1].trim());
            } else if (s.trim().length() > 0) {
                profile.eduInfo.put(s.trim(), "");
            }
        }
    }

    public void parseWork(String work) {
        String[] items = work.split("<br/>");
        for (String s : items) {
            s = s.replaceAll("\\s*·<a([^<]*)>([^<]*)</a>(.*)", "$2$3")
                    .replaceAll("&nbsp\\s*", "").replaceAll("--", "");
            if (s.contains(";")) {
                String[] item = s.split(";");
                profile.workInfo.put(item[0].trim(), item[1].trim());
            } else if (s.trim().length() > 0) {
                profile.workInfo.put(s.trim(), "");
            }
        }
    }

    public void parseOther(String other) {
        String[] items = other.split("<br/>");
        for (String s : items) {
            if (s.contains(":")) {
                int i = s.indexOf(":");
                profile.otherInfo.put(s.substring(0, i).trim(), s.substring(
                        i + 1).trim());
            }
        }
    }

}

