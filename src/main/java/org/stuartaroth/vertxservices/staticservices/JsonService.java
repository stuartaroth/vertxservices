package org.stuartaroth.vertxservices.staticservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.stuartaroth.vertxservices.models.Book;
import org.stuartaroth.vertxservices.models.Movie;

import java.lang.reflect.Type;
import java.util.List;

public class JsonService {
    private static Gson gson = new GsonBuilder().create();

    public static Type LIST_OF_BOOK = new TypeToken<List<Book>>(){}.getType();
    public static Type LIST_OF_MOVIE = new TypeToken<List<Movie>>(){}.getType();

    public static String write(Object o) {
        return gson.toJson(o);
    }

    public static <T> T read(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
