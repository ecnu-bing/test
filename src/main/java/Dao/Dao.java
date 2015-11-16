package Dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Dao extends BaseDao
{

	private static Dao dao;

	static
	{
		dao = new Dao();
	}

	public static Dao getInstance()
	{
		return dao;
	}

	// tb_user
	public Vector sUser()
	{
		return selectSomeNote("select * from users");
	}

	public Vector sVoteNames(int pro_id)
	{
		return selectSomeValue("select vote_option from vote where program_id='"
				+ pro_id + "'");
	}

	// -------------------------ProjectInforFrame----------------------------------
	public Vector sProgramById(int pro_id)
	{
		return selectOnlyNote("select * from program where program_id='"
				+ pro_id + "'");
	}

	public Vector sProgramByUserId(int user_id)
	{

		return selectSomeNote("with pni(program_id,program_name,user_identity) as( "
				+ "select  DISTINCT program.program_id, program.program_name, user_program.user_identity "
				+ "from user_program , program ,users where user_program.user_id = "
				+ user_id
				+ "and user_program.program_id= program.program_id)"
				+

				"select pni.program_id, pni.program_name, pni.user_identity, u.name "
				+ "from pni, user_program as up ,users as u "
				+ "where pni.program_id = up.program_id and "
				+ "up.user_identity = 0 and " + "u.user_id = up.user_id");

		// return
		// selectSomeNote("select  DISTINCT program.program_id, program.program_name, user_program.user_identity"+
		// "from user_program , program ,users where user_program.user_id ="+
		// user_id+
		// "and user_program.program_id= program.program_id");
	}

	public String sTeacherByID(int t_id)
	{

		Object object = selectOnlyValue("select teacher_name from teacher where teacher_id ="
				+ t_id);
		if (object == null)
		{
			return "none";
		}
		else
		{
			return object.toString();
		}
	}

	public String sDepartmentByID(int p_id)
	{
		Object object = selectOnlyValue("select department_name from department where department_id = "
				+ p_id);
		if (object == null)
		{
			return "none";
		}
		else
		{
			return object.toString();
		}
	}

	public String sTypeByID(int type_id)
	{
		Object object = selectOnlyValue("select type_name from type where type_id = "
				+ type_id);
		if (object == null)
		{
			return "none";
		}
		else
		{
			return object.toString();
		}
	}

	public Vector sDepartmentList()
	{
		return selectSomeValue("select department_name from department");
	}

	public Vector sTypeList()
	{
		return selectSomeValue("select type_name from type");
	}

	public String sManagerByPid(int p_id)
	{

		Object object = selectOnlyValue("select users.name from users, user_program where users.user_id = user_program.user_id and user_program.user_identity = 0 and  user_program.program_id = "
				+ p_id);
		if (object == null)
		{
			return "none";
		}
		else
		{
			return object.toString();
		}
	}

	public boolean uUpdateProject(int p_id, String name, String description,
			String start, String finish, int teacher_id, int de_id, int type_id)
	{
		return longHaul("update program set program_name='" + name
				+ "', program_descript ='" + description + "', start_time = '"
				+ start + "',finish_time = '" + finish + "'"
				+ ",teacher_id = '" + teacher_id + "',department_id = '"
				+ de_id + "',type_id = '" + type_id + "' where program_id="
				+ p_id);
	}

	public boolean uUpdateManager(int Nuser_id, int Ouser_id, int p_id)
	{

		boolean assign = longHaul("update user_program set user_identity='0' where user_id ="
				+ Nuser_id + " and program_id = " + p_id);
		boolean cancel = longHaul("update user_program set user_identity='1' where user_id ="
				+ Ouser_id + " and program_id = " + p_id);

		return assign && cancel;

	}

	public String sTeacherIdByName(String name)
	{

		Object object = selectOnlyValue("select teacher_id from teacher where teacher_name ='"
				+ name + "'");
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();

		}
	}

	public String sManagerIdByName(String name)
	{

		Object object = selectOnlyValue("select user_id from users where name ='"
				+ name + "'");
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();

		}
	}

	public String sProjectIdByName(String name, String description,
			String start, String finish, int teacher_id, int de_id, int type_id)
	{

		Object object = selectOnlyValue("select program_id from program where program_name='"
				+ name
				+ "' and program_descript = '"
				+ description
				+ "' and start_time = '"
				+ start
				+ "' and finish_time = '"
				+ finish
				+ "' and teacher_id = "
				+ teacher_id
				+ "and department_id = " + de_id + "and type_id = " + type_id);
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();

		}
	}

	// /-------------------------------------------TeamFrame-------------------------------
	public Vector getUserByPid(Integer proID)
	{
		String sql = "select user_id, name from users where user_id in(select user_id from user_program where program_id ="
				+ proID + ")";

		Vector vector = selectSomeNote(sql);

		return vector;
	}

	// --------------------------------------RegisterUserFrame-----------------------------------------------
	public boolean iInsertUser(String name, String sex, String stunum,
			String department, String grade, String classno, String phone,
			String email, String code)
	{
		return longHaul("insert into users values('" + name + "','" + grade
				+ "','" + classno + "','" + department + "','" + phone + "','"
				+ email + "','" + code + "','" + stunum + "','" + sex + "')");
	}

	// -----------------------------------NewProject-------------------------------
	public boolean iInsertProject(String name, String description,
			String start, String finish, int teacher_id, int de_id, int type_id)
	{
		return longHaul("insert into program values('" + name + "','"
				+ description + "','" + start + "','" + finish + "',"
				+ teacher_id + "," + de_id + "," + type_id + ")");
	}

	public boolean iInsertManager(int Nuser_id, int p_id)
	{

		boolean assign = longHaul("insert into user_program values('"
				+ Nuser_id + "','" + p_id + "','0')");

		return assign;

	}

	// -----------------------------------ProjectInvitation-------------------------------
	public Vector sOneProjectById(int pid)
	{
		return selectOnlyNote("select p.program_id,p.program_name,p.program_descript,p.start_time,p.finish_time,t.teacher_name,d.department_name,tp.type_name"
				+ " from program as p, teacher as t,department as d,type as tp where p.program_id="
				+ pid
				+ " and p.department_id = d.department_id and p.teacher_id = t.teacher_id and p.type_id = tp.type_id");
	}

	public boolean acceptInvitation(int user_id, int pid)
	{
		return longHaul("update user_program set user_identity=1 where user_id ="
				+ user_id + " and program_id = " + pid);

	}

	public boolean refuseInvitation(int user_id, int pid)
	{
		return longHaul("delete from user_program where user_identity=2 and user_id ="
				+ user_id + " and program_id = " + pid);

	}

	// -------------------------------------------DiscussFrame-------------------------------
	public Vector<String> GetGList(Integer proID)
	{
		String sql = "select name from users where user_id in(select user_id from user_program where program_id ="
				+ proID + ")";

		Vector<String> vector = selectSomeValue(sql);
		for (int i = 0; i < vector.size(); i++)
			vector.set(i, vector.get(i).trim());

		return vector;
	}

	// ---------------------------------------Plan---------------------------------------------

	public int sTimeSche(int proID)
	{
		long st = ((Timestamp) select(
				"SELECT start_time FROM program WHERE program_id = " + proID)
				.get(0).get(0)).getTime();
		long ed = ((Timestamp) select(
				"SELECT finish_time FROM program WHERE program_id = " + proID)
				.get(0).get(0)).getTime();
		// System.out.println(st + " " + ed + " " + new Date().getTime());
		if (st == ed)
			return 100;
		double ret = (new Date().getTime() - st) * 100 / (ed - st);
		if (ret < 0)
			return 0;
		if (ret > 100)
			return 100;
		return (int) ret;
	}

	public int sProSche(int proID)
	{
		int total = (Integer) selectOne(
				"SELECT COUNT(program_id) FROM proplan WHERE program_id = "
						+ proID).get(0);
		int ach = (Integer) selectOne(
				"SELECT COUNT(program_id) FROM proplan WHERE program_id = "
						+ proID + " AND achieve = 1").get(0);
		return (int) (ach * 100.0 / total);
	}

	/*
	 * public static void main(String avrg[])
	 * {
	 * Dao dao = new Dao().getInstance();
	 * System.out.println(dao.sTimeSche(1));
	 * }
	 */

	// ---------------------------------------Vote---------------------------------------------
	public boolean iInsertVote(int pro_id, int num, String choose)
	{
		String sql = "INSERT INTO vote VALUES(" + pro_id + "," + num + ", '"
				+ choose + "', 0)";
		return longHaul(sql);
	}

	public Vector sVoteByPid(int pro_id)
	{
		String sql = "select vote_id,vote_option_num,vote_option, state from vote where program_id ="
				+ pro_id;

		return selectSomeNote(sql);
	}

	public String sCountOfVote(int vote_id)
	{
		Object object = selectOnlyValue("select count(*) from vote_to where vote_id ="
				+ vote_id);
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();
		}
	}

	public String sCountOfVoteOption(int vote_id, int i)
	{
		Object object = selectOnlyValue("select count(*) from (select * from vote_to where vote_id="
				+ vote_id
				+ " and vote_option_id= "
				+ i
				+ ") as myvote where vote_option_id = " + i);
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();
		}
	}

	public boolean iInsertOptionVote(int usr_id, int num, int vote_id)
	{
		System.out.println(usr_id + "d" + num + "d" + vote_id);
		String sql = "INSERT INTO vote_to VALUES(" + num + "," + usr_id + ", "
				+ vote_id + ")";
		return longHaul(sql);
	}

	public boolean uUpdateVoteState(int vote_id)
	{

		boolean assign = longHaul("update vote set sate='1'");

		return assign;
	}

	public boolean deleteVote(int vote_id)
	{
		boolean deVoteto = longHaul("delete from vote_to where vote_id ="
				+ "vote_id");
		boolean deVote = longHaul("delete from vote where vote_id ="
				+ "vote_id");
		return deVoteto && deVote;

	}

	public String SqlGetEmail(String name)
	{

		Object object = selectOnlyValue("select e_mail from users where name ='"
				+ name + "'");
		if (object == null)
		{
			return "0";
		}
		else
		{
			return object.toString();
		}
	}

	//
	//
	// ---------------------------------SinaWeibo--------------------------------------------

	public String GetNowTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	// insertion
	public boolean InsertFriendslist(String uid, String frienduid, String seqnum)
	{
		boolean ret = true;
		if (IsInFriendslist(uid, frienduid))
			return ret;

		String stmt = "insert into friendslist(uid,frienduid,addTime,seqNum) values('"
				+ uid
				+ "','"
				+ frienduid
				+ "','"
				+ GetNowTime()
				+ "','"
				+ seqnum + "')";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	public boolean InsertFollowerslist(String uid, String followuid,
			String seqnum)
	{
		boolean ret = true;
		if (IsInFollowerslist(uid, followuid))
			return ret;

		String stmt = "insert into followerslist(uid,followeruid,addTime,seqNum) values('"
				+ uid
				+ "','"
				+ followuid
				+ "','"
				+ GetNowTime()
				+ "','"
				+ seqnum + "')";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	public boolean InsertUserlist(String uid, String uname, int flag)
	{
		boolean ret = true;
		if (IsInuserlist(uid))
			return ret;
		String stmt = "insert into userlist(uid,uname,addTime,flag) values('"
				+ uid + "','" + uname + "','" + GetNowTime() + "','"
				+ String.valueOf(flag) + "')";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	// existence
	public boolean IsInFriendslist(String uid, String frienduid)
	{
		Object object = null;
		String stmt = "select * from friendslist where uid =  '" + uid + "'"
				+ " and frienduid = '" + frienduid + "'";
		object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);
		if (object == null)
			return false;
		return true;
	}
	public boolean IsInFriendslist(String uid)
	{
		Object object = null;
		String stmt = "select * from friendslist where uid =  '" + uid+ "'" ;
		object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);
		if (object == null)
			return false;
		return true;
	}

	public boolean IsInFollowerslist(String uid, String followuid)
	{
		Object object = null;
		String stmt = "select * from followerslist where uid =  '" + uid + "'"
				+ " and followeruid = '" + followuid + "'";
		object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);
		if (object == null)
			return false;
		return true;
	}

	public boolean IsInuser(String uid)
	{
		Object object = null;
		String stmt = "select * from user where uid =  '" + uid + "'";
		object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);
		if (object == null)
			return false;
		return true;
	}

	public boolean IsInuserlist(String uid)
	{
		Object object = null;
		String stmt = "select * from userlist where uid = '" + uid + "'";
		object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);
		if (object == null)
			return false;
		return true;
	}
	public boolean IsInUpdateUidList(String uid)
	{
	    Object object = null;
	    String stmt = "select * from updateuidlist where uid = '" + uid + "'"; 
	    object = selectOnlyValue(stmt);
	    if(isToprint())
	    {
	        System.out.println(stmt);
	    }
	    if (object == null)
            return false;
        return true;
	}
	public boolean InsertUpdateUidlist(String uid,int level)
    {
        boolean ret = true;
        if (IsInUpdateUidList(uid))
            return ret;

        String stmt = "insert into updateuidlist(uid,addTime,level) values('" + uid + "','"
		+ GetNowTime()
		+ "','"
		+ level+ "')";
        if (isToprint())
            System.out.println(stmt);
        if (isToexecute())
            ret = longInsert(stmt);
        return ret;
    }
	
	public String GetLatestCrawledUid()
    {
		Object object = null;
        String ret="";
        String ret1="";
        String stmt = "SELECT count(*) FROM updateuidlist;";
        if (isToprint())
            System.out.println(stmt);
        if (isToexecute())
        	object = selectOnlyValue(stmt);
       ret = new String(object.toString());
       
       if(ret.equals("0")){return ret;}
       else{
        String stmt1 = "select uid from updateuidlist where addTime=(SELECT max(addTime)as b FROM updateuidlist)";
        if (isToprint())
            System.out.println(stmt1);
        if (isToexecute())
        	object = selectOnlyValue(stmt1);
       ret1 = new String(object.toString());
       return ret1;
       }
    }
	
	public String GetLatestCrawledLevel()
    {
		Object object = null;
        String ret="";
        String ret1="";
        String stmt = "SELECT count(*) FROM updateuidlist;";
        if (isToprint())
            System.out.println(stmt);
        if (isToexecute())
        	object = selectOnlyValue(stmt);
       ret = new String(object.toString());

       if(ret.equals("0")){
    	   return ret;
    	   }
       else{
        String stmt1 = "select level from updateuidlist where addTime=(SELECT max(addTime)as b FROM updateuidlist)";
        if (isToprint())
            System.out.println(stmt1);
        if (isToexecute())
        	object = selectOnlyValue(stmt1);
       ret1 = new String(object.toString());
       return ret1;}
    }
	// update --------
	public boolean UpdateUnameInUserlist(String uid, String uname)
	{
		boolean ret = true;
		if (IsInuserlist(uid) == false)
			return false;
		String stmt = "Update userlist set uname = '" + uname
				+ "' where uid = '" + uid + "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	// deletion ------
	public boolean DeleteFromFriendslist(String uid, String frienduid)
	{
		boolean ret = true;
		if (IsInFriendslist(uid, frienduid) == false)
			return ret;

		String stmt = "delete from friendslist where uid = '" + uid
				+ "' and frienduid = '" + frienduid + "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	public boolean DeleteFromFollowerslist(String uid, String followuid)
	{
		boolean ret = true;
		if (IsInFollowerslist(uid, followuid) == false)
			return ret;

		String stmt = "delete from followerslist where uid = '" + uid
				+ "' and followeruid = '" + followuid + "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	public boolean DeleteFromUserlist(String uid)
	{
		boolean ret = true;
		if (IsInuserlist(uid) == false)
			return ret;
		String stmt = "delete from userlist where uid = '" + uid + "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	// ------------------------------------------------------------------------------------------------------
	public int GetFlagFromUserlist(String uid)
	{
		int ret = -1;
		Object object = null;
		if (IsInuserlist(uid))
		{
			String stmt = "select flag from userlist where uid = '" + uid + "'";
			if (isToexecute())
				object = selectOnlyValue(stmt);
			if (isToprint())
				System.out.println(stmt);

			ret = new Integer(object.toString());
		}
		return ret;
	}
	
	public boolean UpdateFlagUserlist(String uid, int flag)
	{
		boolean ret = true;
		if (IsInuserlist(uid) == false)
			return false;
		String stmt = "Update userlist set flag = '" + flag
				+ "' where uid = '" + uid + "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			ret = longInsert(stmt);
		return ret;
	}

	// ------------------------------------------------------------------------------------------------------
	/*
	 * 
	 * //danger function
	 * public void cleardupinUserList(String uid)
	 * {
	 * if(IsInuserlist(uid))
	 * {
	 * System.out.println(uid+" is in the table");
	 * if(DeleteFromUserlist(uid))
	 * {
	 * System.out.println("Delete success");
	 * }
	 * }
	 * else
	 * {
	 * System.out.println("Not in the table");
	 * }
	 * }
	 */
	// ----------------------------------------------------------------------------------------------
	// selection
	public boolean SetFriendlistFlag(String uid, String fuid, boolean b, String timestamp)
	{
		boolean ret = false;
		String stmt = "update friendslist set flag = "+ b+","+"deleteTime=\'"+timestamp+"\' where uid = \'" + uid+"\' and frienduid = \'"+ fuid +"\';";
	
		if (isToprint())
			System.out.println(stmt);
		
		if(isToexecute())
			ret = longHaul(stmt);
		
		return ret;
	}
	
	public boolean SetFollowerslistFlag(String uid, String fuid,String timestamp)
	{
		boolean ret = false;
		String stmt = "update followerslist set "+"deleteTime=\'"+timestamp+"\' where uid = \'" + uid+"\' and followeruid = \'"+ fuid +"\';";
	
		if (isToprint())
			System.out.println(stmt);
		
		if(isToexecute())
			ret = longHaul(stmt);
		
		return ret;
	}
	
	public Vector GetLevelUID(int n)
	{
		Vector v = null;
		String stmt = "select uid from userlist where flag = "
				+ String.valueOf(n);
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			v = selectOnlyNote(stmt);
		return v;
	}

	public Vector GetUserFriendsID(String uid)
	{
		Vector v = null;
		String stmt = "select frienduid from friendslist where uid = '" + uid
				+ "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			v = selectOnlyNote(stmt);
		return v;
	}
	
	public Vector GetUserFollowersID(String uid)
	{
		Vector v = null;
		String stmt = "select followeruid from followerslist where uid = '" + uid
				+ "'";
		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			v = selectOnlyNote(stmt);
		return v;
		
	}

	public String GetUnameByUid(String uid)
	{
		String ret = "";
		Object object = null;
		if (IsInuserlist(uid))
		{
			String stmt = "select uname from userlist where uid = '" + uid
					+ "'";
			if (isToexecute())
				object = selectOnlyValue(stmt);
			if (isToprint())
				System.out.println(stmt);

			ret = new String(object.toString());
		}
		return ret;
	}

	public String GetUidByUname(String uname)
	{
		if (uname == "" || uname == null)
			return "";

		Object object = null;
		String ret = "";

		String stmt = "select uid from userlist where uname = '" + uname + "'";
		if (isToexecute())
			object = selectOnlyValue(stmt);
		if (isToprint())
			System.out.println(stmt);

		ret = new String(object.toString());

		return ret;
	}

	// app
	public int GetSameFriendCount(String uid, String frienduid)
	{
		int ret = 0;
		Object object = null;

		String stmt = "select count(*) from userlist where uid in (select A.frienduid from (select frienduid from friendslist where uid = '"
				+ uid
				+ "') as A , (select frienduid from friendslist where uid = '"
				+ frienduid + "') as B where A.frienduid = B.frienduid )";

		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			object = selectOnlyValue(stmt);

		ret = new Integer(object.toString());

		return ret;
	}

	
	public Vector GetSameFriendsName(String uname, String friendname)
	{
		Vector<String> v = null;

		String uid = GetUidByUname(uname);
		String frienduid = GetUidByUname(friendname);

		if (uid != null && frienduid != null)
			v = GetSameFriendsUid(uid, frienduid);

		return v;
	}

	public Vector GetSameFriendsUid(String uid, String frienduid)
	{
		Vector<String> v = null;

		String stmt = "select uid from userlist where uid in (select A.frienduid from (select frienduid from friendslist where uid = '"
				+ uid
				+ "') as A , (select frienduid from friendslist where uid = '"
				+ frienduid + "') as B where A.frienduid = B.frienduid )";

		if (isToprint())
			System.out.println(stmt);
		if (isToexecute())
			v = selectOnlyNote(stmt);

		return v;
	}

}
