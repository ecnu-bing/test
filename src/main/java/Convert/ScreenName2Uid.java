package Convert;

import Util.Token;
import Util.TokenManage;
import Util.Tool;
import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.User;
import weibo4j.model.UserWapper;

import java.util.ArrayList;
import java.util.List;


public class ScreenName2Uid {
    public static void main(String[] args) throws weibo4j.model.WeiboException, WeiboException {
        ScreenName2Uid test = new ScreenName2Uid();
//        test.getFriend("1197745300", "黄小邪在芝大");
        /*List<String> tempList = Tool.readFile(args[0], "GBK");
        for(String record:tempList)
        {
            String[] list = record.split("\t");
            test.getFriend(list[1], list[0]);
        }*/
        test.getUserId("Teisei-dindin");

    }

    public void getUserId(String name) throws WeiboException, weibo4j.model.WeiboException {
        int count = 0;
        ArrayList<String> unameList = new ArrayList<String>();
        //unameList.add(name);
        ArrayList<String> shipList = new ArrayList<String>();
        Friendships friendship = new Friendships();

        /**
         * 获得用户的uid
         */
        String uid = null;


        Weibo weibo = new Weibo();
        TokenManage tm = new TokenManage();
        tm.ChekState();
        Token tokenpack = null;
        tokenpack = tm.GetToken();
        weibo.setToken(tokenpack.token);


        try {
            User user = weibo.showUserByName(name);
            System.out.println(user.getName() + "|#|" + user.getId() + "|#|" + user.getScreenName());

            try {
                double a = Math.random() * 5000;
                a = Math.ceil(a);
                int randomNum = new Double(a).intValue();
                System.out.println("sleep : " + randomNum / 1000 + "s");
                Thread.sleep(randomNum);
            } catch (InterruptedException e1) {
                //e1.printStackTrace();
            }

        } catch (weibo4j.model.WeiboException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WeiboException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getFriend(String uid, String name) {
        int count = 0;
        ArrayList<String> unameList = new ArrayList<String>();
        //unameList.add(name);
        ArrayList<String> shipList = new ArrayList<String>();
        Friendships friendship = new Friendships();
        Weibo weibo = new Weibo();
        TokenManage tm = new TokenManage();
        tm.ChekState();
        Token tokenpack = null;
        tokenpack = tm.GetToken();
        weibo.setToken(tokenpack.token);

        Paging paging = new Paging();
        paging.setCount(200);
        int page = 1;
        List<User> userList = new ArrayList<User>();
        paging.setPage(page++);
        for (int i = 1; ; ) {
            paging.setPage(i);
            try {
                Response response = friendship.getFriendsByID(uid, paging);
                System.out.println(++count);
                try {
                    double a = Math.random() * 5000;
                    a = Math.ceil(a);
                    int randomNum = new Double(a).intValue();
                    System.out.println("sleep : " + randomNum / 1000 + "s");
                    Thread.sleep(randomNum);
                } catch (InterruptedException e1) {
                    //e1.printStackTrace();
                }
                UserWapper userwapper = User.constructWapperUsers(response);
                userList = userwapper.getUsers();
                for (User user : userList) {
                    //if(!shipList.contains("北冥乘海生"+"\t"+user.getName()))
                    {
                        shipList.add(name + "\t" + user.getName());
                    }
                    if (!unameList.contains(user.getName())) {
                        unameList.add(user.getName());
                    }
                }
                if (userList.size() <= 0) {
                    break;
                }
                ++i;
                userList.clear();
            } catch (weibo4j.model.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        /*for(int j=1;;)
        {
            paging.setPage(j);
            try {
                 Response response = friendship.getFollowersById("1977581633",paging);
                 UserWapper userwapper = User.constructWapperUsers(response);
                userList = userwapper.getUsers();
                try
                {
                    double a = Math.random()*50000;  
                    a = Math.ceil(a);  
                    int randomNum = new Double(a).intValue(); 
                    System.out.println("sleep : " +randomNum/1000 +"s");
                    Thread.sleep(randomNum);
                }catch (InterruptedException e1)
                {
                    //e1.printStackTrace();
                }
                for(User user:userList)
                {
                    //if(!shipList.contains(user.getName()+"\t"+ "北冥乘海生"))
                    {
                        shipList.add(user.getName()+"\t"+ "北冥乘海生");
                    }
                    if(!unameList.contains(user.getName()))
                    {
                        unameList.add(user.getName());
                    }
                }
                if(userList.size()<=0)
                {
                    break;
                }
                ++j;
                userList.clear();
            } catch (weibo4j.model.WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }*/
        for (String uname : unameList) {
            for (int j = 1; ; ) {
                paging.setPage(j);
                try {
                    Response response = friendship.getFriendsByScreenName(uname, paging);
                    try {
                        double a = Math.random() * 5000;
                        a = Math.ceil(a);
                        int randomNum = new Double(a).intValue();
                        System.out.println("sleep : " + randomNum / 1000 + "s");
                        Thread.sleep(randomNum);
                    } catch (InterruptedException e1) {
                        //e1.printStackTrace();
                    }
                    UserWapper userwapper = User.constructWapperUsers(response);
                    userList = userwapper.getUsers();
                    for (User user : userList) {
                        if (unameList.contains(user.getName()) && !shipList.contains(uname + "\t" + user.getName())) {
                            shipList.add(uname + "\t" + user.getName());
                        }
                    }
                    if (userList.size() <= 0) {
                        break;
                    }
                    ++j;
                    userList.clear();
                } catch (weibo4j.model.WeiboException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (WeiboException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        Tool.write(".\\data\\" + name, shipList);
        shipList.clear();
    }
}
