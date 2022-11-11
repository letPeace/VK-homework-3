package factories;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entities.Book;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BooksFactory {

    @NotNull
    private static final Type listBooksType = new TypeToken<ArrayList<Book>>(){}.getType();
    @NotNull
    private final String path;

    public BooksFactory(@NotNull String path) {
        this.path = path;
    }

    @NotNull
    public List<Book> get(){
        try {
            return new Gson().fromJson(new BufferedReader(new FileReader(path)), listBooksType);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
