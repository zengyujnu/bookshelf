package student.jnu.com.bookshelf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity{
    //private SimpleToolbar mSimpleToolbar;
    public List<String> list1 = new ArrayList<String>();
    public List<String> list2 = new ArrayList<String>();

    private Spinner spinner;
    private Spinner spinner_shelf;
    private Spinner spinner_label;
    private String tmpBookShelfName;
    private int tmpStatus;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_shelf;
    ArrayAdapter<String> adapter_label;
    private List<String> list;

    int k=1;


    EditText edit_title ;
    EditText edit_author ;
    EditText edit_publish ;
    EditText edit_year ;
    EditText edit_ISBN ;
    EditText edit_note ;
    EditText edit_website ;
    ImageView imageView;
    static String image;
    int books_index;
    //private Button return_view;
    //private Button save_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_information);
        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_author = (EditText) findViewById(R.id.edit_author);
        edit_publish = (EditText) findViewById(R.id.edit_publish);
        edit_year = (EditText) findViewById(R.id.edit_year);
        edit_ISBN = (EditText) findViewById(R.id.edit_ISBN);
        edit_note = (EditText) findViewById(R.id.edit_note);
        edit_website = (EditText) findViewById(R.id.edit_website);
        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ediglog();
            }
        });
        spinner = (Spinner) findViewById(R.id.spiner1);
        spinner.setPrompt("阅读状态未设置");
        initDatas();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "您选择的状态是：" + list.get(position), Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        tmpStatus = Book.NOTSETUP;
                        break;
                    case 1:
                        tmpStatus = Book.UNREAD;
                        break;
                    case 2:
                        tmpStatus = Book.ALREADYREAD;
                        break;
                    case 3:
                        tmpStatus = Book.READING;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_shelf = (Spinner) findViewById(R.id.spiner2);
        //spinner_shelf.setPrompt("默认书架");
        initshelfDatas();
        adapter_shelf = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);
        spinner_shelf.setAdapter(adapter_shelf);
        spinner_shelf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (list1.size() - 1 != position) {
                    //Toast.makeText(MainActivity.this, "您选择的状态是：" + list1.get(position), Toast.LENGTH_SHORT).show();
                    tmpBookShelfName = list1.get(position);
                } else {
                    diglog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        spinner_label = (Spinner) findViewById(R.id.spiner3);
        // spinner_label.setPrompt("添加新标签");
        initlabelDatas();
        adapter_label = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list2);
        spinner_label.setAdapter(adapter_label);
        spinner_label.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (list2.size() - 1 != position) {
                    //Toast.makeText(MainActivity.this, "您选择的状态是：" + list1.get(position), Toast.LENGTH_SHORT).show();
                } else {
                    label_diglog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (MainActivity.intent1 == 1) {
            tmpBookShelfName = "默认书架";
            tmpStatus = Book.NOTSETUP;

            Bundle bundle = this.getIntent().getExtras();
            final String ISBN = bundle.getString("isbn");
            edit_ISBN.setText(ISBN);
            final BookCollection bookCollection = new BookCollection();
            @SuppressLint("HandlerLeak")
            Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    try{
                        edit_title.setText(bookCollection.getBook());
                        edit_author.setText(bookCollection.getAuthor());
                        edit_publish.setText(bookCollection.getPublisher());
                        edit_year.setText(bookCollection.getYear());
                        //edit_note.setText(bookCollection.getImgSrc());
                        image = bookCollection.getImgSrc();
                        byte[] decode = Base64.decode(bookCollection.getImgSrc(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        imageView.setImageBitmap(bitmap);
                        //DownImage downImage = new DownImage();
                        //downImage.download(imageView,ISBN,"http://app2.showapi.com/isbn/img1/eaa363cbced8474e992dea310faf176d.jpg");
                    }catch (Exception E){}
                };
            };
            bookCollection.download(handler,ISBN);
            //edit_title.setText(bookCollection.getBook());

        }

        if(MainActivity.intent1 == 2){
            Bundle bundle = this.getIntent().getExtras();
            books_index = bundle.getInt("books_index");
            try{
                byte[] decode = android.util.Base64.decode(MainActivity.books.get(books_index).getImageUrl(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                //bookImage.setImageResource(R.mipmap.ic_launcher);
            }catch (Exception E){}
            edit_title.setText(MainActivity.books.get(books_index).getBookName());
            edit_author.setText(MainActivity.books.get(books_index).getAuthor());
            edit_publish.setText(MainActivity.books.get(books_index).getPress());
            edit_ISBN.setText(MainActivity.books.get(books_index).getISBN());
            edit_year.setText(String.valueOf(MainActivity.books.get(books_index).getPublishTime_Year()));

            //bookshelfText.setText(MainActivity.books.get(books_index).getBookshelf());
            edit_note.setText(MainActivity.books.get(books_index).getNote());
            //lableText.setText(BookShelfActivity.books.get(books_index).get);
            edit_website.setText(MainActivity.books.get(books_index).getUrl());
            tmpBookShelfName =MainActivity.books.get(books_index).getBookshelf();
            tmpStatus = MainActivity.books.get(books_index).getStatus();
            spinner.setSelection(tmpStatus);
            for(int i=0;i<list1.size();i++){
                if(list1.get(i).equals(tmpBookShelfName)){
                    spinner_shelf.setSelection(i);
                    break;
                }
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                imageView.setImageURI(uri);
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = getBitmap(drawable);
                image = getBitmapStrBase64(bitmap);
            }
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap 通过Base64 转换为字符串
     * @param bitmap
     * @return
     */
    private String getBitmapStrBase64(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bytes = bos.toByteArray();
        String string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }


    private void initDatas() {
        list = new ArrayList<String>();
        list.add("阅读状态未设置");
        list.add("未读");
        list.add("在读");
        list.add("已读");

    }

    private void initshelfDatas() {
        list1 = new ArrayList<String>();
        for(int i=1;i<MainActivity.bookshelfs.size();i++){
            list1.add(MainActivity.bookshelfs.get(i));
        }
        list1.add("添加新书架");


    }

    private void initlabelDatas() {
        list2 = new ArrayList<String>();
        list2.add("未设置标签");
        list2.add("添加新标签");


    }
    public void diglog()

    {
        View view = getLayoutInflater().inflate(R.layout.new_shelf, null);
        final EditText editText = (EditText) view.findViewById(R.id.editText_shelfname);

        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle("添加书架")//设置对话框的标题
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = editText.getText().toString();

                        //把书架添加到数据库中
                        SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(BookDB.BookShelfTable.Cols.BOOKSHELF_NAME,content);
                        db.insert(BookDB.BookShelfTable.NAME,null,values);
                        db.close();

                        //把书架添加到MainActivity的bookshelfs中
                        MainActivity.bookshelfs.add(content);

                        //把书架添加到该Activity中的list1中
                        list1.add(list1.size()-1, content);
                        ((BaseAdapter)spinner_shelf.getAdapter()).notifyDataSetChanged();
                        spinner_shelf.setSelection(list1.size()-2);
                        tmpBookShelfName = list1.get(list1.size()-2);

                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    public void label_diglog()

    {
        View view = getLayoutInflater().inflate(R.layout.new_label, null);
        final EditText editText = (EditText) view.findViewById(R.id.editText_labelname);

        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle("添加新标签")//设置对话框的标题

                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = editText.getText().toString();
                        list2.add(k, content);
                        k= k + 1;
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public void ediglog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle("舍弃书籍")//设置对话框的标题
                .setMessage("我们将不会保存此书籍信息，书籍信息将被舍弃，确认要继续此操作吗？")//设置对话框的内容
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
                        /*Intent intent = new Intent(EditActivity.this,MainActivity.class);
                        startActivity(intent);*/
                        finish();
                    }
                }).create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    public void save(){
        SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BookDB.BookTable.Cols.BOOK_NAME,String.valueOf(edit_title.getText()));
        values.put(BookDB.BookTable.Cols.AUTHOR,String.valueOf(edit_author.getText()));
        values.put(BookDB.BookTable.Cols.PRESS,String.valueOf(edit_publish.getText()));
        if(edit_year.getText().toString().length()==0)
            values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,0);
        else
            values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,Integer.valueOf(edit_year.getText().toString()));
        values.put(BookDB.BookTable.Cols.PUBLISHTIME_MONTH,0);
        values.put(BookDB.BookTable.Cols.ISBN,String.valueOf(edit_ISBN.getText()));
        values.put(BookDB.BookTable.Cols.STATUS,tmpStatus);
        values.put(BookDB.BookTable.Cols.BOOKSHELF,tmpBookShelfName);
        values.put(BookDB.BookTable.Cols.NOTE,String.valueOf(edit_note.getText()));
        values.put(BookDB.BookTable.Cols.URL,"http://119.29.3.47:9001/book/worm/isbn?isbn=");
        values.put(BookDB.BookTable.Cols.IMAGEURL,image);
        db.insert(BookDB.BookTable.NAME,null,values);
        db.close();
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
                Toast.makeText(EditActivity.this, "修改成功" , Toast.LENGTH_SHORT).show();
                SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(BookDB.BookTable.Cols.BOOK_NAME,edit_title.getText().toString());
                values.put(BookDB.BookTable.Cols.AUTHOR,edit_author.getText().toString());
                values.put(BookDB.BookTable.Cols.PRESS,edit_publish.getText().toString());
                values.put(BookDB.BookTable.Cols.ISBN,edit_ISBN.getText().toString());
                values.put(BookDB.BookTable.Cols.NOTE,edit_note.getText().toString());
                values.put(BookDB.BookTable.Cols.URL,edit_website.getText().toString());
                if(edit_year.getText().toString().length()==0)
                    values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,0);
                else
                    values.put(BookDB.BookTable.Cols.PUBLISHTIME_YEAR,Integer.valueOf(edit_year.getText().toString()));
                values.put(BookDB.BookTable.Cols.STATUS,tmpStatus);
                values.put(BookDB.BookTable.Cols.BOOKSHELF,tmpBookShelfName);
                values.put(BookDB.BookTable.Cols.IMAGEURL,image);
                String where = BookDB.BookTable.Cols.ID + " = " + MainActivity.books.get(books_index).getID();
                db.update(BookDB.BookTable.NAME,values,where,null);
                db.close();
                MainActivity.intent1 = 0;
                this.finish();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(EditActivity.this, "保存成功" , Toast.LENGTH_SHORT).show();
                save();
                MainActivity.intent1 = 0;

                //Intent intent = new Intent(EditActivity.this,MainActivity.class);
                //startActivity(intent);
                this.finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
