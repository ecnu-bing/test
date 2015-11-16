/**
 * WeiboCrawler2 
 * @date Jul 2, 2011
 * @author haixinma
 */
package Util;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable,Comparable<User>{
    public static final String eol = System.getProperty("line.separator");
    public String uid = null;
    public String uname = null;
    public String longitude = null;
    public String latitude = null;
    public String location = null;
    public String description = null;
    public String blog_url = null;
    public String domain_url = null;
    public String gender = null;
    public String followers_count = null;
    public String friends_count = null;
    public String statuses_count = null;
    public String favourites_count = null;
    public Date created_at = null;
    public String verified = null;
    public String timeZone = null;
    public String isProtected = null;
    
    public User()
    {
        
    }
    public User(String uid,String uname,
            String longitude,String latitude,String location,String description,
            String blog_url,String domain_url,String gender,String followers_count,
            String friends_count,String statuses_count,String favourites_count,
            Date created_at,String verified,String timeZone,String isProtected)
    {
        this.uid = uid;
        this.uname = uname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.description = description;
        this.blog_url = blog_url;
        this.domain_url = domain_url;
        this.gender = gender;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.statuses_count = statuses_count;
        this.favourites_count = favourites_count;
        this.created_at = created_at;
        this.verified = verified;
        this.timeZone = timeZone;
        this.isProtected = isProtected;
    }
    public int compareTo(User m2){
        return uid.compareTo(m2.uid);
    }

    @Override
	public boolean equals(Object o){
        return this.uid.equals(((User)o).uid);
    }

    @Override
	public int hashCode(){
        return this.uid.hashCode();
    }
    
    @Override
	public String toString(){
        return xmlString();
    }
    public void set(String uid,String uname,
            String longitude,String latitude,String location,String description,
            String blog_url,String domain_url,String gender,String followers_count,
            String friends_count,String statuses_count,String favourites_count,
            Date created_at,String verified,String timeZone,String isProtected)
    {
        this.uid = uid;
        this.uname = uname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.description = description;
        this.blog_url = blog_url;
        this.domain_url = domain_url;
        this.gender = gender;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.statuses_count = statuses_count;
        this.favourites_count = favourites_count;
        this.created_at = created_at;
        this.verified = verified;
        this.timeZone = timeZone;
        this.isProtected = isProtected;
    }
    private String xmlString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<user>");
        sb.append(eol);
        if (uid == null)
        {
            sb.append("<uid/>");
        }else{
            sb.append("<uid>");
            sb.append(uid);
            sb.append("</uid>");
        }
        sb.append(eol);
        if (uname == null)
        {
            sb.append("<uname/>");
        }else{
            sb.append("<uname>");
            sb.append(uname);
            sb.append("</uname>");
        }
        sb.append(eol);
        sb.append("<geoinfo>");
        sb.append(eol);
        if (longitude== null)
        {
            sb.append("<longitude/>");
        }else{
            sb.append("<longitude>");
            sb.append(longitude);
            sb.append("</longitude>");
        }
        sb.append(eol);
        if (latitude== null)
        {
            sb.append("<latitude/>");
        }else{
            sb.append("<latitude>");
            sb.append(latitude);
            sb.append("</latitude>");
        }
        sb.append(eol);
        if (location== null || location.equals(""))
        {
            sb.append("<location/>");
        }else{
            sb.append("<location>");
            sb.append(location);
            sb.append("</location>");
        }
        sb.append(eol);
        if (timeZone== null || timeZone.equals(""))
        {
            sb.append("<timeZone/>");
        }else{
            sb.append("<timeZone>");
            sb.append(timeZone);
            sb.append("</timeZone>");
        }
        sb.append(eol);
        sb.append("</geoinfo>");
        sb.append(eol);
        if (description == null || timeZone.equals(""))
        {
            sb.append("<description/>");
        }else{
            sb.append("<description>");
            sb.append(description);
            sb.append("</description>");
        }
        sb.append(eol);
        if (blog_url == null|| blog_url.equals("")||blog_url.equals("null"))
        {
            sb.append("<blog_url/>");
        }else{
            sb.append("<blog_url>");
            sb.append(blog_url);
            sb.append("</blog_url>");
        }
        sb.append(eol);
        if (domain_url == null || domain_url.equals("")||domain_url.equals("null"))
        {
            sb.append("<domain_url/>");
        }else{
            sb.append("<domain_url>");
            sb.append(domain_url);
            sb.append("</domain_url>");
        }
        sb.append(eol);
        if (gender == null || gender.equals(""))
        {
            sb.append("<gender/>");
        }else{
            sb.append("<gender>");
            sb.append(gender);
            sb.append("</gender>");
        }
        sb.append(eol);
        if (followers_count == null)
        {
            sb.append("<followers_count/>");
        }else{
            sb.append("<followers_count>");
            sb.append(followers_count);
            sb.append("</followers_count>");
        }
        sb.append(eol);
        if (friends_count == null)
        {
            sb.append("<friends_count/>");
        }else{
            sb.append("<friends_count>");
            sb.append(friends_count);
            sb.append("</friends_count>");
        }
        sb.append(eol);
        if (statuses_count == null)
        {
            sb.append("<statuses_count/>");
        }else{
            sb.append("<statuses_count>");
            sb.append(statuses_count);
            sb.append("</statuses_count>");
        }
        sb.append(eol);
        if (favourites_count == null)
        {
            sb.append("<favourites_count/>");
        }else{
            sb.append("<favourites_count>");
            sb.append(favourites_count);
            sb.append("</favourites_count>");
        }
        sb.append(eol);
        if (created_at == null)
        {
            sb.append("<created_at/>");
        }else{
            sb.append("<created_at>");
            sb.append(created_at);
            sb.append("</created_at>");
        }
        sb.append(eol);
        if (verified == null)
        {
            sb.append("<verified/>");
        }else{
            sb.append("<verified>");
            sb.append(verified);
            sb.append("</verified>");
        }
        sb.append(eol);
        if (isProtected == null)
        {
            sb.append("<isProtected/>");
        }else{
            sb.append("<isProtected>");
            sb.append(isProtected);
            sb.append("</isProtected>");
        }
        sb.append(eol);
        sb.append("</user>");
        sb.append(eol);
        return sb.toString();
    }
    public void clear()
    {
        this.uid = null;
        this.uname = null;
        this.longitude = null;
        this.latitude = null;
        this.location = null;
        this.description = null;
        this.domain_url = null;
        this.gender = null;
        this.followers_count = null;
        this.friends_count = null;
        this.statuses_count = null;
        this.favourites_count = null;
        this.created_at = null;
        this.verified = null;
        this.isProtected = null;
    }

}
