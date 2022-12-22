package com.oldschoolminecraft.cb;

public class Util
{
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
