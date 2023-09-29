package dev.ohate.vanguard.util;

import com.google.gson.*;
import lombok.Getter;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static final JsonWriterSettings RELAXED_WRITER = JsonWriterSettings.builder()
            .outputMode(JsonMode.RELAXED)
            .build();

    private static final Map<Type, Object> TYPE_ADAPTERS = new HashMap<>();

    @Getter
    private static Gson gson;

    @Getter
    private static Gson prettyGson;

    static {
        rebuildGson();
    }

    public static void registerTypeAdapters(Map<Type, Object> adapters) {
        TYPE_ADAPTERS.putAll(adapters);
        rebuildGson();
    }

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        TYPE_ADAPTERS.put(type, typeAdapter);
        rebuildGson();
    }

    private static void rebuildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        for (Type type : TYPE_ADAPTERS.keySet()) {
            gsonBuilder.registerTypeAdapter(type, TYPE_ADAPTERS.get(type));
        }

        gson = gsonBuilder.create();
        prettyGson = gsonBuilder.setPrettyPrinting().create();
    }

    public static JsonObject writeToPrettyTree(Object object) {
        return (JsonObject) prettyGson.toJsonTree(object);
    }

    public static String writeToPrettyJson(Object object) {
        return prettyGson.toJson(object);
    }

    public static JsonObject writeToTree(Object object) {
        return (JsonObject) gson.toJsonTree(object);
    }

    public static String writeToJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T readFromJson(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }

    public static <T> T readFromJson(JsonElement json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }

    public static <T> T readFromJson(Document document, Class<T> clazz) throws JsonSyntaxException {
        return readFromJson(document.toJson(RELAXED_WRITER), clazz);
    }

}
