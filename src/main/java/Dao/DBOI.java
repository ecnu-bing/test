/**
 * SinaT 
 * @date Oct 14, 2010
 * @author haixinma
 */
/**
 *  Mysql Operation Interface
 *
 *  Operation List: 
 *    (1) Open
 *    (2) Close
 *    (3) Put
 *    (4) Get
 *
 */
package Dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBOI {
    private Connection connection = null;
    private DatabaseMetaData dbMetaData = null;
    String database ;
    Statement statement = null;
    String sql = null;
    public DBOI(String database)
    {
        this.database = "jdbc:mysql://localhost:3306/" + database + "?useUnicode=true&characterEncoding=utf8"; 
    }
    public void open()
    {
        try {
            //connection = DriverManager.getConnection(database,"haixinma","");
            connection = DriverManager.getConnection(database,"root","mhx");
            if(!connection.isClosed())
            {
                //System.out.println("Connect successfully!");
            }
            else
            {
                System.out.println("Connection Failed!");
            }
            dbMetaData = connection.getMetaData();
            if (dbMetaData.supportsTransactions())
            {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate("SET NAMES utf8");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public ResultSet select(String srtCommond)
    {
    	ResultSet res = null;
    	try {
			res = statement.executeQuery(srtCommond);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return res;
    }
    public void insert(String strCommond,boolean flag)
    {
        this.sql = strCommond;
        //System.out.println(sql);
        boolean success = false;
        try {
            if (dbMetaData.supportsBatchUpdates())
            {
                if(!sql.equals(""))
                {
                    statement.addBatch(sql);
                }
            }
            else
            {
                if(!sql.equals(""))
                {
                    statement.executeUpdate(sql);
                }
            }
            if(dbMetaData.supportsBatchUpdates() && flag)
            {
                statement.executeBatch();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            //System.out.println(sql);
            //e.printStackTrace();
            success = true;
            //System.err.println(e.getMessage());
        }
        try
        {
            if (dbMetaData.supportsTransactions())
            {
                if (success)
                {
                    connection.rollback();
                } else if(flag)
                {
                    connection.commit();
                }
            }
        } catch (SQLException e)
        {
            System.out.println("Can't close connection.");
        }
    }
    
    public void close()
    {
        try {
            connection.close();
            //System.out.println("Disconnect successfully!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Can't close connection.");
        }
    }
}
