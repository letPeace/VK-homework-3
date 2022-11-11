package factories;

import com.google.inject.Inject;
import entities.Library;
import org.jetbrains.annotations.NotNull;

public class LibraryFactory {

    @NotNull
    private final BooksFactory booksFactory;

    @Inject
    public LibraryFactory(@NotNull BooksFactory booksFactory) {
        this.booksFactory = booksFactory;
    }

    @NotNull
    public Library get(int capacity){
        return new Library(capacity, booksFactory);
    }

}
