package student.jnu.com.bookshelf;

import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class BookCollection {
    public String getBook() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthor() {
        return author;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getName() {
        return name;
    }

    private String name;
    private String author;
    private String publisher;
    private String year;
    private String imgSrc;

    public void download(final Handler handler, final String ISBN){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //你的URL
                    String url_s = "http://119.29.3.47:9001/book/worm/isbn?isbn="+ ISBN;
                    //String url_s = "https://api.jisuapi.com/isbn/query?appkey=d4a09c0d185f8782&isbn="+ISBN;
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
            JSONObject json = jsonObject.getJSONObject("data");
            name = json.getString("name");
            author = json.getString("author");
            publisher = json.getString("publisher");
            year = json.getString("publishingTime");
            imgSrc = json.getString("image");
            /*JSONObject jsonObject = new JSONObject(text);
            JSONObject json = jsonObject.getJSONObject("result");
            name = json.getString("title");
            author = json.getString("author");
            publisher = json.getString("publisher");
            year = json.getString("pubdate");
            imgSrc = json.getString("pic");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
