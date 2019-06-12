package student.jnu.com.bookshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyCustomCaptureActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    //private EditText ISBN_SHOW;
    //private Button ISBN;
    private boolean isShow=false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_custom_capture);// 自定义布局
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ediglog();
            }
        });
        //ISBN= (Button) findViewById(R.id.ISBN);
        //ISBN_SHOW=(EditText)findViewById(R.id.ISBN_SHOW);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        /*ISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从文件中读取文本信息
                String inputText=load();
                ISBN_SHOW.setText(inputText);
            }
        });*/
//        //从文件中读取文本信息
//        String inputText=load();
//        ISBN_SHOW.setText(inputText);

    }

    public void ediglog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle("舍弃书籍")//设置对话框的标题
                .setMessage("我们将不会保存这些书籍信息，书籍信息将被舍弃，确认要继续此操作吗？")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.intent1 = 0;
                        dialog.dismiss();
                        Intent intent = new Intent(MyCustomCaptureActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create();
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    public String load(){
        FileInputStream in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            if(MainActivity.intent1 == 2){
                /*Toast.makeText(EditActivity.this, "修改成功" , Toast.LENGTH_SHORT).show();
                SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(BookDB.BookTable.Cols.BOOK_NAME,edit_title.getText().toString());
                values.put(BookDB.BookTable.Cols.AUTHOR,edit_author.getText().toString());
                values.put(BookDB.BookTable.Cols.PRESS,edit_publish.getText().toString());
                values.put(BookDB.BookTable.Cols.ISBN,edit_ISBN.getText().toString());
                values.put(BookDB.BookTable.Cols.NOTE,edit_note.getText().toString());
                values.put(BookDB.BookTable.Cols.URL,edit_website.getText().toString());
                values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,Integer.valueOf(edit_year.getText().toString()));
                String where = BookDB.BookTable.Cols.ID + " = " + MainActivity.books.get(books_index).getID();
                db.update(BookDB.BookTable.NAME,values,where,null);
                db.close();
                MainActivity.intent1 = 0;
                this.finish();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);*/
            }
            else{
                /*Toast.makeText(EditActivity.this, "保存成功" , Toast.LENGTH_SHORT).show();
                SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(BookDB.BookTable.Cols.BOOK_NAME,String.valueOf(edit_title.getText()));
                values.put(BookDB.BookTable.Cols.AUTHOR,String.valueOf(edit_author.getText()));
                values.put(BookDB.BookTable.Cols.PRESS,String.valueOf(edit_publish.getText()));
                values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,Integer.valueOf(edit_year.getText().toString()));
                values.put(BookDB.BookTable.Cols.PUBLISHTIME_MONTH," ");
                values.put(BookDB.BookTable.Cols.ISBN,String.valueOf(edit_ISBN.getText()));
                values.put(BookDB.BookTable.Cols.STATUS,Book.NOTSETUP);
                values.put(BookDB.BookTable.Cols.BOOKSHELF,"默认书架");
                values.put(BookDB.BookTable.Cols.NOTE,String.valueOf(edit_note.getText()));
                values.put(BookDB.BookTable.Cols.URL,"http://119.29.3.47:9001/book/worm/isbn?isbn=");
                values.put(BookDB.BookTable.Cols.IMAGEURL,image);
                db.insert(BookDB.BookTable.NAME,null,values);
                db.close();
                MainActivity.intent1 = 0;

                //Intent intent = new Intent(EditActivity.this,MainActivity.class);
                //startActivity(intent);
                this.finish();*/
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}