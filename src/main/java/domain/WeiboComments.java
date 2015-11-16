package domain;

import Util.XmlUtil;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建一条微博的xml文件
 * Created by dingcheng on 2015/1/7.
 */
public class WeiboComments {
    String id = "";
    String content = "";
    List<String> commentList = new ArrayList<String>();

    public WeiboComments(String id, String content, List<String> commentList) {
        this.id = id;
        this.content = content;
        this.commentList = commentList;
    }

    @Override
    public String toString() {
        return "WeiboComments{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", commentList=" + commentList +
                '}';
    }

    public Element createElement() {
        Element root = XmlUtil.createElement("weibo");
        root.addElement("id").setText(id);
        root.addElement("text").setText(content);
        Element comments = root.addElement("comments");
        for (String each : commentList) {
            comments.addElement("comment").setText(each);
        }
        return root;
    }
}
