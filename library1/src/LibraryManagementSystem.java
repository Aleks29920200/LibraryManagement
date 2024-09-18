import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagementSystem extends JFrame {
    private JTabbedPane tabbedPane;
    private DatabaseManager databaseManager;
    private JComboBox<String> authorComboBox = new JComboBox<>();
   private JComboBox<String> bookComboBox = new JComboBox<>();
    private Book selectedBook = null;
    private Author selectedAuthor = null;
    private Reader selectedReader = null;
    private JComboBox<String> readerComboBox = new JComboBox<>();
    static class Author {
        private String name;
        private String nationality;
        private Integer birthYear;

        public Author() {
        }

        public Author(String name, String nationality, Integer birthYear) {
            this.name = name;
            this.nationality = nationality;
            this.birthYear = birthYear;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public Integer getBirthYear() {
            return birthYear;
        }

        public void setBirthYear(Integer birthYear) {
            this.birthYear = birthYear;
        }

    }
     static class Book {
        private Integer bookId;
        private String isbn;
        private String title;
        private Author author;
        private boolean available;

        private String genre;

        public Book() {
        }

        public Book(String isbn, String title, Author author, boolean available, String genre) {
            this.isbn = isbn;
            this.title = title;
            this.author = new Author();
            this.available = available;
            this.genre = genre;
        }

         public Book(String title, String isbn, String genre) {
            this.title=title;
            this.isbn=isbn;
            this.genre=genre;
         }


         public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        @Override
        public String toString() {
            return title + " (ISBN: " + isbn + ")";
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

         public Integer getBookId() {
             return bookId;
         }

         public void setBookId(Integer bookId) {
             this.bookId = bookId;
         }
     }
    public class Reader {
        private int id;
        private String name;
        private String address;

        public Reader() {
        }

        public Reader(String name, String address) {
            this.name = name;
            this.address = address;
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        // toString method to represent Reader object as a string
        @Override
        public String toString() {
            return "Reader{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }
    static class BorrowBook{
        private Book book;
        private Reader reader;


        public BorrowBook() {
        }

        public BorrowBook(int readerId, int bookId) {
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public Reader getReader() {
            return reader;
        }

        public void setReader(Reader reader) {
            this.reader = reader;
        }
    }

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        databaseManager = new DatabaseManager();

        JPanel addBookPanel = createAddBookPanel();
        JPanel removeBookPanel = createRemoveBookPanel();
        JPanel updateBookPanel = createUpdateBookPanel();
        JPanel addAuthorPanel = createAddAuthorPanel();
        JPanel removeAuthorPanel = createRemoveAuthorPanel();
        JPanel updateAuthorPanel = createUpdateAuthorPanel();
        JPanel addReaderPanel = createAddReaderPanel();
        JPanel removeReaderPanel = createRemoveReaderPanel();
        JPanel updateReaderPanel = createUpdateReaderPanel();
        JPanel borrowBookPanel = createBorrowBookPanel();
        JPanel searchPanel = createSearchPanel();

        tabbedPane.addTab("Add Book", addBookPanel);
        tabbedPane.addTab("Remove Book", removeBookPanel);
        tabbedPane.addTab("Update Book", updateBookPanel);
        tabbedPane.addTab("Add Author", addAuthorPanel);
        tabbedPane.addTab("Remove Author", removeAuthorPanel);
        tabbedPane.addTab("Update Author", updateAuthorPanel);
        tabbedPane.addTab("Add Reader", addReaderPanel);
        tabbedPane.addTab("Remove Reader", removeReaderPanel);
        tabbedPane.addTab("Update Reader", updateReaderPanel);
        tabbedPane.addTab("Borrow Book", borrowBookPanel);
        tabbedPane.addTab("Search", searchPanel);

        add(tabbedPane);

        setVisible(true);
    }

    public <T> List<T> getDataForGivenTable(String tableName, Class<T> type) throws SQLException {
        List<T> dataList = new ArrayList<>();
        String query="";
        if(tableName.equals("books")){
            query="SELECT b.*,a.* FROM books b JOIN authors a on a.author_id = b.author_id";
        }else if(tableName.equals("borrowedbooks")){
            query="SELECT b.*,bb.*,r.* FROM books b JOIN borrowedbooks bb on bb.book_id=b.book_id " +
                    "JOIN readers r on r.id = bb.id";
        }
        else{
             query = "SELECT * FROM " + tableName;
        }

        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            
            while (resultSet.next()) {
                if (type == Author.class) {
                    Author author = new Author();
                    author.setName(resultSet.getString("name"));
                    author.setNationality(resultSet.getString("nationality"));
                    author.setBirthYear(resultSet.getInt("birth_year"));
                    dataList.add(type.cast(author));
                } else if (type == Book.class) {
                    Book book = new Book();
                    Author author=new Author(resultSet.getString("name"),resultSet.getString("nationality"),resultSet.getInt("birth_year"));
                    book.setTitle(resultSet.getString("title"));
                    book.setGenre(resultSet.getString("genre"));
                    book.setIsbn(resultSet.getString("ISBN"));
                    book.setAuthor(author);
                    // Populate other fields as needed
                    dataList.add(type.cast(book));
                } else if (type == Reader.class) {
                    Reader reader = new Reader();
                    reader.setName(resultSet.getString("name"));
                    reader.setAddress(resultSet.getString("address"));
                    // Populate other fields as needed
                    dataList.add(type.cast(reader));
                }else if(type==BorrowBook.class){
                    BorrowBook borrowBook=new BorrowBook();
                    Book book=new Book(resultSet.getString("title"),resultSet.getString("ISBN"),resultSet.getString("genre"));
                    Reader reader=new Reader(resultSet.getString("name"),resultSet.getString("address"));
                    borrowBook.setBook(book);
                    borrowBook.setReader(reader);
                    dataList.add(type.cast(borrowBook));
                }
            }
        }
        return dataList;
    }

    public <T> void displayDataInTable(String tableName, Class<T> type) {
        JFrame frame = new JFrame("Data Display");

        frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);


        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);


        if (type == Author.class) {
            tableModel.addColumn("Name");
            tableModel.addColumn("Nationality");
            tableModel.addColumn("Birth Year");
        } else if (type == Book.class) {
            tableModel.addColumn("Title");
            tableModel.addColumn("Genre");
            tableModel.addColumn("ISBN");
            tableModel.addColumn("Author name");
            tableModel.addColumn("Author nationality");
            tableModel.addColumn("Author birthYear");
            // Add other columns as needed
        } else if (type == Reader.class) {
            tableModel.addColumn("Name");
            tableModel.addColumn("Address");
            // Add other columns as needed
        }else if(type== BorrowBook.class){
            tableModel.addColumn("book title");
            tableModel.addColumn("book ISBN");
            tableModel.addColumn("book genre");
            tableModel.addColumn("reader name");
            tableModel.addColumn("reader address");
        }

        try {
            // Fetch data from the database
            List<?> dataList = getDataForGivenTable(tableName, type);


            for (Object obj : dataList) {
                if (type == Author.class) {
                    Author author = (Author) obj;
                    tableModel.addRow(new Object[]{author.getName(), author.getNationality(), author.getBirthYear()});
                } else if (type == Book.class) {
                    Book book = (Book) obj;
                    tableModel.addRow(new Object[]{book.getTitle(), book.getGenre(), book.getIsbn(), book.getAuthor().getName(),book.getAuthor().getNationality(),book.getAuthor().getBirthYear()});
                } else if (type == Reader.class) {
                    Reader reader = (Reader) obj;
                    tableModel.addRow(new Object[]{reader.getName(), reader.getAddress()});
                }else if(type==BorrowBook.class){
                    BorrowBook borrowBook=(BorrowBook) obj;
                    tableModel.addRow(new Object[]{borrowBook.getBook().getTitle(),borrowBook.getBook().getIsbn(),borrowBook.getBook().getGenre(),borrowBook.getReader().getName(),borrowBook.getReader().getAddress()});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }


        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        try {
            getDataForGivenTable("books",Book.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        JTextField titleField = new JTextField();
        JTextField authorNameField = new JTextField();
        JTextField authorNationalityField = new JTextField();
        JTextField authorBirthYearField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField genreField = new JTextField();
        JButton addButton = new JButton("Add");

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author Name:"));
        panel.add(authorNameField);
        panel.add(new JLabel("Author Nationality:"));
        panel.add(authorNationalityField);
        panel.add(new JLabel("Author Birth Year:"));
        panel.add(authorBirthYearField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String authorName = authorNameField.getText();
                String authorNationality = authorNationalityField.getText();
                int authorBirthYear = Integer.parseInt(authorBirthYearField.getText());
                String isbn = isbnField.getText();
                String genre=genreField.getText();

                Author author = new Author(authorName, authorNationality, authorBirthYear);


                if (!title.isEmpty() && !authorName.isEmpty() && !authorNationality.isEmpty() && !isbn.isEmpty()) {
                    databaseManager.addBook(title, authorName,authorNationality,authorBirthYear, isbn,genre);
                    try {
                        List<Book> books = getDataForGivenTable("books", Book.class);
                        bookComboBox.addItem(books.get(books.size()-1).getTitle());
                        selectedBook=books.get(books.size()-1);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("books",Book.class);
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Book added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields(titleField, authorNameField, authorNationalityField, authorBirthYearField, isbnField,genreField);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createRemoveBookPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        try {
            getDataForGivenTable("books",Book.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        JTextField isbnField = new JTextField();
        JButton removeButton = new JButton("Remove");

        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(removeButton);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = isbnField.getText();
                Book bookByISBN;
                if (!isbn.isEmpty()) {
                    try {
                         bookByISBN = getBookByISBN(isbn);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    Integer bookId = bookByISBN.getBookId();
                    databaseManager.removeBook(bookId);
                    try {
                        getDataForGivenTable("books", Book.class);
                        bookComboBox.removeItem(isbn);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("books",Book.class);
                    clearFields(isbnField);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please enter ISBN.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*,a.* FROM Books b JOIN authors a on a.author_id = b.author_id";
        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String isbn = resultSet.getString("ISBN");
                String title = resultSet.getString("title");
                int authorId = resultSet.getInt("author_id");
                String nameAuthor = resultSet.getString("name");
                String nationality = resultSet.getString("nationality");
                Integer birthYear=resultSet.getInt("birth_year");
                boolean available = resultSet.getBoolean("available");
                String genre=resultSet.getString("genre");
                Author author = new Author(nameAuthor, nationality, birthYear);
                Book book = new Book();
                book.setIsbn(isbn);
                book.setTitle(title);
                book.setGenre(genre);
                book.setAuthor(author);
                book.setAvailable(available);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void updateTitleInComboBox(JComboBox<String> comboBox, String oldTitle, String newTitle, Book selectedBook) {
        int itemCount = comboBox.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (comboBox.getItemAt(i).equals(oldTitle)) {
                if(newTitle!=null) {
                    comboBox.removeItemAt(i);
                    comboBox.insertItemAt(newTitle, i);
                    comboBox.setSelectedItem(newTitle);
                    selectedBook.setTitle(newTitle);
                    break;
                }
            }
        }
    }

    private JPanel createUpdateBookPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2)); // Increased rows to accommodate additional fields

        try {
            getDataForGivenTable("books",Book.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Book> books = getAllBooks();


        for (Book book : books) {
            bookComboBox.addItem(book.getTitle());
        }

        JTextField titleField = new JTextField();
        JTextField authorNameField = new JTextField();
        JTextField authorNationalityField = new JTextField();
        JTextField authorBirthYearField = new JTextField();
        JTextField genreField=new JTextField();
        JCheckBox titleCheckbox = new JCheckBox("Update Title");
        JCheckBox authorNameCheckbox = new JCheckBox("Update Author Name");
        JCheckBox authorNationalityCheckbox = new JCheckBox("Update Author Nationality");
        JCheckBox authorBirthYearCheckbox = new JCheckBox("Update Author Birth Year");
        JCheckBox availabilityCheckbox = new JCheckBox("Update Availability");
        JCheckBox genreCheckbox = new JCheckBox("Update Genre");
        JButton updateButton = new JButton("Update");

        panel.add(new JLabel("Select Book:"));
        panel.add(bookComboBox);
        panel.add(titleCheckbox);
        panel.add(titleField);
        panel.add(authorNameCheckbox);
        panel.add(authorNameField);
        panel.add(authorNationalityCheckbox);
        panel.add(authorNationalityField);
        panel.add(authorBirthYearCheckbox);
        panel.add(authorBirthYearField);
        panel.add(genreCheckbox);
        panel.add(genreField);
        panel.add(availabilityCheckbox);
        panel.add(updateButton);

        // Disable text fields by default
        titleField.setEnabled(false);
        authorNameField.setEnabled(false);
        authorNationalityField.setEnabled(false);
        authorBirthYearField.setEnabled(false);
        genreField.setEnabled(false);

        // Add action listeners to checkboxes to enable/disable corresponding text fields
        titleCheckbox.addActionListener(e -> titleField.setEnabled(titleCheckbox.isSelected()));
        authorNameCheckbox.addActionListener(e -> authorNameField.setEnabled(authorNameCheckbox.isSelected()));
        authorNationalityCheckbox.addActionListener(e -> authorNationalityField.setEnabled(authorNationalityCheckbox.isSelected()));
        authorBirthYearCheckbox.addActionListener(e -> authorBirthYearField.setEnabled(authorBirthYearCheckbox.isSelected()));
        genreCheckbox.addActionListener(e -> genreField.setEnabled(genreCheckbox.isSelected()));

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTitle = (String) bookComboBox.getSelectedItem();

                for (Book book : books) {
                    if (book.getTitle().equals(selectedTitle)) {
                        selectedBook = book;
                        break;
                    }
                }
                if (selectedBook != null) {
                    String isbn = selectedBook.getIsbn();
                    String title = titleField.isEnabled() ? titleField.getText() : null;
                    String authorName = authorNameField.isEnabled() ? authorNameField.getText() : null;
                    String authorNationality = authorNationalityField.isEnabled() ? authorNationalityField.getText() : null;
                    Integer authorBirthYear = authorBirthYearField.isEnabled() ? Integer.parseInt(authorBirthYearField.getText()) : null;
                    String genre = genreField.isEnabled() ? genreField.getText() : null;
                    boolean available = availabilityCheckbox.isSelected();
                    updateTitleInComboBox(bookComboBox, selectedTitle, title, selectedBook);
                    databaseManager.updateBook(isbn, title, authorName, authorNationality, authorBirthYear,genre, available,
                            titleCheckbox.isSelected(), authorNameCheckbox.isSelected(), authorNationalityCheckbox.isSelected(),
                            authorBirthYearCheckbox.isSelected(),genreCheckbox.isSelected(), availabilityCheckbox.isSelected());
                    try {
                        getDataForGivenTable("books",Book.class);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("books",Book.class);
                    clearFields(titleField, authorNameField, authorNationalityField, authorBirthYearField,genreField);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please select a book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }



    private JPanel createAddAuthorPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        try {
            getDataForGivenTable("authors",Author.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        JTextField nameField = new JTextField();
        JTextField nationalityField = new JTextField();
        JTextField birthYearField = new JTextField();
        JButton addButton = new JButton("Add");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Nationality:"));
        panel.add(nationalityField);
        panel
                .add(new JLabel("Birth Year:"));
        panel.add(birthYearField);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String nationality = nationalityField.getText();
                String birthYearText = birthYearField.getText();

                if (name.isEmpty() || nationality.isEmpty() || birthYearText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit method if any field is empty
                }

                try {
                    int birthYear = Integer.parseInt(birthYearText);
                    databaseManager.addAuthor(name, nationality, birthYear);
                    try {
                        List<Author> authors = getDataForGivenTable("authors", Author.class);
                        authorComboBox.addItem(authors.get(authors.size()-1).getName());
                        selectedAuthor=(authors.get(authors.size()-1));
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("authors",Author.class);
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Author added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields(nameField, nationalityField, birthYearField);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Invalid birth year. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        return panel;
    }

    private JPanel createRemoveAuthorPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        try {
            getDataForGivenTable("authors",Author.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        JTextField nameField = new JTextField();
        JButton removeButton = new JButton("Remove");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(removeButton);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please enter author name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit method if name field is empty
                }
                databaseManager.removeAuthor(name);
                try {
                    getDataForGivenTable("authors", Author.class);
                    authorComboBox.removeItem(name);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                displayDataInTable("authors",Author.class);
                clearFields(nameField);
            }
        });


        return panel;
    }
    public  List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM Authors";
        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");
                String name = resultSet.getString("name");
                String nationality = resultSet.getString("nationality");
                int birthYear = resultSet.getInt("birth_year");
                Author author = new Author(name, nationality, birthYear);
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }
    public int getAuthorIdByName(String authorName) {
        String query = "SELECT author_id FROM Authors WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, authorName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("author_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if author is not found
    }
    private void updateAuthorNameInComboBox(JComboBox<String> comboBox, String oldName, String newName, Author selectedAuthor) throws SQLException {
        int itemCount = comboBox.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            if(newName!=null) {
                if (comboBox.getItemAt(i).equals(oldName)) {
                    comboBox.removeItemAt(i);
                    comboBox.insertItemAt(newName, i);
                    comboBox.setSelectedItem(newName);
                    selectedAuthor.setName(newName);
                    break;
                }
            }
        }
    }
    private JPanel createUpdateAuthorPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2));

        try {
            getDataForGivenTable("authors",Author.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Author> authors = getAllAuthors();


        for (Author author : authors) {
            authorComboBox.addItem(author.name);
        }
        JTextField nameField = new JTextField();
        JTextField nationalityField = new JTextField();
        JTextField birthYearField = new JTextField();
        JCheckBox nameCheckbox = new JCheckBox("Update Name");
        JCheckBox nationalityCheckbox = new JCheckBox("Update Nationality");
        JCheckBox birthYearCheckbox = new JCheckBox("Update Birth Year");
        JButton updateButton = new JButton("Update");

        panel.add(new JLabel("Select Author:"));
        panel.add(authorComboBox);
        panel.add(nameCheckbox);
        panel.add(nameField);
        panel.add(nationalityCheckbox);
        panel.add(nationalityField);
        panel.add(birthYearCheckbox);
        panel.add(birthYearField);
        panel.add(updateButton);

        // Disable text fields by default
        nameField.setEnabled(false);
        nationalityField.setEnabled(false);
        birthYearField.setEnabled(false);

        // Add action listeners to checkboxes to enable/disable corresponding text fields
        nameCheckbox.addActionListener(e -> nameField.setEnabled(nameCheckbox.isSelected()));
        nationalityCheckbox.addActionListener(e -> nationalityField.setEnabled(nationalityCheckbox.isSelected()));
        birthYearCheckbox.addActionListener(e -> birthYearField.setEnabled(birthYearCheckbox.isSelected()));

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedName = (String) authorComboBox.getSelectedItem();

                for (Author author : authors) {
                    if (author.name.equals(selectedName)) {
                        selectedAuthor = author;
                        break;
                    }
                }
                if (selectedAuthor != null) {
                    int authorId = getAuthorIdByName(selectedAuthor.name); // Assuming Author has an id field

                    String name = nameField.isEnabled() ? nameField.getText() : selectedAuthor.name;
                    String nationality = nationalityField.isEnabled() ? nationalityField.getText() : selectedAuthor.nationality;
                    String birthYearText = birthYearField.isEnabled() ? birthYearField.getText() : null;

                    if (name.isEmpty() || nationality.isEmpty() || (birthYearText != null && birthYearText.isEmpty())) {
                        JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                                "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Exit method if any required field is empty
                    }
                    try {
                        Integer birthYear = birthYearText != null ? Integer.parseInt(birthYearText) : null;
                        updateAuthorNameInComboBox(authorComboBox, selectedName, name, selectedAuthor);
                        databaseManager.updateAuthor(authorId, name, nationality, birthYear);
                        getDataForGivenTable("authors",Author.class);
                        displayDataInTable("authors",Author.class);
                        clearFields(nameField, nationalityField, birthYearField);
                    } catch (NumberFormatException | SQLException ex) {
                        JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                                "Invalid birth year. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please select an author.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createAddReaderPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        try {
            getDataForGivenTable("readers",Reader.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JButton addButton = new JButton("Add");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel(""));
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String address = addressField.getText();
                if (!name.isEmpty() && !address.isEmpty()) {
                    databaseManager.addReader(name, address);
                    List<Reader> readers = null;
                    try {
                        readers = getDataForGivenTable("readers", Reader.class);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                   readerComboBox.addItem(readers.get(readers.size()-1).getName());
                    selectedReader= readers.get(readers.size()-1);
                    try {
                        getDataForGivenTable("readers",Reader.class);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("readers",Reader.class);
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Reader added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields(nameField, addressField);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createRemoveReaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        try {
            getDataForGivenTable("readers",Reader.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        JTextField nameField = new JTextField();
        JButton removeButton = new JButton("Remove");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(removeButton);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (!name.isEmpty()) {
                    databaseManager.removeReader(name);
                    try {
                        getDataForGivenTable("readers", Reader.class);
                        readerComboBox.removeItem(name);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("readers",Reader.class);
                    clearFields(nameField);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please enter reader name.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void updateReaderNameInComboBox(JComboBox<String> comboBox, String oldName, String newName, Reader selectedReader) {
        int itemCount = comboBox.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (comboBox.getItemAt(i).equals(oldName)) {
                comboBox.removeItemAt(i);
                comboBox.insertItemAt(newName, i);
                comboBox.setSelectedItem(newName);
                selectedReader.setName(newName);
                break;
            }
        }
    }

    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Readers")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                readers.add(new Reader(name, address));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readers;
    }
    private JPanel createUpdateReaderPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2)); // Increased rows to accommodate additional fields

        try {
            getDataForGivenTable("readers",Reader.class);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Reader> readers = getAllReaders();


        for (Reader reader : readers) {
            readerComboBox.addItem(reader.getName());
        }

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JCheckBox nameCheckbox = new JCheckBox("Update Name");
        JCheckBox addressCheckbox = new JCheckBox("Update Address");
        JButton updateButton = new JButton("Update");

        panel.add(new JLabel("Select Reader:"));
        panel.add(readerComboBox);
        panel.add(nameCheckbox);
        panel.add(nameField);
        panel.add(addressCheckbox);
        panel.add(addressField);
        panel.add(updateButton);

        // Disable text fields by default
        nameField.setEnabled(false);
        addressField.setEnabled(false);

        // Add action listeners to checkboxes to enable/disable corresponding text fields
        nameCheckbox.addActionListener(e -> nameField.setEnabled(nameCheckbox.isSelected()));
        addressCheckbox.addActionListener(e -> addressField.setEnabled(addressCheckbox.isSelected()));

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedName = (String) readerComboBox.getSelectedItem();

                for (Reader reader : readers) {
                    if (reader.getName().equals(selectedName)) {
                        selectedReader = reader;
                        break;
                    }
                }
                if (selectedReader != null) {
                    String name = nameField.isEnabled() ? nameField.getText() : null;
                    String address = addressField.isEnabled() ? addressField.getText() : null;

                    if (name == null) {
                        name = selectedReader.getName();
                    }

                    if (address == null) {
                        address = selectedReader.getAddress();
                    }

                    databaseManager.updateReader(name,selectedName, address);
                    updateReaderNameInComboBox(readerComboBox, selectedName, name, selectedReader);
                    try {
                        getDataForGivenTable("readers",Reader.class);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("readers",Reader.class);
                    clearFields(nameField, addressField);
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Please select a reader.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }
    public Book getBookByISBN(String isbn) throws SQLException {
        Book book = null;

        // SQL query to select a book by its ISBN
        String query = "SELECT b.*,a.* FROM Books b JOIN authors a on a.author_id = b.author_id WHERE isbn = ?";

        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, DatabaseManager.ROOT, DatabaseManager.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, isbn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Author author=new Author(resultSet.getString("name"),resultSet.getString("nationality"),resultSet.getInt("birth_year"));
                    int bookId = resultSet.getInt("book_id");
                    String title = resultSet.getString("title");
                    String genre = resultSet.getString("genre");
                    boolean available=resultSet.getBoolean("available");
                    book = new Book(isbn,title,author,available,genre);
                    book.setBookId(bookId);
                }
            }
        }

        return book;
    }

    private JPanel createBorrowBookPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JTextField readerNameField = new JTextField();
        JTextField isbnField = new JTextField();
        JButton borrowButton = new JButton("Borrow");

        panel.add(new JLabel("Reader Name:"));
        panel.add(readerNameField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel(""));
        panel.add(borrowButton);

        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String readerName = readerNameField.getText();
                String isbn = isbnField.getText();

                if (!readerName.isEmpty() && !isbn.isEmpty()) {
                    Book bookByISBN;
                    try {
                        bookByISBN = getBookByISBN(isbn);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    boolean success = databaseManager.borrowBook(readerName,bookByISBN.getBookId());
                    try {
                         getDataForGivenTable("borrowedbooks", BorrowBook.class);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    displayDataInTable("borrowedbooks",BorrowBook.class);
                    if (success) {
                        JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                                "Book borrowed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields(readerNameField, isbnField);
                    } else {
                        JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                                "Book not available or reader does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystem.this,
                            "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, searchField.getPreferredSize().height)); // Set preferred size

        JButton searchButton = new JButton("Search");
        JTable table = new JTable();

        // Create a combo box to select search criteria
        JComboBox<String> searchCriteriaComboBox = new JComboBox<>();
        searchCriteriaComboBox.addItem("Title");
        searchCriteriaComboBox.addItem("Name");
        searchCriteriaComboBox.addItem("Nationality");
        searchCriteriaComboBox.addItem("ISBN");


        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Add action listener to search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                String selectedCriteria = (String) searchCriteriaComboBox.getSelectedItem();
                if (!keyword.isEmpty()) {
                    // Call searchBooks method to update table with search results
                    databaseManager.searchBooks(table, keyword, selectedCriteria);
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Please enter a search keyword.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }


    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    public static void main(String[] args) {
        DatabaseManager.createTables();
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}

class DatabaseManager {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    public static final String ROOT = "root";
    public static final String PASSWORD = "YOUR_PASSWORD";

    public static void createTables() {
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             Statement statement = connection.createStatement()) {
            // Create Authors table
            String createAuthorsTableSQL = "CREATE TABLE IF NOT EXISTS Authors (" +
                    "author_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "nationality VARCHAR(100)," +
                    "birth_year INT)";
            statement.executeUpdate(createAuthorsTableSQL);

            // Create Books table
            String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS Books (" +
                    "book_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "ISBN VARCHAR(20) ," +
                    "title VARCHAR(255) NOT NULL," +
                    "author_id INT," +
                    "genre VARCHAR(25)," +
                    "available BOOLEAN DEFAULT TRUE," +
                    "FOREIGN KEY (author_id) REFERENCES Authors(author_id))";
            statement.executeUpdate(createBooksTableSQL);

            // Create Readers table
            String createReadersTableSQL = "CREATE TABLE IF NOT EXISTS Readers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "address VARCHAR(255))";
            statement.executeUpdate(createReadersTableSQL);

            // Create BorrowedBooks table
            String createBorrowedBooksTableSQL = "CREATE TABLE IF NOT EXISTS BorrowedBooks (" +
                    "borrowedBook_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "id INT NOT NULL," +
                    "book_id INT NOT NULL," +
                    "FOREIGN KEY (id) REFERENCES Readers(id)," +
                    "FOREIGN KEY (book_id) REFERENCES Books(book_id))";
            statement.executeUpdate(createBorrowedBooksTableSQL);

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(String title, String authorName, String authorNationality, int authorBirthYear, String isbn, String genre) {
        int authorId = getAuthorId(authorName, authorNationality, authorBirthYear);
        if (authorId != -1) {
            String query = "INSERT INTO Books (title, author_id, ISBN,genre, available) VALUES (?, ?,?, ?, TRUE)";
            try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, title);
                preparedStatement.setInt(2, authorId);
                preparedStatement.setString(3, isbn);
                preparedStatement.setString(4,genre);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to add book: Author not found or couldn't be created.");
        }
    }


    private int getAuthorId(String authorName, String authorNationality, int authorBirthYear) {
        int authorId = -1;
        String query = "SELECT author_id FROM Authors WHERE name = ? AND nationality = ? AND birth_year = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, authorName);
            preparedStatement.setString(2, authorNationality);
            preparedStatement.setInt(3, authorBirthYear);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    authorId = resultSet.getInt("author_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authorId;
    }

    public void removeBook(Integer id) {
        boolean isBorrowed = checkIfBookIsBorrowed(id);
        if (isBorrowed) {
            int option = JOptionPane.showConfirmDialog(null, "This book is currently borrowed. Do you want to mark it as returned before removing?", "Book is Borrowed", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {

                markBookAsReturned(id);
            } else {
                return;
            }
        }

        int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this book?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmDelete == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM Books WHERE book_id = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1,id);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfBookIsBorrowed(Integer id) {
        String query = "SELECT * FROM BorrowedBooks WHERE book_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markBookAsReturned(Integer id) {
        String query = "DELETE FROM BorrowedBooks  WHERE book_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book marked as returned.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 class BookComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Assuming value is a Book object
            if (value instanceof Book) {
             LibraryManagementSystem.Book book = (LibraryManagementSystem.Book) value;
                label.setText(book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
            }

            return label;
        }
    }
    private LibraryManagementSystem.Book findBookByTitle(List<LibraryManagementSystem.Book> books, String title) {
        for (LibraryManagementSystem.Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }
    public void updateBook(String isbn, String title, String authorName, String authorNationality, Integer authorBirthYear,String genre, boolean available,
                           boolean updateTitle, boolean updateAuthorName, boolean updateAuthorNationality, boolean updateAuthorBirthYear,boolean updateGenre, boolean updateAvailability) {
        List<LibraryManagementSystem.Book> allBooks = LibraryManagementSystem.getAllBooks();

        JComboBox<String> bookComboBox = new JComboBox<>();
        for (LibraryManagementSystem.Book book : allBooks) {
            bookComboBox.addItem(book.getTitle());
        }
        bookComboBox.setRenderer(new BookComboBoxRenderer());
        int option = JOptionPane.showConfirmDialog(null, bookComboBox, "Select book to update:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String selectedTitle = (String) bookComboBox.getSelectedItem();
            LibraryManagementSystem.Book selectedBook = findBookByTitle(allBooks, selectedTitle);

            if (selectedBook != null) {
                int authorId = getAuthorIdByTitle(selectedTitle);
                if (authorId != -1) {
                    if ((updateAuthorName || updateAuthorNationality || updateAuthorBirthYear)) {
                        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD)) {
                            StringBuilder authorQueryBuilder = new StringBuilder("UPDATE Authors SET ");
                            List<Object> authorParameters = new ArrayList<>();
                            if (updateAuthorName) {
                                authorQueryBuilder.append(" name = ?, ");
                                authorParameters.add(authorName);
                            }
                            if (updateAuthorNationality) {
                                authorQueryBuilder.append(" nationality = ?, ");
                                authorParameters.add(authorNationality);
                            }
                            if (updateAuthorBirthYear) {
                                authorQueryBuilder.append(" birth_year = ?, ");
                                authorParameters.add(authorBirthYear);
                            }

                            // Remove the trailing comma and space
                            authorQueryBuilder.setLength(authorQueryBuilder.length() - 2);
                            authorQueryBuilder.append(" WHERE author_id = ? ");
                            authorParameters.add(authorId);

                            try (PreparedStatement authorStatement = connection.prepareStatement(authorQueryBuilder.toString())) {
                                int authorParameterIndex = 1;
                                for (Object parameter : authorParameters) {
                                    authorStatement.setObject(authorParameterIndex++, parameter);
                                }
                                authorStatement.executeUpdate();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else if(updateTitle||updateAvailability||updateGenre){
                        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD)) {
                            StringBuilder bookQueryBuilder = new StringBuilder("UPDATE Books SET ");
                            List<Object> bookParameters = new ArrayList<>();
                            if (updateTitle) {
                                bookQueryBuilder.append("title = ?, ");
                                bookParameters.add(title);
                            }
                            if (updateAuthorName || updateAuthorNationality || updateAuthorBirthYear) {
                                bookQueryBuilder.append("author_id = ?, ");
                                bookParameters.add(authorId);
                            }
                            if (updateAvailability) {
                                bookQueryBuilder.append("available = ?, ");
                                bookParameters.add(available);
                            }
                            if (updateGenre) {
                                bookQueryBuilder.append(" genre = ?, ");
                                bookParameters.add(genre);
                            }
                            // Remove the trailing comma and space
                            bookQueryBuilder.setLength(bookQueryBuilder.length() - 2);
                            bookQueryBuilder.append(" WHERE ISBN = ?");
                            bookParameters.add(isbn);

                            try (PreparedStatement bookStatement = connection.prepareStatement(bookQueryBuilder.toString())) {
                                int bookParameterIndex = 1;
                                for (Object parameter : bookParameters) {
                                    bookStatement.setObject(bookParameterIndex++, parameter);
                                }
                                bookStatement.executeUpdate();
                                // If title is updated, refresh the selection
                                if (updateTitle) {
                                    // Re-populate the combo box with updated titles
                                    bookComboBox.removeAllItems();
                                    for (LibraryManagementSystem.Book book : allBooks) {
                                        bookComboBox.addItem(book.getTitle());
                                    }
                                    // Select the updated title
                                    bookComboBox.setSelectedItem(title);
                                }
                                JOptionPane.showMessageDialog(null, "Book updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println("Author not found.");
                        return;
                    }
                } else {
                    System.out.println("No book selected.");
                }
            }
        }
    }

    private int getAuthorIdByTitle(String title) {
        int authorId = -1;
        String query = "SELECT author_id FROM Books WHERE title = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    authorId = resultSet.getInt("author_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authorId;
    }
    public void addAuthor(String name, String nationality, int birthYear) {
        String query = "INSERT INTO Authors (name, nationality, birth_year) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, nationality);
            preparedStatement.setInt(3, birthYear);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteBooksByAuthor(String authorName) {
        // SQL query to delete books by author name
        String deleteBooksSQL = "DELETE FROM Books WHERE author_id IN " +
                "(SELECT author_id FROM Authors WHERE name = '" + authorName + "')";

        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, ROOT, PASSWORD);
             Statement statement = connection.createStatement()) {
            // Execute the delete query
            int rowsAffected = statement.executeUpdate(deleteBooksSQL);

            // Display number of rows deleted
            System.out.println(rowsAffected + " row(s) deleted for author: " + authorName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeAuthor(String name) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this author?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            if(LibraryManagementSystem.getAllBooks().size()!=0){
             deleteBooksByAuthor(name);
            }
            String query = "DELETE FROM authors  WHERE name = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Author removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateAuthor(int authorId, String name, String nationality, Integer birthYear) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Authors SET ");
        List<Object> parameters = new ArrayList<>();

        // Check which fields are being updated and add them to the query
        if (name != null) {
            queryBuilder.append("name = ?, ");
            parameters.add(name);
        }
        if (nationality != null) {
            queryBuilder.append("nationality = ?, ");
            parameters.add(nationality);
        }
        if (birthYear != null) {
            queryBuilder.append("birth_year = ?, ");
            parameters.add(birthYear);
        }

        // Remove the trailing comma and space
        if (queryBuilder.lastIndexOf(", ") != -1) {
            queryBuilder.delete(queryBuilder.lastIndexOf(", "), queryBuilder.length());
        }

        queryBuilder.append(" WHERE author_id = ?");
        parameters.add(authorId);

        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Author updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addReader(String name, String address) {
        String query = "INSERT INTO Readers (name, address) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<LibraryManagementSystem.BorrowBook> getAllBorrowedBooks() {
        List<LibraryManagementSystem.BorrowBook> borrowedBooks = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD)) {
            String query = "SELECT * FROM BorrowedBooks";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int borrowedBookId = resultSet.getInt("borrowedBook_id");
                    int readerId = resultSet.getInt("id");
                    int bookId = resultSet.getInt("book_id");

                    LibraryManagementSystem.BorrowBook borrowedBook = new LibraryManagementSystem.BorrowBook(readerId, bookId);
                    borrowedBooks.add(borrowedBook);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions here
        }

        return borrowedBooks;
    }
    public void deleteBorrowedBooksByReaderName(String readerName) {
        // Find the id of the reader based on the reader name
        int readerId = getReaderId(readerName);
        if (readerId == -1) {
            System.out.println("Reader not found.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD)) {
            // Delete borrowed books associated with the reader id
            String deleteQuery = "DELETE FROM BorrowedBooks WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, readerId);
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println(rowsAffected + " borrowed books deleted for reader: " + readerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }
    public void removeReader(String name) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this reader?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            if(getAllBorrowedBooks().size()!=0){
                deleteBorrowedBooksByReaderName(name);
            }
            String query = "DELETE  FROM Readers WHERE name = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Reader removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateReader(String s, String name, String address) {
        int readerId = getReaderId(name);
        StringBuilder queryBuilder = new StringBuilder("UPDATE Readers SET ");
        List<Object> parameters = new ArrayList<>();

        // Check which fields are being updated and add them to the query
        if (s != null) {
            queryBuilder.append(" name = ?, ");
            parameters.add(s);
        }
        if (address != null) {
            queryBuilder.append(" address = ?, ");
            parameters.add(address);
        }
        // Remove the trailing comma and space
        if (queryBuilder.lastIndexOf(", ") != -1) {
            queryBuilder.delete(queryBuilder.lastIndexOf(", "), queryBuilder.length());
        }

        queryBuilder.append(" WHERE id = ? ");
        parameters.add(readerId);

        try (Connection connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Reader updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int getReaderId(String readerName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int readerId = -1;
        try {
            connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
            String query = "SELECT id FROM Readers WHERE name = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, readerName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                readerId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return readerId;
    }

    public boolean borrowBook(String readername, Integer bookId) {
        int readerId1 = getReaderId(readername);
        Connection connection = null;
        PreparedStatement borrowStatement = null;
        PreparedStatement updateAvailabilityStatement = null;
        try {
            connection = DriverManager.getConnection(DB_URL, ROOT, PASSWORD);
            connection.setAutoCommit(false);


            boolean available = isBookAvailable(connection, bookId);
            if (!available) {
                return false;
            }

            // Insert borrowed book record
            String borrowQuery = "INSERT INTO BorrowedBooks (id, book_id) VALUES (?, ?)";
            borrowStatement = connection.prepareStatement(borrowQuery);
            borrowStatement.setInt(1, readerId1);
            borrowStatement.setInt(2, bookId);
            borrowStatement.executeUpdate();

            // Update book availability
            String updateAvailabilityQuery = "UPDATE Books SET available = FALSE WHERE book_id = ?";
            updateAvailabilityStatement = connection.prepareStatement(updateAvailabilityQuery);
            updateAvailabilityStatement.setInt(1, bookId);
            updateAvailabilityStatement.executeUpdate();

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (borrowStatement != null) borrowStatement.close();
                if (updateAvailabilityStatement != null) updateAvailabilityStatement.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isBookAvailable(Connection connection, Integer bookId) throws SQLException {
        String query = "SELECT available FROM Books WHERE book_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("available");
            }
        }
        return false;
    }
    public void searchBooks(JTable table, String keyword, String criteria) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if(model.getColumnCount()==0) {
            model.addColumn("title of the book");
            model.addColumn("ISBN of the book");
            model.addColumn("genre of the book");
            model.addColumn("name of the author");
            model.addColumn("nationality of the author");
            model.addColumn("date of birth of the author");
        }
        // Perform database query and update table model with retrieved data
        try (Connection connection = DriverManager.getConnection(DatabaseManager.DB_URL, ROOT, PASSWORD)) {
            String query = "SELECT b.*, a.* FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id " +
                    "WHERE " + criteria + " LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();


            // Clear existing table data
            model.setRowCount(0);

            // Add retrieved data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("title"),
                        resultSet.getString("ISBN"),
                        resultSet.getString("genre"),
                        resultSet.getString("name"),
                        resultSet.getString("nationality"),
                        resultSet.getString("birth_year")
                };

                model.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error executing query: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}