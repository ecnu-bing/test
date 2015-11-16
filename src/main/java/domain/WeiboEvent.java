package domain;

import Util.XmlUtil;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingcheng on 2015/1/7.
 */
public class WeiboEvent {
    String name = "";
    String keywords = "";
    List<WeiboComments> wCommentList = new ArrayList<WeiboComments>();

    public WeiboEvent(String name, String keywords, List<WeiboComments> wCommentList) {
        this.name = name;
        this.keywords = keywords;
        this.wCommentList = wCommentList;
    }

    public Element createElement() {
        Element root = XmlUtil.createElement("event");
        root.addElement("name").setText(name);
        root.addElement("keywords").setText(keywords);
        Element weibosElem = root.addElement("weibos");
        for (WeiboComments each : wCommentList) {
            weibosElem.add(each.createElement());
        }
        return root;
    }
}
