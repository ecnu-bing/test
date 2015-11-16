package Util;

public class MyUser {
	protected String uname = null;
	protected int statusesCount = 0;
	protected int friendsCount = 0;
	protected int followersCount = 0;
	protected int favouritesCount = 0;
	protected boolean isV = false;
	protected String address = null;
	protected Long createdAt = null;
	protected String gender;
	public MyUser()
	{
		
	}
	public MyUser(String uname,int statusesCount,int friendsCount,int followersCount,
			int favouritesCount,boolean isV,String address,Long createdAt,
			String gender)
	{
		this.uname = uname;
		this.statusesCount = statusesCount;
		this.friendsCount = friendsCount;
		this.followersCount = followersCount;
		this.favouritesCount = favouritesCount;
		this.isV = isV;
		this.address = address;
		this.createdAt = createdAt;
		this.gender = gender;
	}
	public String getUname()
	{
		return this.uname;
	}
	public int getStatusesCount()
	{
		return this.statusesCount;
	}
	public int getFriendsCount()
	{
		return this.friendsCount;
	}
	public int getFollowersCount()
	{
		return this.followersCount;
	}
	public int getFavouritesCount()
	{
		return this.getFavouritesCount();
	}
	public boolean getIsV()
	{
		return this.isV;
	}
	public String getAddress()
	{
		return this.address;
	}
	public Long getCreatedAt()
	{
		return this.createdAt;
	}
	public String getGender()
	{
		return this.gender;
	}
	public void setUname(String uname)
	{
		this.uname = uname;
	}
	public void setStatusesCount(int statusesCount)
	{
		this.statusesCount = statusesCount;
	}
	public void setFriendsCount(int friendsCount)
	{
		this.friendsCount = friendsCount;
	}
	public void setFollowersCount(int followersCount)
	{
		this.followersCount = followersCount;
	}
	public void setFavouritesCount(int favouritesCount)
	{
		this.favouritesCount = favouritesCount;
	}
	public void setIsV(boolean isV)
	{
		this.isV = isV;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public void setCreatedAt(long createdAt)
	{
		this.createdAt = createdAt;
	}
	public void setGender(String gender)
	{
		this.gender = gender;
	}
	
}
