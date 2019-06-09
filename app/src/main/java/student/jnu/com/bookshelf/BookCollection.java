package student.jnu.com.bookshelf;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookCollection {
    public String getBook() {
        return name;
    }
    String name;

    public BookCollection(String content) {
        this.name=content;
    }
    public void download(final Handler handler, final String ISBN){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //你的URL
                    String url_s = "http://119.29.3.47:9001/book/worm/isbn?isbn="+ ISBN;
                    URL url = new URL(url_s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //设置连接属性。不喜欢的话直接默认也阔以
                    conn.setConnectTimeout(5000);//设置超时
                    conn.setUseCaches(false);//数据不多不用缓存了

                    //这里连接了
                    conn.connect();
                    //这里才真正获取到了数据
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader input = new InputStreamReader(inputStream);
                    BufferedReader buffer = new BufferedReader(input);
                    if(conn.getResponseCode() == 200){//200意味着返回的是"OK"
                        String inputLine;
                        StringBuffer resultData  = new StringBuffer();//StringBuffer字符串拼接很快
                        while((inputLine = buffer.readLine())!= null){
                            resultData.append(inputLine);
                        }
                        String text = resultData.toString();
                        //String text ="{                  \"showapi_res_error\": \"\",                  \"showapi_res_id\": \"85d0fdf07cef4b3bb64daf7ef3fd968f\",                  \"showapi_res_code\": 0,                  \"showapi_res_body\": {\"ret_code\":0,\"remark\":\"success\",\"showapi_fee_code\":-1,\"data\":{\"edition\":\"\",\"paper\":\"胶版纸\",\"pubdate\":\"2016-12\",\"img\":\"http://app2.showapi.com/isbn/imgs/ca4106df22ea4fa08f2e552ecd8a2779.jpg\",\"gist\":\"《*行代码 Android 第2版》被Android开发者誉为Android学习经典。全书系统全面、循序渐进地介绍了Android软件开发的知识、经验和技巧。第2版基于Android 7.0对第1版进行了全面更新，将所有知识点都在新的Android系统上进行重新适配，使用全新的Android Studio开发工具代替之前的Eclipse，并添加了对Material Design、运行时权限、Gradle、RecyclerView、百分比布局、OkHttp、Lambda表达式等全新知识点的详细讲解。\\\\n本书内容通俗易懂，由浅入深，既适合Android初学者的入门，也适合Android开发者的进阶。\",\"format\":\"16开\",\"publisher\":\"人民邮电出版社\",\"author\":\"郭霖\",\"title\":\"第一行代码\",\"price\":\"79.00\",\"page\":\"570\",\"isbn\":\"9787115439789\",\"binding\":\"平装-胶订\",\"produce\":\"\"}}                }";
                        parseJson(text);
                        handler.sendEmptyMessage(1);
                        Log.v("out---------------->",text);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJson(String text) {
        try {
            //这里的text就是上边获取到的数据，一个String.
            JSONObject jsonObject = new JSONObject(text);
            name=jsonObject.getString("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
