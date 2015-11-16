package Util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dingcheng on 2014/11/3.
 */
public class XmlUtil {
    static FileTool myFiles = new FileTool();

    public XmlUtil() {
    }

    public static Document createDocument() {
        return DocumentHelper.createDocument();
    }

    public static Element createElement(String tag) {
        return DocumentHelper.createElement(tag);
    }

    /**
     * 通过路径inputPath读取一个XML文件，返回document
     * @param inputPath
     * @return
     * @throws Exception
     */
    public static Document readXMLDom(String inputPath) throws Exception {
        return readXMLDom(inputPath, "utf-8");
    }

    public static Document readXMLDom(String inputPath, String encode) throws Exception{
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding(encode);
        Document document = saxReader.read(new File(inputPath));
        return document;
    }

    /**
     * 将doc文件（XML文件，在内存中）输出到outputPath指定路径的文件。
     * @param doc
     * @param outputPath
     * @throws Exception
     */
    public static void writeXMLDom(Document doc,String outputPath) throws Exception {
        writeXMLDom(doc, outputPath, "utf-8");
    }

    public static void writeXMLDom(Document doc, String outputPath, String encode) throws Exception{
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding(encode);
        XMLWriter xmlWriter2 = new XMLWriter(
                new FileOutputStream(outputPath), format);
        xmlWriter2.write(doc);
    }

    public static void writeXMLDom(Element elem, String outputPath, String encode) throws Exception{writeXMLDom(DocumentHelper.createDocument(elem), outputPath, encode);};

    /**
     * 将这个树节点以string 输出
     * @param elem
     * @return
     */
    public static String elem2str(Element elem) {
        return elem.getText().trim();
    }
}
