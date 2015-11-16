package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

public class BaseDao
{

	public boolean toprint = false;
	public boolean toexecute = true;

	public boolean isToexecute()
	{
		return toexecute;
	}

	public void setToexecute(boolean toexecute)
	{
		this.toexecute = toexecute;
	}

	public boolean isToprint()
	{
		return toprint;
	}

	public void setToprint(boolean toprint)
	{
		this.toprint = toprint;
	}

	private Connection getConnection() throws Exception
	{ // 获得数据库连接
		return JDBC.getConnection();
	}

	private void closeConnection(Connection conn)
	{ // 关连接
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void closePrepStmt(PreparedStatement prepStmt)
	{ // 关preStmt
		try
		{
			if (prepStmt != null)
			{
				prepStmt.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void closeStmt(Statement stmt)
	{ // 关连接状态对象
		try
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void closeResultSet(ResultSet rs)
	{ // 关查询结果
		try
		{
			if (rs != null)
			{
				rs.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// 查询
	public Vector<Vector<Object>> select(String sql)
	{
		Vector<Vector<Object>> ret = new Vector<Vector<Object>>();// 创建结果集向量
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果
			int c = rs.getMetaData().getColumnCount();// 获得查询数据表的列数
			while (rs.next())
			{// 遍历结果集
				Vector<Object> newRow = new Vector<Object>();// 创建行向量
				// rowV.add(new Integer(row++));// 添加行序号
				for (int i = 1; i <= c; i++)
				{
					newRow.add(rs.getObject(i));// 添加列值
				}
				ret.add(newRow);// 将行向量添加到结果集向量中
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
		}
		return ret;// 返回结果集向量
	}

	// 查询单记录
	public Vector<Object> selectOne(String sql)
	{
		Vector<Object> ret = new Vector<Object>();// 创建结果集向量
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果
			while (rs.next())
			{// 遍历结果集
				ret.add(rs.getObject(1));// 将行向量添加到结果集向量中
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
		}
		return ret;// 返回结果集向量
	}

	// 查询多个记录
	protected Vector selectSomeNote(String sql)
	{
		Vector<Vector<Object>> vector = new Vector<Vector<Object>>();// 创建结果集向量
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果
			int columnCount = rs.getMetaData().getColumnCount();// 获得查询数据表的列数
			int row = 1;// 定义行序号 ??有必要？？？
			while (rs.next())
			{// 遍历结果集
				Vector<Object> rowV = new Vector<Object>();// 创建行向量
				rowV.add(new Integer(row++));// 添加行序号
				for (int column = 1; column <= columnCount; column++)
				{
					rowV.add(rs.getObject(column));// 添加列值
				}
				vector.add(rowV);// 将行向量添加到结果集向量中
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
		}
		return vector;// 返回结果集向量
	}

	// 查询单个记录
	protected Vector selectOnlyNote(String sql)
	{
		Vector<Object> vector = new Vector<Object>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果
			int columnCount = rs.getMetaData().getColumnCount();
			while (rs.next())
			{
				for (int column = 1; column <= columnCount; column++)
				{
					vector.add(rs.getObject(column));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
			// closeConnection(conn);
		}
		return vector;
	}

	// 查询多个值
	protected Vector selectSomeValue(String sql)
	{
		Vector<Object> vector = new Vector<Object>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果
			while (rs.next())
			{
				vector.add(rs.getObject(1));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
		}
		return vector;
	}

	// 查询单个值
	protected Object selectOnlyValue(String sql)
	{
		Object value = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			rs = stmt.executeQuery(sql);// 执行SQL语句获得查询结果

			while (rs.next())
			{
				value = rs.getObject(1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResultSet(rs);
			closeStmt(stmt);
		}
		return value;
	}

	// 插入、修改、删除记录
	public boolean longHaul(String sql)
	{
		boolean isLongHaul = true;// 默认持久化成功
		Connection conn = null;
		Statement stmt = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			stmt.execute(sql);// 执行SQL语句获得查询结果
		}
		catch (Exception e)
		{
			isLongHaul = false;// 持久化失败
			try
			{
				conn.rollback();// 回滚
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally
		{
			closeStmt(stmt);
		}
		return isLongHaul;// 返回持久化结果
	}

	public boolean longInsert(String sql)
	{
		boolean isLongHaul = true;// 默认持久化成功
		Connection conn = null;
		Statement stmt = null;
		try
		{
			conn = getConnection();// 获得数据库连接
			stmt = conn.createStatement();// 创建连接状态对象
			stmt.execute(sql);// 执行SQL语句获得查询结果
		}
		catch (Exception e)
		{
			isLongHaul = false;// 持久化失败
			e.printStackTrace();
		}
		finally
		{
			closeStmt(stmt);
		}
		return isLongHaul;// 返回持久化结果
	}

	public boolean longHaul(Vector<String> sql)
	{
		boolean isLongHaul = true;// 默认持久化成功
		Enumeration e = sql.elements();
		while (e.hasMoreElements())
		{
			if (!longHaul((String) e.nextElement()))
			{
				isLongHaul = false;
			}
		}
		return isLongHaul;// 返回持久化结果
	}

	// 向一个table插入几行值，保证每行的数据个数与数据库中的数据项一致且一一对应
	public Vector<String> insertSQL(Vector<Vector<Object>> insertObject,
			String table) throws NotAligned
	{
		if (insertObject.isEmpty())
		{
			return new Vector<String>();
		}
		Enumeration e = insertObject.elements();
		int c = ((Vector<Object>) e.nextElement()).size();
		while (e.hasMoreElements())
		{
			if (c != ((Vector<Object>) e.nextElement()).size())
			{
				throw new NotAligned("数据未对齐！");
			}
		}
		Vector<String> ret = new Vector<String>();
		Enumeration ee;
		String sql, q;
		e = insertObject.elements();
		while (e.hasMoreElements())
		{
			sql = "INSERT INTO " + table + " VALUES (";
			boolean first = true;
			ee = ((Vector<Object>) e.nextElement()).elements();
			while (ee.hasMoreElements())
			{
				Object o = ee.nextElement();
				if (o.getClass().equals(String.class)
						|| o.getClass().equals(java.sql.Timestamp.class))
				{
					q = "'";
				}
				else
				{
					q = "";
				}
				if (first)
				{
					sql = sql + q + o.toString() + q;
				}
				else
				{
					sql = sql + "," + q + o.toString() + q;
				}
				first = false;
			}
			sql = sql + ")";
			ret.add(sql);
		}
		return ret;
	}

	// 从一个table中删除某些行数据
	public Vector<String> deleteSQL(Vector<Vector<Object>> deleteObject,
			String table, Vector<String> pk) throws NotAligned
	{
		if (deleteObject.isEmpty() || pk.isEmpty())
		{
			return new Vector<String>();
		}
		Enumeration e = deleteObject.elements();
		int c = pk.size();
		while (e.hasMoreElements())
		{
			if (c > ((Vector<Object>) e.nextElement()).size())
			{
				throw new NotAligned("数据未对齐！");
			}
		}
		Vector<String> ret = new Vector<String>();
		e = deleteObject.elements();
		Enumeration ee, pke;
		String sql, p, q;
		while (e.hasMoreElements())
		{
			sql = "DELETE FROM " + table + " WHERE ";
			boolean first = true;
			ee = ((Vector<Object>) e.nextElement()).elements();
			pke = pk.elements();
			while (pke.hasMoreElements())
			{
				Object o = ee.nextElement();
				p = (String) pke.nextElement();
				if (o.getClass().equals(String.class)
						|| o.getClass().equals(java.sql.Timestamp.class))
				{
					q = "'";
				}
				else
				{
					q = "";
				}
				if (first)
				{
					sql = sql + p + " = " + q + o.toString() + q;
				}
				else
				{
					sql = sql + " and " + p + " = " + q + o.toString() + q;
				}
				first = false;
			}
			ret.add(sql);
		}
		return ret;
	}

	// 更新数据库
	public Vector<String> updateSQL(Vector<Vector<Object>> updateObject,
			String table, Vector<String> pk, Vector<String> key)
			throws NotAligned
	{
		if (updateObject.isEmpty() || pk.isEmpty())
		{
			return new Vector<String>();
		}
		Enumeration e = updateObject.elements();
		int c = pk.size() + key.size();
		while (e.hasMoreElements())
		{
			if (c != ((Vector<Object>) e.nextElement()).size())
			{
				throw new NotAligned("数据未对齐！");
			}
		}
		Vector<String> ret = new Vector<String>();
		e = updateObject.elements();
		Enumeration ee, ke;
		String sql1, sql2, p, q;
		while (e.hasMoreElements())
		{
			sql1 = "UPDATE " + table + " SET ";
			sql2 = " WHERE ";
			boolean first = true;
			ee = ((Vector<Object>) e.nextElement()).elements();
			ke = pk.elements();
			while (ke.hasMoreElements())
			{
				Object o = ee.nextElement();
				p = (String) ke.nextElement();
				if (o.getClass().equals(String.class)
						|| o.getClass().equals(java.sql.Timestamp.class))
				{
					q = "'";
				}
				else
				{
					q = "";
				}
				if (first)
				{
					sql2 = sql2 + p + " = " + q + o.toString() + q;
				}
				else
				{
					sql2 = sql2 + " and " + p + " = " + q + o.toString() + q;
				}
				first = false;
			}
			first = true;
			ke = key.elements();
			while (ke.hasMoreElements())
			{
				Object o = ee.nextElement();
				p = (String) ke.nextElement();
				if (o.getClass().equals(String.class)
						|| o.getClass().equals(java.sql.Timestamp.class))
				{
					q = "'";
				}
				else
				{
					q = "";
				}
				if (first)
				{
					sql1 = sql1 + p + " = " + q + o.toString() + q;
				}
				else
				{
					sql1 = sql1 + " , " + p + " = " + q + o.toString() + q;
				}
				first = false;
			}
			ret.add(sql1 + sql2);
		}
		return ret;
	}
}

// 自定义未对齐异常
class NotAligned extends Exception
{

	public NotAligned()
	{
		super();
	}

	public NotAligned(String msg)
	{
		super(msg);
	}

	public NotAligned(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public NotAligned(Throwable cause)
	{
		super(cause);
	}
}
