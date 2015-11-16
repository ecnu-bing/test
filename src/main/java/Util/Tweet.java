/**
 * WeiboCrawler2 
 * @date Jul 2, 2011
 * @author haixinma
 */
package Util;

import java.io.Serializable;
import java.util.Date;

public class Tweet implements Serializable,Comparable<Tweet>{
    public static final String eol = System.getProperty("line.separator");
    public String uid= null;
    public String mid = null;
    public String content = null;
    public String forwardID = null;
    public Date created_at = null;
    public String source = null;
    public String truncated = null;
 
    public Tweet()
    {
        
    }
    public Tweet(String uid,String mid,String content,String forwardID,
            Date created_at,String source,String truncated)
    {
        this.uid = uid;
        this.mid = mid;
        this.content = content;
        this.forwardID = forwardID;
        this.created_at = created_at;
        this.source = source;
        this.truncated = truncated;
    }

    public int compareTo(Tweet t2){
        return mid.compareTo(t2.mid);
    }

    @Override
	public boolean equals(Object o){
        return this.mid.equals(((Tweet)o).mid);
    }

    @Override
	public int hashCode(){
        return this.mid.hashCode();
    }
    
    @Override
	public String toString(){
        return xmlString();
    }
    public void set(String uid,String mid,String content,String forwardID,
            Date created_at,String source,String truncated)
    {
        this.uid = uid;
        this.mid = mid;
        this.content = content;
        this.forwardID = forwardID;
        this.created_at = created_at;
        this.source = source;
        this.truncated = truncated;
    }
    private String xmlString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<message>");
        sb.append(eol);
        if(mid == null)
        {
            sb.append("<mid/>");
        }
        else {
            sb.append("<mid>");
            sb.append(mid);
            sb.append("</mid>");
        }
        sb.append(eol);
        if(uid == null)
        {
            sb.append("<uid/>");
        }
        else {
            sb.append("<uid>");
            sb.append(uid);
            sb.append("</uid>");
        }
        sb.append(eol);
        if(content == null || content.equals(""))
        {
            sb.append("<content/>");
        }
        else {
            sb.append("<content>");
            sb.append(content);
            sb.append("</content>");
        }
        sb.append(eol);
        if(forwardID == null || forwardID.equals(""))
        {
            sb.append("<forwardID/>");
        }
        else {
            sb.append("<forwardID>");
            sb.append(forwardID);
            sb.append("</forwardID>");
        }
        sb.append(eol);
        if(created_at == null)
        {
            sb.append("<created_at/>");
        }
        else {
            sb.append("<created_at>");
            sb.append(created_at);
            sb.append("</created_at>");
        }
        sb.append(eol);
        if(source == null)
        {
            sb.append("<source/>");
        }
        else {
            sb.append("<source>");
            sb.append(source);
            sb.append("</source>");
        }
        sb.append(eol);
        if(truncated == null)
        {
            sb.append("<truncated/>");
        }
        else {
            sb.append("<truncated>");
            sb.append(truncated);
            sb.append("</truncated>");
        }
        sb.append(eol);
        sb.append("</message>");
        sb.append(eol);
        return sb.toString();
    }
    public void clear()
    {
        this.mid = null;
        this.uid = null;
        this.content = null;
        this.forwardID = null;
        this.created_at = null;
        this.source = null;
        this.truncated = null;
    }
}
