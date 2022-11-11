package entities;

import com.google.inject.Inject;
import factories.BooksFactory;
import factories.LibraryFactory;

import modules.TestModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

@ExtendWith(GuiceExtension.class)
@IncludeModule(TestModule.class)
class LibraryTest {

    static{
        String path = System.getProperty("user.dir")+"\\src\\test\\resources\\books.txt";
        TestModule.setPath(path);
    }

    @Inject
    static BooksFactory booksFactory;
    @Inject
    static LibraryFactory libraryFactory;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void changeOutput() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void returnInitialOutput() {
        System.setOut(standardOut);
    }

    @Test
    @DisplayName("Библиотека бросает исключение при создании, если ее вместимость меньше чем количество книг, возвращаемое фабрикой.")
    void testCapacity(){
        Assertions.assertThrows(RuntimeException.class, () -> libraryFactory.get(1));
    }

    @Test
    @DisplayName("При создании библиотеки все книги расставлены по ячейкам в порядке как они возвращаются фабрикой книг. Остальные ячейки пусты.")
    void testBooksOrder(){
        List<Book> booksFromLibrary = libraryFactory.get(7).getBooks();
        List<Book> booksFromFactory = booksFactory.get();
        for(int i=0; i<booksFromFactory.size(); i++){
            Assertions.assertEquals(booksFromFactory.get(i), booksFromLibrary.get(i));
        }
        for(int i=booksFromFactory.size(); i<booksFromLibrary.size(); i++){
            Assertions.assertNull(booksFromLibrary.get(i));
        }
    }

    @Test
    @DisplayName("При взятии книги информация о ней и ячейке выводится.")
    void testGetOutput(){
        Library library = libraryFactory.get(7);
        Book book = library.get(0);
        Assertions.assertEquals("0 -> "+book, outputStreamCaptor.toString().trim()); // gson
    }

    @Test
    @DisplayName("При попытке взять книгу из пустой ячейки библиотека бросает исключение.")
    void testGetFromEmpty(){
        Library library = libraryFactory.get(7);
        Assertions.assertThrows(NullPointerException.class, () -> library.get(6));
    }

    @Test
    @DisplayName("При взятии книги возвращается именно та книга, что была в этой ячейке.")
    void testSameBook(){
        Library library = libraryFactory.get(7);
        Assertions.assertEquals(library.get(0), booksFactory.get().get(0));
    }

    @Test
    @DisplayName("При добавлении книги она размещается в первой свободной ячейке.")
    void testAdd(){
        Library library = libraryFactory.get(7);
        Book book = new Book("", new Author("")); // mock
        library.add(book);
        Assertions.assertEquals(library.get(4), book);
    }

    @Test
    @DisplayName("Если при добавлении книги свободных ячеек нет, библиотека бросает исключение.")
    void testAddException(){
        Library library = libraryFactory.get(4);
        Book book = new Book("", new Author("")); // mock
        Assertions.assertThrows(RuntimeException.class, () -> library.add(book));
    }

    @Test
    @DisplayName("Вызов метода “напечатать в консоль содержимое” выводит информацию о содержимом ячеек библиотеки.")
    void testPrintOutput(){
        Library library = libraryFactory.get(7);
        library.print();
        StringBuilder stringBuilder = new StringBuilder();
        int i=0;
        for(Book book : library.getBooks()){
            stringBuilder.append(i).append(" -> ").append(book).append("\r\n");
            i++;
        }
        Assertions.assertEquals(stringBuilder.toString().trim(), outputStreamCaptor.toString().trim()); // gson
    }

}