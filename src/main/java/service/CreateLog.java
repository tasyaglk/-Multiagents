package service;

import java.io.IOException;

public interface CreateLog {
    void addLog(String s);

    void putLog() throws IOException;
}
