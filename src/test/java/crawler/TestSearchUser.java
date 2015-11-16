package crawler;

import SN.WeiboCrawler;
import org.junit.Test;
import weibo4j.http.Response;

/**
 * Created by Teisei on 2015/4/7.
 */
public class TestSearchUser extends WeiboCrawler {
    @Test
    public void testOne() {
        String args[] = {};
        init(args);
        resetToken();
        refreshToken();
        try {
//            Response res = weibo.getSearchUser("teisei", 10);
            Response res = weibo.getTopStatus("丰球集团", 10);
            System.out.println(res.toString());

        } catch (Exception e) {
//{"statuses":[{"created_at":"Sun May 13 18:38:30 +0800 2012","id":3445293779808422,"mid":"3445293779808422","idstr":"3445293779808422","text":"[\u7231\u4f60]\u6211\u5728\u8fd9\u91cc:#\u6d59\u6c5f\u4e30\u7403\u96c6\u56e2\u6709\u9650\u516c\u53f8#\u6b21\u996d\u996d\u2026\u6211\u997f\u4e86\uff0c\u997f\u4e86\u3002 http:\/\/t.cn\/zOEtprD","source_allowclick":0,"source_type":1,"source":"<a href=\"http:\/\/app.weibo.com\/t\/feed\/c66T5g\" rel=\"nofollow\">Android\u5ba2\u6237\u7aef<\/a>","favorited":false,"truncated":false,"in_reply_to_status_id":"","in_reply_to_user_id":"","in_reply_to_screen_name":"","pic_ids":["6e6bf15ejw1dswvmwun4fj"],"thumbnail_pic":"http:\/\/ww4.sinaimg.cn\/thumbnail\/6e6bf15ejw1dswvmwun4fj.jpg","bmiddle_pic":"http:\/\/ww4.sinaimg.cn\/bmiddle\/6e6bf15ejw1dswvmwun4fj.jpg","original_pic":"http:\/\/ww4.sinaimg.cn\/large\/6e6bf15ejw1dswvmwun4fj.jpg","geo":{"type":"Point","coordinates":[29.70396,120.2508]},"user":{"id":1852567902,"idstr":"1852567902","class":1,"screen_name":"\u971e\u971e\u971e\u5b9d\u8d1d","name":"\u971e\u971e\u971e\u5b9d\u8d1d","province":"33","city":"6","location":"\u6d59\u6c5f \u7ecd\u5174","description":"\u505a\u597d\u81ea\u5df1\uff0c\u522b\u7684\u4e8b\u60c5\u4e0a\u5929\u81ea\u6709\u5b89\u6392\uff5e","url":"http:\/\/625027974.qzone.qq.com","profile_image_url":"http:\/\/tp3.sinaimg.cn\/1852567902\/50\/5706180188\/0","cover_image_phone":"http:\/\/ww2.sinaimg.cn\/crop.0.0.640.640.640\/a1d3feabjw1ecassls6b2j20hs0hsq50.jpg","profile_url":"tutu3325749","domain":"tutu3325749","weihao":"","gender":"f","followers_count":602,"friends_count":313,"pagefriends_count":1,"statuses_count":448,"favourites_count":0,"created_at":"Thu Oct 14 01:19:34 +0800 2010","following":false,"allow_all_act_msg":false,"geo_enabled":true,"verified":false,"verified_type":-1,"ptype":0,"allow_all_comment":true,"avatar_large":"http:\/\/tp3.sinaimg.cn\/1852567902\/180\/5706180188\/0","avatar_hd":"http:\/\/ww4.sinaimg.cn\/crop.0.0.852.852.1024\/6e6bf15ejw8ekj66leszdj20no0npjuf.jpg","verified_reason":"","verified_trade":"","verified_reason_url":"","verified_source":"","verified_source_url":"","follow_me":false,"online_status":0,"bi_followers_count":149,"lang":"zh-cn","star":0,"mbtype":0,"mbrank":0,"block_word":0,"block_app":0,"ulevel":0,"badge_top":"","extend":{"privacy":{"mobile":1},"mbprivilege":"0000000000000000000000000000000000000000000000000000000000000000"},"credit_score":80,"urank":20},"annotations":[{"place":{"poiid":"B2094651DA6DA7F84393","title":"\u6d59\u6c5f\u4e30\u7403\u96c6\u56e2\u6709\u9650\u516c\u53f8","lon":120.2508,"lat":29.70396,"type":"checkin"}}],"reposts_count":0,"comments_count":6,"attitudes_count":0,"mlevel":0,"visible":{"type":0,"list_id":0},"url_objects":[{"url_ori":"http:\/\/t.cn\/zOEtprD","info":{"url_short":"http:\/\/t.cn\/zOEtprD","url_long":"http:\/\/m.weibo.cn\/poi?entry=client&poiid=B2094651DA6DA7F84393","type":0,"result":true,"title":"","description":"","last_modified":1336905510,"transcode":0},"like_count":0,"follower_count":0,"asso_like_count":0,"card_info_un_integrity":false},{"url_ori":"\u6d59\u6c5f\u4e30\u7403\u96c6\u56e2\u6709\u9650\u516c\u53f8","object_id":"1022:10080876aba069299deff3f921374debc962e0","like_count":0,"follower_count":0,"asso_like_count":0,"card_info_un_integrity":false}],"darwin_tags":[],"category":31}],"total_number":1}
        }
    }
}
