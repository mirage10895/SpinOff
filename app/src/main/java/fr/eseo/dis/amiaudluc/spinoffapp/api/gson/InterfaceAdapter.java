package fr.eseo.dis.amiaudluc.spinoffapp.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

public class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASSNAME = "media_type";
    private static final String DATA = "gson.interface.adapter.data";

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", src.getClass().getName());
        wrapper.add("data", context.serialize(src));
        return wrapper;
    }

    @Override
    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get(wrapper, CLASSNAME);
        final Type actualType = typeForName(typeName);
        return context.deserialize(wrapper, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        String type = typeElem.getAsString();
        try {
            if (Media.MOVIE.equals(type)) {
                return Movie.class;
            } else if (Media.SERIE.equals(type)) {
                return Serie.class;
            } else if (Media.ARTIST.equals(type)) {
                return Artist.class;
            }
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null) {
            throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
        }
        return elem;
    }
}
