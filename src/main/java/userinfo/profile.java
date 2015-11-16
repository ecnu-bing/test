package userinfo;

import java.util.TreeMap;



public class profile {
	public String userID = "";
	public TreeMap<String,String> basicInfo = new TreeMap<String,String>();
	public TreeMap<String,String> eduInfo = new TreeMap<String,String>();
	public TreeMap<String,String> workInfo = new TreeMap<String,String>();
	public TreeMap<String,String> otherInfo = new TreeMap<String,String>();
	
	public profile(){
		
	}
	public profile(String userID){
		this.userID = userID;
	}
	public void setUserID(String userID){
		this.userID = userID;
	}
	public String getUserID(){
		return this.userID;
	}
	public TreeMap<String,String> getBasicInfo(){
		return this.basicInfo;
	}
	public TreeMap<String,String> getEduInfo(){
		return this.eduInfo;
	}
	public TreeMap<String,String> getWorkInfo(){
		return this.workInfo;
	}
	public TreeMap<String,String> getOtherInfo(){
		return this.otherInfo;
	}
	
	public void clear()
	{
	    this.basicInfo.clear();
	    this.eduInfo.clear();
	    this.workInfo.clear();
	    this.otherInfo.clear();
	}
}

