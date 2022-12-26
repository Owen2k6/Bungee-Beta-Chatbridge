package com.oldschoolminecraft.cb;

import java.util.ArrayList;
import java.util.List;

public class Util
{
    public static List<String> splitIntoChunks(String text, int chunkSize)
    {
        String parsedText = translateAlternateColorCodes('&', text);
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < parsedText.length(); i += chunkSize)
            chunks.add(parsedText.substring(i, Math.min(parsedText.length(), i + chunkSize)));
        return chunks;
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate)
    {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++)
        {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1)
            {
                b[i] = '\u00A7';
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    public static String generateRandomString(int length)
    {
        String characterSet = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++)
        {
            int index = (int)(characterSet.length() * Math.random());
            sb.append(characterSet.charAt(index));
        }

        return sb.toString();
    }
}
