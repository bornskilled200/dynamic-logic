package com.unseenspace.dynalogic;

import javax.script.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by madsk_000 on 6/18/2016.
 */
public class ScriptService {

    private ConcurrentHashMap<String, CompiledScript> scripts = new ConcurrentHashMap<>();
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Compilable compilable = (Compilable)engine;

    private static String getScriptKey(Path path) {
        String key = path.getFileName().toString();
        key = key.substring(0, key.lastIndexOf('.'));
        return key;
    }

    public void putScript(Path path) {
        try {
            CompiledScript compiledScript = compilable.compile(Files.newBufferedReader(path));
            String key = getScriptKey(path);
            CompiledScript oldScript = scripts.put(key, compiledScript);
            System.out.println((oldScript == null ? "New" : "Modified") + " " + key + " script");
        } catch (ScriptException | IOException e) {
            System.err.println(e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    public String getScriptKeys() {
        return Utility.enumerationAsStream(scripts.keys())
                .collect(Collectors.joining(" "));
    }

    public boolean containsScript(String key) {
        return scripts.containsKey(key);
    }

    public CompiledScript getScript(String key) {
        return scripts.get(key);
    }

    public void removeScript(Path path) {
        scripts.remove(getScriptKey(path.getFileName()));
    }
}
