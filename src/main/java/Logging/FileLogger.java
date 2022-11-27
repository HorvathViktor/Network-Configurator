package main.java.Logging;

import java.io.*;
import java.nio.file.Files;

public class FileLogger
{
    private final String fileName;
    private File file;

    public FileLogger(String fileName)
    {
        this.fileName = fileName + ".log";
        createFile();
    }

    private void createFile()
    {
        try
        {
            file = new File(fileName);
            Files.deleteIfExists(file.toPath());
            file.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void write(String line)
    {
        try (FileWriter writer = new FileWriter(fileName, true); PrintWriter out = new PrintWriter(writer, true))
        {
            out.println(line);
            System.out.println(line);
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

