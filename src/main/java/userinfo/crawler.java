package userinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;

//import com.dappit.Dapper.parser.MozillaParser;

public class crawler {
	public Document htmlDom;
	public String htmlString;
	/*
	 * download a page that url links to, return the document type of the html
	 * 
	 * @param url url of the page to download
	 * 
	 * @param charset charset of the page to download
	 * 
	 * @param outFile outFile of the result to store if want to store
	 * 
	 * @param save whether to save the downloaded page
	 */
	public String DownloadPages(String url, String charset, String outFile)
			throws HttpException, IOException, Exception {
		String CONTENT_CHARSET = charset;
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, CONTENT_CHARSET);
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
		// 30000);
		// httpClient.getHttpConnectionManager().getParams().setSoTimeout(90000);
		GetMethod getMethod = null;
		StringBuffer sb = new StringBuffer();
		try {
			getMethod = new GetMethod(url);
			httpClient.executeMethod(getMethod);
			InputStream responseBody1 = getMethod.getResponseBodyAsStream();

			InputStreamReader dis = new InputStreamReader(responseBody1,
					charset);
			BufferedReader in = new BufferedReader(dis);
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				sb = sb.append(inputLine + "\n");
//				System.out.println(inputLine + "\n");
			}
			in.close();
			dis.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());

		} finally {
			getMethod.releaseConnection();
		}

//		MozillaParser.init(
//				"C:\\AzaleaDu\\Mozilla\\dist\\windows\\MozillaParser.dll",
//				"C:\\AzaleaDu\\Mozilla\\dist\\windows\\mozilla");
//		MozillaParser mParser = new MozillaParser();
//		// w3c
//		org.w3c.dom.Document dom = mParser.parse(sb.toString().getBytes(charset), "UTF-8");
//
//		//
//		DOMReader reader = new DOMReader();
//		Document document = reader.read(dom);
//
		FileProcesser fp = new FileProcesser();
		fp.save(outFile, sb.toString(), "UTF-8");

		return sb.toString();
	}

	public static void main(String args[]) throws Exception {
		// WikiSearchProcesser wp = new WikiSearchProcesser();
		Date start = new Date();
		crawler c = new crawler();
		c.DownloadPages(
				"http://www.weibo.cn/dpool/ttt/user.php?uid=1935560003",
				"UTF-8", "D:\\zhouxiaofang1.html");
		System.out.println("Time cost: "
				+ (new Date().getTime() - start.getTime()));
	}
}

