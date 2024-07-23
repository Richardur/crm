package network;

import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CleanJsonConverterFactory extends Converter.Factory {
    private final GsonConverterFactory gsonConverterFactory;

    public CleanJsonConverterFactory(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<ResponseBody, ?> gsonConverter = gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                String rawJson = body.string();
                int jsonStartIndex = rawJson.indexOf('{');
                if (jsonStartIndex > 0) {
                    rawJson = rawJson.substring(jsonStartIndex);
                }
                Log.d("CleanJson", "Cleaned JSON: " + rawJson); // Log the cleaned JSON
                ResponseBody cleanedBody = ResponseBody.create(rawJson, body.contentType());
                body.close(); // Make sure to close the original body
                return gsonConverter.convert(cleanedBody);
            }
        };
    }
}
