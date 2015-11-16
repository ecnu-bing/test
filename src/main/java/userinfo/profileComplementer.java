package userinfo;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;


public class profileComplementer {
	public profile profile = new profile();

	public profileComplementer() {

	}

	public profileComplementer(String userID) {
		profile.setUserID(userID);
	}

	public String expand(String path, String charset, String output)
			throws HttpException, IOException, Exception {
		crawler c = new crawler();
		return c.DownloadPages(path + profile.getUserID(), charset, output);
	}

	public profile extract(String html) {
		String basic = "";
		String edu = "";
		String work = "";
		String other = "";
		int basicStart = html.indexOf("【基本信息】") + ("【基本信息】").length();
		html = html.substring(basicStart);
		int basicEnd = html.indexOf("<br/>【");
		if (basicStart > -1 && basicEnd > -1) {
			basic = html.substring(0, basicEnd);
			parseBasic(basic);
		}

		int eduStart = html.indexOf("【学习经历】") + ("【学习经历】").length();
		int eduEnd = html.indexOf("<br/>他的校友:");
		if (eduStart > -1 && eduEnd > -1) {
			edu = html.substring(eduStart, eduEnd);
			parseEdu(edu);
		}

		int workStart = html.indexOf("【工作经历】") + ("【工作经历】").length();
		int workEnd = html.indexOf("<br/>他的同事:");
		if (workStart > -1 && workEnd > -1) {
			work = html.substring(workStart, workEnd);
			parseWork(work);
		}

		int otherStart = html.indexOf("【其他信息】") + ("【其他信息】").length();
		int otherEnd = html.indexOf("<br/><a href=\"album.php?");
		if (otherStart > -1 && otherEnd > -1) {
			other = html.substring(otherStart, otherEnd);
			parseOther(other);
		}
		return profile;
	}

	public void parseBasic(String basic) {
		String[] items = basic.split("<br/>");
		for (String s : items) {
			String[] item = s.split(":");
			if (item.length >= 2) {
				profile.basicInfo.put(item[0].trim(), item[1].replaceAll(
						"<a([^<]*)>([^<]*)</a>", "$2").replaceAll("&nbsp", "")
						.replaceAll("更多&gt;&gt;", "").trim());
			}
		}
	}

	public void parseEdu(String edu) {
		String[] items = edu.split("<br/>");
		for (String s : items) {
			s = s.replaceAll("\\s*·<a([^<]*)>([^<]*)</a>(.*)", "$2$3")
					.replaceAll("&nbsp\\s*", "").replaceAll("--", "");
			if (s.contains(";")) {
				String[] item = s.split(";");
				profile.eduInfo.put(item[0].trim(), item[1].trim());
			} else if (s.trim().length() > 0) {
				profile.eduInfo.put(s.trim(), "");
			}
		}
	}

	public void parseWork(String work) {
		String[] items = work.split("<br/>");
		for (String s : items) {
			s = s.replaceAll("\\s*·<a([^<]*)>([^<]*)</a>(.*)", "$2$3")
					.replaceAll("&nbsp\\s*", "").replaceAll("--", "");
			if (s.contains(";")) {
				String[] item = s.split(";");
				profile.workInfo.put(item[0].trim(), item[1].trim());
			} else if (s.trim().length() > 0) {
				profile.workInfo.put(s.trim(), "");
			}
		}
	}

	public void parseOther(String other) {
		String[] items = other.split("<br/>");
		for (String s : items) {
			if (s.contains(":")) {
				int i = s.indexOf(":");
				profile.otherInfo.put(s.substring(0, i).trim(), s.substring(
						i + 1).trim());
			}
		}
	}

	public static void main(String[] args) throws HttpException, IOException,
			Exception {
		// String test =
		// "<a href=\"search.php?suser=1&amp;gender=0&amp;comorsch=%E5%A4%8D%E6%97%A6%E5%A4%A7%E5%AD%A6\">复旦大学</a>&nbsp;94级";
		// System.out.println(test.matches("<a([^<]*)>([^<]*)</a>"));
		// test = test.replaceAll("<a([^<]*)>([^<]*)</a>(.*)", "$2$3");
		// System.out.println(test);
		String userID = "1075876334";
		profileComplementer pe = new profileComplementer(userID);
		String html = pe.expand("http://www.weibo.cn/dpool/ttt/user.php?uid=",
				"UTF-8", "C:\\AzaleaDu\\data-profile\\html\\" + userID + ".html");

		profile p = pe.extract(html);
		System.out.println(p.userID);
		System.out.println("Basic info:");
		TreeMap<String, String> basic = p.basicInfo;
		for (Entry<String, String> e : basic.entrySet()) {
			System.out.println(e.getKey() + ":" + e.getValue());
		}

		System.out.println("edu info:");
		TreeMap<String, String> edu = p.eduInfo;
		for (Entry<String, String> e : edu.entrySet()) {
			System.out.println(e.getKey() + ":" + e.getValue());
		}

		System.out.println("work info:");
		TreeMap<String, String> work = p.workInfo;
		for (Entry<String, String> e : work.entrySet()) {
			System.out.println(e.getKey() + ":" + e.getValue());
		}

		System.out.println("other info:");
		TreeMap<String, String> other = p.otherInfo;
		for (Entry<String, String> e : other.entrySet()) {
			System.out.println(e.getKey() + ":" + e.getValue());
		}
	}
}

