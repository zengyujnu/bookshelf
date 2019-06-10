package student.jnu.com.bookshelf;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends AppCompatActivity{

    private int books_index;
    private TextView authorText,pressText,isbnText,statusText,bookshelfText,noteText,lableText,urlText;
    private ImageView image;
    private Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        authorText = (TextView)findViewById(R.id.author_text);
        pressText = (TextView)findViewById(R.id.press_text);
        isbnText = (TextView)findViewById(R.id.isbn_text);
        statusText = (TextView)findViewById(R.id.status_text);
        bookshelfText = (TextView)findViewById(R.id.bookshelf_text);
        noteText = (TextView)findViewById(R.id.note_text);
        lableText = (TextView)findViewById(R.id.lable_text);
        urlText = (TextView)findViewById(R.id.url_text);
        image = (ImageView)findViewById(R.id.image);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        books_index = getIntent().getIntExtra("books_index",0);

        try{
            byte[] decode = android.util.Base64.decode(MainActivity.books.get(books_index).getImageUrl(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            image.setImageBitmap(bitmap);
            image.setScaleType(ImageView.ScaleType.CENTER);
            //bookImage.setImageResource(R.mipmap.ic_launcher);
        }catch (Exception E){}
        toolbar.setTitle(MainActivity.books.get(books_index).getBookName());
        authorText.setText(MainActivity.books.get(books_index).getAuthor());
        pressText.setText(MainActivity.books.get(books_index).getPress());
        isbnText.setText(MainActivity.books.get(books_index).getISBN());
        switch (MainActivity.books.get(books_index).getStatus()){
            case Book.NOTSETUP:
                statusText.setText("阅读状态未设置");
                break;
            case Book.UNREAD:
                statusText.setText("未读");
                break;
            case Book.READING:
                statusText.setText("在读读");
                break;
            case Book.ALREADYREAD:
                statusText.setText("已读");
                break;
        }
        bookshelfText.setText(MainActivity.books.get(books_index).getBookshelf());
        noteText.setText(MainActivity.books.get(books_index).getNote());
        //lableText.setText(BookShelfActivity.books.get(books_index).get);
        urlText.setText(MainActivity.books.get(books_index).getUrl());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookshelf_detail_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings1) {
//            Toast.makeText(BookDetailActivity.this,"这里是菜单1",Toast.LENGTH_SHORT).show();
            deleteInDB(MainActivity.books.get(books_index).getID());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void deleteInDB(int idInDB){
        SQLiteDatabase db = MainActivity.helper.getWritableDatabase();
        String where = BookDB.BookTable.Cols.ID + "=" + idInDB;
        db.delete(BookDB.BookTable.NAME,where,null);
        db.close();
    }
}

