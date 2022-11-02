package entities;

import factories.BooksFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class Library {

    int capacity;
    List<Book> books;

    public Library(int capacity, BooksFactory factory) {
        this(capacity, factory.get());
        if(capacity < books.size()) throw new RuntimeException("Capacity ("+capacity+") is less than books amount!");
        for(int i=books.size(); i<capacity; i++){
            books.add(null);
        }
    }

    public Book get(int number) {
        try{
            Book book = books.get(number);
            if(book == null) throw new NullPointerException("Failed to get book from "+number);
            System.out.println(number+" -> "+book); // gson
            books.set(number, null);
            return book;
        } catch (ArrayIndexOutOfBoundsException exception){
            throw new ArrayIndexOutOfBoundsException("Incorrect index = "+number+" Capacity = "+capacity);
        }
    }

    public void add(Book book){
        int i = 0;
        while(i < capacity){
            if(books.get(i) == null){
                books.set(i, book);
                break;
            }
            i++;
        }
        if(i == capacity) throw new RuntimeException("Library is full!");
    }

    public void print(){
        int i=0;
        for(Book book : books){
            System.out.println(i+" -> "+book); // gson
            i++;
        }
    }

}
