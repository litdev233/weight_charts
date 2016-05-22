package net.litdev.weight_charts.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象序列化
 */
public class UtilsSerialize<E> {

    /**
     * 序列化
     * @param entity
     * @return
     * @throws IOException
     */
    public String serialize(E entity) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(entity);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    /**
     * 反序列化
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public E deSerialization(String str) throws IOException,ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        E entity = (E) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return entity;
    }


}
