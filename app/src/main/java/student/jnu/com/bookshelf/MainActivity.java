package student.jnu.com.bookshelf;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private int REQUEST_CODE_SCAN = 111;
    static List<Book> books = new ArrayList<>();
    static DBConnection helper;
    ListView booklist;
    BookListAdapter bookListAdapter;
    Cursor cursor;
    SQLiteDatabase db;
    public boolean isMore_add;
    static int i = 0;
    String[] items = new String[] { "作者", "标题", "出版社","出版时间" };

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, 1, 0, "删除" );
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String where = BookDB.BookTable.Cols.ID + "=" + books.get(itemInfo.position).getID();
        db.delete(BookDB.BookTable.NAME,where,null);

        books.remove(itemInfo.position);
        bookListAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);

                //editTextName.setText(content);
                //Toast.makeText(this,"点击发送" + content,Toast.LENGTH_SHORT).show();
                /*final BookCollection bookCollection = new BookCollection(content);

                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", bookCollection.getBook());
                        //editTextName.setText(bookCollection.getBook());
                    };
                };
                bookCollection.download(handler,content);*/
                i = 1;
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("isbn", content);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        }
        // 获取解析结果
        /*IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "ISBN:" + result.getContents(), Toast.LENGTH_LONG).show();
                //edit_ISBN.setText(result.getContents());//单次扫描

                i = 1;
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("isbn", result.getContents());
                intent.putExtras(bundle);
                startActivity(intent);
            }
               if (isMore_add) {//多次扫码
                    /*isMore_textView.setText(isMore_textView.getText() + "  " + result.getContents());
                    save(isMore_textView.getText().toString());*/
                    // 创建IntentIntegrator对象
                    /*IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                    //设置超时关闭扫描界面
//                intentIntegrator.setTimeout(10000);
                    intentIntegrator.setCaptureActivity(MyCustomCaptureActivity.class);
                    intentIntegrator.initiateScan();*/

                /*}


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        booklist = (ListView)findViewById(R.id.BookList);

        bookListAdapter = new BookListAdapter();
        booklist.setAdapter(bookListAdapter);
        registerForContextMenu(booklist);
        booklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,BookDetailActivity.class);
                intent.putExtra("books_index",position);
                startActivity(intent);
            }
        });

        helper = new DBConnection(MainActivity.this);
        db = helper.getWritableDatabase();
        cursor = db.query(BookDB.BookTable.NAME,null,null,null,null,null,null);
        books.clear();
        if(cursor.getCount()!=0){
            while (!cursor.isLast()){
                cursor.moveToNext();
                addTOBooks();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final Button batchAdd1=(Button)findViewById(R.id.batchAdd1) ;
        final Button singleAdd1=(Button)findViewById(R.id.singleAdd1) ;
        final FloatingActionButton batchAdd = (FloatingActionButton) findViewById(R.id.batchAdd);
        final FloatingActionButton singleAdd = (FloatingActionButton) findViewById(R.id.singleAdd);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        final Button writeAdd1=(Button)findViewById(R.id.writeAdd1) ;
        final FloatingActionButton writeAdd = (FloatingActionButton) findViewById(R.id.writeAdd);

        writeAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                //跳转编辑
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        batchAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                // 创建IntentIntegrator对象
                /*IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                //设置超时关闭扫描界面
//                intentIntegrator.setTimeout(10000);
                intentIntegrator.setCaptureActivity(MyCustomCaptureActivity.class);
                intentIntegrator.setPrompt("请对准二维码");// 设置提示语
                intentIntegrator.initiateScan();
                isMore_add = true;*/

            }
        });

        singleAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                config.setReactColor(R.color.colorWhite);//设置扫描框四个角的颜色 默认为白色
                config.setFrameLineColor(R.color.colorWhite);//设置扫描框边框颜色 默认无色
                config.setScanLineColor(R.color.colorWhite);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                isMore_add = false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.VISIBLE);
                singleAdd1.setVisibility(View.VISIBLE);
                writeAdd1.setVisibility(View.VISIBLE);
                batchAdd.show();
                singleAdd.show();
                writeAdd.show();
                fab.hide();
                fab1.show();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
            }
        });

        writeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                //跳转编辑
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        batchAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                // 创建IntentIntegrator对象
                /*IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                //设置超时关闭扫描界面
//                intentIntegrator.setTimeout(10000);
                intentIntegrator.setCaptureActivity(MyCustomCaptureActivity.class);
                intentIntegrator.setPrompt("请对准二维码");// 设置提示语
                intentIntegrator.initiateScan();
                isMore_add = true;*/
            }
        });

        singleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchAdd1.setVisibility(View.INVISIBLE);
                singleAdd1.setVisibility(View.INVISIBLE);
                writeAdd1.setVisibility(View.INVISIBLE);
                batchAdd.hide();
                singleAdd.hide();
                writeAdd.hide();
                fab.show();
                fab1.hide();
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                config.setReactColor(R.color.colorWhite);//设置扫描框四个角的颜色 默认为白色
                config.setFrameLineColor(R.color.colorWhite);//设置扫描框边框颜色 默认无色
                config.setScanLineColor(R.color.colorWhite);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                isMore_add = false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Spinner shelfChoose = (Spinner)menu.findItem(R.id.action_arrow).setActionView(R.layout.spinner).getActionView().findViewById(R.id.shelfType);
        shelfChoose.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.shelf,R.layout.dropdown);
        shelfChoose.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("排序")//设置对话框的标题
                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                            //dialog.dismiss();
                            switch (which){
                                case 0:
                                    Collections.sort(books, new Comparator<Book>() {
                                        @Override
                                        public int compare(Book o1, Book o2) {
                                            return Collator.getInstance(Locale.CHINA).compare(o1.getAuthor(),o2.getAuthor());
                                        }
                                    });
                                    bookListAdapter.notifyDataSetChanged();
                                    break;
                                case 1:
                                    Collections.sort(books, new Comparator<Book>() {
                                        @Override
                                        public int compare(Book o1, Book o2) {
                                            return Collator.getInstance(Locale.CHINA).compare(o1.getBookName(),o2.getBookName());
                                        }
                                    });
                                    bookListAdapter.notifyDataSetChanged();
                                    break;
                                case 2:
                                    Collections.sort(books, new Comparator<Book>() {
                                        @Override
                                        public int compare(Book o1, Book o2) {
                                            return Collator.getInstance(Locale.CHINA).compare(o1.getPress(),o2.getPress());
                                        }
                                    });
                                    bookListAdapter.notifyDataSetChanged();
                                    break;
                                case 3:
                                    Collections.sort(books, new Comparator<Book>() {
                                        @Override
                                        public int compare(Book o1, Book o2) {
                                            int time1 = o1.getPublishTime_Year() * 100 + o1.getPublishTime_Month();
                                            int time2 = o2.getPublishTime_Year() * 100 + o2.getPublishTime_Month();
                                            return  time1-time2;
                                        }
                                    });
                                    bookListAdapter.notifyDataSetChanged();
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_book) {
            // Handle the camera action
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_lable) {

        } else if (id == R.id.nav_donate) {

        } else if (id == R.id.nav_set) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
            //Toast.makeText(BookShelfActivity.this,"点击发送",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*private class mItemClick implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(MainActivity.this,BookDetailActivity.class);
            startActivity(intent);
        }
    }*/

    //booklist的适配器
    class BookListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.listview_item,parent,false);
            ImageView bookImage = (ImageView)view.findViewById(R.id.bookImage);
            TextView bookname_text = (TextView)view.findViewById(R.id.bookName_text);
            TextView authorPress_name = (TextView)view.findViewById(R.id.authorPress_text);
            TextView publishTime_text = (TextView)view.findViewById(R.id.publishTime_text);
            bookImage.setImageResource(R.mipmap.ic_launcher);


            bookname_text.setText(books.get(position).getBookName());
            authorPress_name.setText(books.get(position).getAuthor()+" 著 "+books.get(position).getPress());
            publishTime_text.setText(String.valueOf(books.get(position).getPublishTime_Year())+"-"+String.valueOf(books.get(position).getPublishTime_Month()));
            return view;
            //return null;
        }
    }

    //将cursor的内容添加到Books中
    public void addTOBooks(){
        Book book = new Book();
        book.setID(cursor.getInt(0));
        book.setBookName(cursor.getString(1));
        book.setAuthor(cursor.getString(2));
        book.setPress(cursor.getString(3));
        book.setPublishTime_Year(cursor.getInt(4));
        book.setPublishTime_Month(cursor.getInt(5));
        book.setISBN(cursor.getString(6));
        book.setStatus(cursor.getInt(7));
        book.setBookshelf(cursor.getString(8));
        book.setNote(cursor.getString(9));
        book.setUrl(cursor.getString(10));
        book.setImageUrl(cursor.getString(11));
        books.add(book);
    }

    //Activity的生命周期
    @Override
    protected void onRestart() {
        super.onRestart();
        books.clear();
        db = helper.getWritableDatabase();
        cursor = db.query(BookDB.BookTable.NAME,null,null,null,null,null,null);
        if(cursor.getCount()!=0){
            while (!cursor.isLast()){
                cursor.moveToNext();
                addTOBooks();
            }
        }
        bookListAdapter.notifyDataSetChanged();
    }

    public void save(String inputText) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //String inputText = isMore_textView.getText().toString();
        //save(inputText);
    }

}
