package entities;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import factories.BooksFactory;
import factories.LibraryFactory;
import modules.TestModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(GuiceExtension.class)
@IncludeModule(TestModule.class)
class LibraryTest {

    @TempDir
    static Path tempDirPath;
    static Injector injector;
    static BooksFactory booksFactory;
    static LibraryFactory libraryFactory;

//    @NotNull
//    private MockitoSession session;

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

    @BeforeAll
    public static void beforeAll() throws IOException {
        final var path = tempDirPath.resolve("data.txt");
        final var books = List.of(
                "[",
                "{\"author\":{\"name\":\"Author0\"},\"name\":\"Book0\"},",
                "{\"author\":{\"name\":\"Author0\"},\"name\":\"Book1\"},",
                "{\"author\":{\"name\":\"Author1\"},\"name\":\"Book0\"},",
                "{\"author\":{\"name\":\"Author2\"},\"name\":\"Book0\"}",
                "]"
        );
        Files.write(path, books);
        injector = Guice.createInjector(new TestModule(path.toString()));
        booksFactory = injector.getInstance(BooksFactory.class);
        libraryFactory = injector.getInstance(LibraryFactory.class);
    }

    @Test
    @DisplayName("Библиотека бросает исключение при создании, если ее вместимость меньше чем количество книг, возвращаемое фабрикой.")
    void testCapacity(){
        Assertions.assertThrows(RuntimeException.class, () -> injector.getInstance(LibraryFactory.class).get(1));
    }

    @Test
    @DisplayName("При создании библиотеки все книги расставлены по ячейкам в порядке как они возвращаются фабрикой книг. Остальные ячейки пусты.")
    void testBooksOrder(){
        List<Book> booksFromLibrary = injector.getInstance(LibraryFactory.class).get(7).getBooks();
        List<Book> booksFromFactory = injector.getInstance(BooksFactory.class).get();
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
        Library library = injector.getInstance(LibraryFactory.class).get(7);
        Book book = library.get(0);
        Assertions.assertEquals("0 -> "+book, outputStreamCaptor.toString().trim()); // gson
    }

    @Test
    @DisplayName("При попытке взять книгу из пустой ячейки библиотека бросает исключение.")
    void testGetFromEmpty(){
        Library library = injector.getInstance(LibraryFactory.class).get(7);
        Assertions.assertThrows(NullPointerException.class, () -> library.get(6));
    }

    @Test
    @DisplayName("При взятии книги возвращается именно та книга, что была в этой ячейке.")
    void testSameBook(){
        Library library = injector.getInstance(LibraryFactory.class).get(7);
        Assertions.assertEquals(library.get(0), injector.getInstance(BooksFactory.class).get().get(0));
    }

    @Test
    @DisplayName("При добавлении книги она размещается в первой свободной ячейке.")
    void testAdd(){
        Library library = injector.getInstance(LibraryFactory.class).get(7);
        Book book = new Book("", new Author("")); // mock
        library.add(book);
        Assertions.assertEquals(library.get(4), book);
    }

    @Test
    @DisplayName("Если при добавлении книги свободных ячеек нет, библиотека бросает исключение.")
    void testAddException(){
        Library library = injector.getInstance(LibraryFactory.class).get(4);
        Book book = new Book("", new Author("")); // mock
        Assertions.assertThrows(RuntimeException.class, () -> library.add(book));
    }

    @Test
    @DisplayName("Вызов метода “напечатать в консоль содержимое” выводит информацию о содержимом ячеек библиотеки.")
    void testPrintOutput(){
        Library library = injector.getInstance(LibraryFactory.class).get(7);
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