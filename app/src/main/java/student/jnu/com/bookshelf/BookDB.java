package student.jnu.com.bookshelf;

public class BookDB {
    public static final class BookTable {
        public static final String NAME = "Books";

        public static final class Cols {
            public static final String ID = "id";
            public static final String BOOK_NAME = "book_name";
            public static final String AUTHOR = "author";
            public static final String PRESS = "press";
            public static final String PUBLISHTIME_YEAR = "publishtime_year";
            public static final String PUBLISHTIME_MONTH = "publishtime_month";
            public static final String ISBN = "isbn";
            public static final String STATUS = "status";
            public static final String BOOKSHELF = "bookshelf";
            public static final String NOTE = "note";
            public static final String URL = "url";
            public static final String IMAGEURL = "imageurl";

        }
    }
    public static final class BookShelfTable{
        public static final String NAME = "BookShelfs";
        public static final class Cols{
            public static final String BOOKSHELF_NAME = "bookshelf_name";
        }
    }
}
