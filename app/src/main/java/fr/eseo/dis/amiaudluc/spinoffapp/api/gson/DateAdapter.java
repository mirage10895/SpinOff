package fr.eseo.dis.amiaudluc.spinoffapp.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAdapter implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return df.parse(json.getAsString());
        } catch (ParseException e) {
            return null;
        }
    }
}
