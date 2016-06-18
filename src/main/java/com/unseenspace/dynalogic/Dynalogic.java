package com.unseenspace.dynalogic;

import com.darylteo.nio.AbstractDirectoryWatchService;
import com.darylteo.nio.ClosableThreadPoolDirectoryWatchService;
import com.darylteo.nio.DirectoryWatcher;
import com.darylteo.nio.ThreadPoolDirectoryWatchService;

import javax.script.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by madsk_000 on 6/17/2016.
 */
public class Dynalogic {

    private static String prompt(Scanner scanner) {
        System.out.print("> ");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("scripts");
        Files.createDirectories(path);
        ScriptService scriptService = new ScriptService();
        try (ScriptSubscriber scriptSubscriber = new ScriptSubscriber(scriptService);
             AbstractDirectoryWatchService factory = new ClosableThreadPoolDirectoryWatchService();
             Scanner scanner = new Scanner(System.in)) {
            DirectoryWatcher watcher = factory.newWatcher(path);

            // Subscribe
            watcher.subscribe(scriptSubscriber);
            Files.list(path).forEach(scriptService::putScript);

            String input;
            while (!(input = prompt(scanner)).equals("exit")) {
                if (!scriptService.containsScript(input)) {
                    System.out.println("Available Commands: " + scriptService.getScriptKeys());
                    continue;
                }

                CompiledScript compiledScript = scriptService.getScript(input);
                try {
                    compiledScript.eval();
                } catch (ScriptException ex) {
                    ex.printStackTrace();
                }
            }
            watcher.unsubscribe(scriptSubscriber);
        }
        System.out.println("Exiting...");
    }
}
