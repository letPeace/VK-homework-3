package modules;

import com.google.inject.AbstractModule;
import factories.BooksFactory;
import factories.LibraryFactory;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TestModule extends AbstractModule {

    private static String path;

    public static void setPath(String path){
        TestModule.path = path;
    }

    @Override
    protected void configure() {
        bind(BooksFactory.class).toInstance(new BooksFactory(path));
        bind(LibraryFactory.class);
    }

}
