package student.jnu.com.bookshelf;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {
    static final String Database_name = "Book.db";  //数据库名称
    static final int Database_Version = 1;          //数据库版本
    SQLiteDatabase db;
    public int id_this;
    Cursor cursor;
    //定义表名和表结构

    DBConnection(Context context){
        super(context,Database_name,null,Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+ BookDB.BookTable.NAME +" ("
                + BookDB.BookTable.Cols.ID + " INTEGER primary key autoincrement, "
                + BookDB.BookTable.Cols.BOOK_NAME + " text, "
                + BookDB.BookTable.Cols.AUTHOR + " text,"
                + BookDB.BookTable.Cols.PRESS + " text,"
                + BookDB.BookTable.Cols.PUBLISHTIME_YEAR + " INTEGER,"
                + BookDB.BookTable.Cols.PUBLISHTIME_MONTH + " INTEGER,"
                + BookDB.BookTable.Cols.ISBN + " text,"
                + BookDB.BookTable.Cols.STATUS + " INTEGER,"
                + BookDB.BookTable.Cols.BOOKSHELF + " text,"
                + BookDB.BookTable.Cols.NOTE + " text,"
                + BookDB.BookTable.Cols.URL + " text,"
                + BookDB.BookTable.Cols.IMAGEURL + " text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
