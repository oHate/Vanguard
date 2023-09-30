package dev.ohate.vanguard.framework;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Handler {

    private boolean loaded = false;

    public abstract Module getModule();

    public void initialLoad() {
    }

    public void saveData() {
        if (!loaded) {
            throw new IllegalStateException("Can't save a handler that hasn't been loaded");
        }
    }

    public void ensureResourceExists(File file) {
        file.getParentFile().mkdirs();
    }

    public List<Class<?>> getCommands() {
        return new ArrayList<>();
    }

    public List<Object> getListeners() {
        return new ArrayList<>();
    }

}
