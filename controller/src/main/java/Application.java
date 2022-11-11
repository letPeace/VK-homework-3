import com.google.inject.Guice;
import com.google.inject.Injector;
import entities.Library;
import factories.LibraryFactory;
import modules.TestModule;

public class Application {

    public static void main(String[] args){
        String path = System.getProperty("user.dir")+"\\controller\\src\\main\\resources\\"+args[0];
        TestModule.setPath(path);
        int capacity = Integer.parseInt(args[1]); // probably should catch NumberFormatException
        //
        final Injector injector = Guice.createInjector(new TestModule());
        Library library = injector.getInstance(LibraryFactory.class).get(capacity);
        library.print();
    }

}
