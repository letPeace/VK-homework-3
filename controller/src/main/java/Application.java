import com.google.inject.Guice;
import com.google.inject.Injector;
import entities.Book;
import entities.Library;
import factories.LibraryFactory;
import modules.TestModule;

public class Application {

    public static void main(String[] args){
        String fileName = args[0];
        int capacity = Integer.parseInt(args[1]); // probably should catch NumberFormatException
        //
        final Injector injector = Guice.createInjector(new TestModule(fileName));
        Library library = injector.getInstance(LibraryFactory.class).get(capacity);
        library.print();
    }

}
