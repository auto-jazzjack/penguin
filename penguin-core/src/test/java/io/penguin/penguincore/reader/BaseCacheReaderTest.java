package io.penguin.penguincore.reader;

import io.penguin.penguincore.util.IOUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

import java.io.*;
import java.util.Locale;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BaseCacheReaderTest {


    @Test
    public void asd() throws Exception {
        Sinks.Many<Object> objectMany = Sinks.many().unicast()
                .onBackpressureBuffer();
        objectMany.asFlux()
                .subscribe(i -> System.out.println(i));

        for (int i = 0; i < 100; i++)
            objectMany.tryEmitNext(i);

        Thread.sleep(111);

        for (int i = 100; i < 110; i++)
            objectMany.tryEmitNext(i);
    }

    @Test
    public void asd22() throws Exception {
        File file1 = new File("/Users/jazzjack/Desktop/he/asdasdasd.txt");
        FileInputStream fileInputStream = new FileInputStream(file1);

        ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        byte[] bytes = zipInputStream.readAllBytes();

        File file = new File("/Users/jazzjack/Desktop/he/penguin_con222.zip");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);


    }

    @Test
    public void ads() throws Exception {
        File file = new File("/Users/jazzjack/Desktop/he/penguin.zip");

        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        IOUtils.copy(fileInputStream, byteArrayOutputStream, 100);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        File file1 = new File("/Users/jazzjack/Desktop/he/penguin_con.text");
        FileOutputStream fileOutputStream = new FileOutputStream(file1);
        fileOutputStream.write(convertByteToHexadecimal(bytes).getBytes());


        File file1asd = new File("/Users/jazzjack/Desktop/he/asdasdasd.txt");
        FileInputStream fileInputStreamasd = new FileInputStream(file1asd);
        byte[] bytes1 = fileInputStreamasd.readAllBytes();
        byte[] bytes2 = convertByteToHexa(bytes1);
        System.out.println();

        /*String s = new String(bytes);
        System.out.println();*/

    }

    public static byte[] convertByteToHexa(byte[] byteArray) {
        byte[] hex = new byte[byteArray.length / 2];

        // Iterating through each byte in the array
        for (int i = 0; i < byteArray.length; i += 2) {

            byte left = byteArray[i];
            byte right = byteArray[i + 1];

            hex[i / 2] = (byte) (left + ((byte) (right * 16)));
            //hex[i / 2] -= '0';
            //hex[i / 2] -= '0';

        }

        return hex;
    }

    public byte nomalize(byte b) {
        if (b >= 'a' && b <= 'z') {
            return (byte) (b - 'a');
        } else if (b >= '0' && b <= '9') {
            return (byte) (b - '0');
        } else if (b >= 'a' && b <= 'z') {
            return (byte) (b - 'a');
        }
    }

    public static String convertByteToHexadecimal(byte[] byteArray) {
        StringBuilder hex = new StringBuilder();

        // Iterating through each byte in the array
        for (int i = 0; i < byteArray.length; i++) {
            hex.append(String.format("%02X", byteArray[i]).toLowerCase(Locale.ROOT));
            if (i % 16 == 15) {
                //hex.append("\n");
                continue;
            }
            if (i % 2 == 1) {
                //hex.append(" ");
            }

        }

        //System.out.print(hex);
        return hex.toString();
    }

}
