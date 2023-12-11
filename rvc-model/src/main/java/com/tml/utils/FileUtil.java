package com.tml.utils;

import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 15:18
 */
@Component
public class FileUtil {


    public String getMD5Checksum(InputStream inputStream) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream fis = inputStream) {
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, numRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] md5Bytes = md.digest();

        // 将字节数组转换为十六进制值
        BigInteger bigInt = new BigInteger(1, md5Bytes);
        return String.format("%032x", bigInt);
    }
}
