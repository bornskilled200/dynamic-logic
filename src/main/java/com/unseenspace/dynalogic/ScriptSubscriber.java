package com.unseenspace.dynalogic;

import com.darylteo.nio.DirectoryChangedSubscriber;
import com.darylteo.nio.DirectoryWatcher;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by madsk_000 on 6/18/2016.
 */
class ScriptSubscriber extends DirectoryChangedSubscriber implements Closeable {
    private ScriptService scriptService;
    private final DelayedOperator<Path> delayedOperator;

    public ScriptSubscriber(ScriptService scriptService) {
        this.delayedOperator = new DelayedOperator<>(scriptService::putScript, 1000);
        this.scriptService = scriptService;
    }

    private Path getScriptPath(DirectoryWatcher watcher, Path entry) {
        return watcher.getPath().resolve(entry);
    }

    @Override
    public void directoryChanged(DirectoryWatcher watcher, Path entry) {
    }

    @Override
    public void entryCreated(DirectoryWatcher watcher, Path entry) {
        entryModified(null, entry);
    }

    @Override
    public void entryDeleted(DirectoryWatcher watcher, Path entry) {
        scriptService.removeScript(entry);
        delayedOperator.remove(getScriptPath(watcher, entry));
    }

    @Override
    public void entryModified(DirectoryWatcher watcher, Path entry) {
        delayedOperator.put(getScriptPath(watcher, entry));
    }

    @Override
    public void close() throws IOException {
        delayedOperator.close();
    }
}
