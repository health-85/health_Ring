package com.healthy.rvigor.net.http;

import com.healthy.rvigor.util.AppUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * delete 请求
 */
public class HttpDeleteRequestBase extends HttpRequestBase {

    public HttpDeleteRequestBase(String url) {
        super(url);
    }
    @Override
    public String getUrl() {
        String  url= super.getUrl();
        if (url.indexOf("?")>-1){
            return url+"&"+makeContent();
        }else {
            return url+"?"+makeContent();
        }
    }

    public  String  makeContent(){
        String R="";
        Set<Map.Entry<String,String>> entrySet= getParams().entrySet();
        Iterator<Map.Entry<String,String>> iterator=  entrySet.iterator();
        while (iterator.hasNext()){
            Map.Entry<String,String>  entry=iterator.next();
            if (R.equals("")){
                R+=entry.getKey()+"="+ AppUtils.urlEncoding(entry.getValue(),"utf-8");
            }else{
                R+="&"+entry.getKey()+"="+ AppUtils.urlEncoding(entry.getValue(),"utf-8");
            }
        }
        return R;
    }

}
