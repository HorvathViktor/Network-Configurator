package main.java.Configs;

import main.java.Logging.FileLogger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CheckPrompt
{
    public static boolean assertPrompt(String receivedLine, String expectedPrompt, FileLogger logger)
    {
        String extractedPrompt = extractPrompt(receivedLine);
        if (Objects.equals(expectedPrompt, extractedPrompt))
        {
            logger.write("Receiving: " + extractedPrompt + " matched with " + expectedPrompt);
        }
        else
        {
            logger.write("Receiving: " + extractedPrompt + " did not match with " + expectedPrompt);
        }
        return Objects.equals(expectedPrompt, extractedPrompt);
    }

    private static @NotNull String extractPrompt(@NotNull String line)
    {
        int openPos = line.indexOf('(');
        int closePos = line.indexOf(')');
        if (openPos == -1)
        {
            return line.contains("#") ? "#" : "";
        }

        return line.substring(openPos + 1, closePos);
    }
}
