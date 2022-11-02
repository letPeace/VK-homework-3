package modules;

import com.google.inject.AbstractModule;
import factories.BooksFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestModule extends AbstractModule {

    private final String fileName;

    public TestModule(){
        this("");
    }

    @Override
    protected void configure() {
        bind(BooksFactory.class).toInstance(new BooksFactory(fileName));
    }

}
