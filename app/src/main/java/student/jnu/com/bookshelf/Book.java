package student.jnu.com.bookshelf;

public class Book {
    public static final int NOTSETUP = 0;
    public static final int UNREAD = 1;
    public static final int READING = 2;
    public static final int ALREADYREAD = 3;


    private int ID;
    private String BookName;
    private String Author;
    private String Press;
    private int PublishTime_Year;
    private int PublishTime_Month;
    private String ISBN;
    private int Status;
    private String Bookshelf;
    private String Note;
    private String Url;
    private String ImageUrl;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setPress(String press) {
        Press = press;
    }

    public void setPublishTime_Year(int publishTime_Year) {
        PublishTime_Year = publishTime_Year;
    }

    public void setPublishTime_Month(int publishTime_Month) {
        PublishTime_Month = publishTime_Month;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setBookshelf(String bookshelf) {
        Bookshelf = bookshelf;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getID() {
        return ID;
    }

    public String getBookName() {
        return BookName;
    }

    public String getAuthor() {
        return Author;
    }

    public String getPress() {
        return Press;
    }

    public int getPublishTime_Year(){
        return PublishTime_Year;
    }

    public int getPublishTime_Month(){
        return PublishTime_Month;
    }

    public String getISBN(){
        return ISBN;
    }

    public int getStatus(){
        return Status;
    }

    public String getBookshelf(){
        return Bookshelf;
    }

    public String getNote(){
        return  Note;
    }

    public String getUrl() {
        return Url;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}
