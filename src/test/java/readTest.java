import Util.MyFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teisei on 2015/4/26.
 */
public class readTest {
    public static void main(String args[]) throws IOException {
        String path = "\\\\58.198.176.83\\zhangqy\\songleyi\\data\\fusionresult.txt";String encode = "utf-8";
//        path = "D:\\RWork\\ekg\\3073_2.2";encode="gbk";
//        path = "D:\\RWork\\ekg\\timeline和supply chain 备份\\kg.sql";encode="utf-8";
        MyFile myFile = new MyFile(path, encode);
        int test = 227800000;
        test = 228800000;
        test = 200000;
        int i = 0;
        String line = myFile.readLine();
        String first = line;
        String res0[]=new String[]{"companyname",
                "organization",
                "area",
                "englishname",
                "address",
                "chinesename",
                "telephone",
                "email",
                "registered",
                "president",
                "staff",
                "secretary",
                "secretaryphone",
                "artificialperson",
                "contactname",
                "turnover",
                "fax",
                "homepage",
                "secretaryemail",
                "infowebsite",
                "newspaper",
                "range",
                "model",
                "founding_time",
                "register_number",
                "Zip_code",
                "brandname",
                "customers",
                "product",
                "exports",
                "sales_area",
                "monthproduction",
                "ManagementAuthority",
                "OEMProcessing",
                "FixedPhone",
                "MainService",
                "ServiceArea",
                "CompamyCode",
                "A_shareStockCode",
                "A_shareTicker",
                "A_shareListingDate",
                "A_shareTotalCapital",
                "A_shareFreefloat",
                "B_shareStockCode",
                "B_shareTickerH",
                "B_shareListingDate",
                "B_shareTotalCapital",
                "B_shareFreeFloat"};
        int max_len[] = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        line = myFile.readLine();
        while (line != null && i < test) {

            i++;
//            String sql = "INSERT INTO `company_total`() VALUES ()";
            String res[] = line.split("\t");
//            String keys = "";
//            String values = "";
            for (int j = 0; j < res.length; j++) {
                if (!res[j].equals("")) {
//                    keys += "" + res0[j] + ",";
//                    values += "`" + res[j] + "`";

                    max_len[j] = Math.max(max_len[j], res[j].length());
//                    if(res0[j].equals("CompamyCode"))
//                        System.out.println(res0[j] + " || " + res[j]);
                }
            }
//            keys += res0[res0.length-1];
//            values += res[res.length-1];
//            sql += keys;
//            sql += ") VALUES (";
//            sql += values;
//            sql += ")";

            line = myFile.readLine();
        }
        System.out.println(i);
        for (int j = 0; j < res0.length; j++) {
            System.out.println("" + j + "\t" + res0[j] + "\t\t" + max_len[j]);
        }
    }
}
