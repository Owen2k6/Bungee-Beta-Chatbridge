package test;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugBungeePlugin
{
    public static void main(String[] args)
    {
        String oldPath = System.getenv("OLD_PATH");
        String newPath = System.getenv("NEW_PATH");
        File newFile = new File(newPath);
        if (newFile.exists()) newFile.delete();
        else newFile.getParentFile().mkdirs();
        try
        {
            Files.move(Paths.get(oldPath), Paths.get(newPath));
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("java", "-jar", "proxy/BungeeCord.jar");
            pb.directory(new File("proxy"));
            Process p = pb.start();
            pb.redirectInput();
            pb.redirectError();
        }
        catch (Exception ex) { System.out.println("Failed to move plugin to test server"); }
    }
}
