package dev.ohate.vanguard.store.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;

import java.io.Closeable;

@Getter
public class Mongo implements Closeable {

    private MongoClient client;

    public void connect(String uri) {
        client = MongoClients.create(uri);
    }

    @Override
    public void close() {
        client.close();
    }

}
