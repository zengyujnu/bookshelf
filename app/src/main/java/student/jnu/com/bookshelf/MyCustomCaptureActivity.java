package student.jnu.com.bookshelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;

import static student.jnu.com.bookshelf.MainActivity.helper;

public class MyCustomCaptureActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    ListView booklist;
    static List<Book> books = new ArrayList<Book>();

    BookListAdapter bookListAdapter;
    SQLiteDatabase db;

    public class BookListAdapter extends BaseAdapter {

        //将cursor的内容添加到Books中
        public void addTOBooks(final BookCollection bookCollection) {
            //book.setID(cursor.getInt(0));
            Book book = new Book();
            book.setBookName(bookCollection.getBook());
            book.setAuthor(bookCollection.getAuthor());
            book.setPress(bookCollection.getPublisher());
            book.setPublishTime_Year(Integer.valueOf(bookCollection.getYear()));
            book.setPublishTime_Month(0);
            book.setISBN(MainActivity.isbn);
            //book.setStatus(cursor.getInt(7));
            //book.setBookshelf(cursor.getString(8));
            //book.setNote(cursor.getString(9));
            book.setUrl("http://119.29.3.47:9001/book/worm/isbn?isbn=");
            book.setImageUrl(bookCollection.getImgSrc());
            books.add(book);
        }
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
            View view = LayoutInflater.from(MyCustomCaptureActivity.this).inflate(R.layout.listview_item, parent, false);
            ImageView bookImage = (ImageView) view.findViewById(R.id.bookImage);
            TextView bookname_text = (TextView) view.findViewById(R.id.bookName_text);
            TextView authorPress_name = (TextView) view.findViewById(R.id.authorPress_text);
            TextView publishTime_text = (TextView) view.findViewById(R.id.publishTime_text);
            try {
                byte[] decode = android.util.Base64.decode(books.get(position).getImageUrl(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                bookImage.setImageBitmap(bitmap);
                //bookImage.setImageResource(R.mipmap.ic_launcher);
            } catch (Exception E) {
            }

            bookname_text.setText(books.get(position).getBookName());
            authorPress_name.setText(books.get(position).getAuthor() + "    " + books.get(position).getPress());
            //publishTime_text.setText(String.valueOf(books.get(position).getPublishTime_Year())+"-"+String.valueOf(books.get(position).getPublishTime_Month()));
            publishTime_text.setText(String.valueOf(books.get(position).getPublishTime_Year()) + "  ");
            return view;
            //return null;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, 1, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        /*String where = BookDB.BookTable.Cols.ID + "=" + books.get(itemInfo.position).getID();
        db.delete(BookDB.BookTable.NAME, where, null);*/
        books.remove(itemInfo.position);
        bookListAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

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


        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        booklist = (ListView) findViewById(R.id.BookList);

        bookListAdapter = new BookListAdapter();
        booklist.setAdapter(bookListAdapter);
        bookListAdapter.notifyDataSetChanged();
        registerForContextMenu(booklist);
        if (MainActivity.isbn != null) {
            handle(MainActivity.isbn);
        }
    }

    public void ediglog() {
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
                        dialog.dismiss();
                        MainActivity.isMore_add = false;
                        books.clear();
                        Intent intent = new Intent(MyCustomCaptureActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create();
        dialog.show();

    }

    public void addtoDB(){
        int position ;
        for(position=0;position<books.size();position++){
            SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BookDB.BookTable.Cols.BOOK_NAME, books.get(position).getBookName());
            values.put(BookDB.BookTable.Cols.AUTHOR, books.get(position).getAuthor());
            values.put(BookDB.BookTable.Cols.PRESS, books.get(position).getPress());
            values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR, books.get(position).getPublishTime_Year());
            values.put(BookDB.BookTable.Cols.PUBLISHTIME_MONTH, "");
            values.put(BookDB.BookTable.Cols.ISBN, books.get(position).getISBN());
            values.put(BookDB.BookTable.Cols.STATUS, Book.NOTSETUP);
            values.put(BookDB.BookTable.Cols.BOOKSHELF, "默认书架");
            values.put(BookDB.BookTable.Cols.NOTE, books.get(position).getNote());
            values.put(BookDB.BookTable.Cols.URL, books.get(position).getUrl());
            values.put(BookDB.BookTable.Cols.IMAGEURL, books.get(position).getImageUrl());
            db.insert(BookDB.BookTable.NAME, null, values);
            db.close();
        }
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
            addtoDB();
            books.clear();
            MainActivity.isMore_add = false;
            Intent intent = new Intent(MyCustomCaptureActivity.this, MainActivity.class);
            startActivity(intent);
            finish();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handle(final String ISBN) {
        final BookCollection bookCollection = new BookCollection();
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    bookListAdapter.addTOBooks(bookCollection);
                    bookListAdapter.notifyDataSetChanged();
                    MainActivity.isbn = null;
                } catch (Exception E) {
                }
            }
        };
        bookCollection.download(handler, ISBN);
    }
}