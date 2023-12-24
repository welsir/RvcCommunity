package com.tml.utils;

import com.tml.common.exception.BaseException;
import com.tml.config.SystemConfig;
import com.tml.pojo.ResultCodeEnum;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 15:18
 */
@Component
public class FileUtil {

    @Resource
    SystemConfig systemConfig;

    private static String imageSize= "";
    private static String modelSize = "";
    private static String audioSize = "";
    private static String[] imageTypes;
    private static String[] modelTypes;
    private static String[] audioTypes;
    @PostConstruct
    public void init(){
        imageSize = systemConfig.getImageSize();
        modelSize = systemConfig.getModelSize();
        imageTypes = systemConfig.getImageType();
        modelTypes = systemConfig.getModelType();
        audioTypes = systemConfig.getAudioType();
        audioSize = systemConfig.getAudioSize();
    }

    public static boolean isImageFile(String filename) {
        String extension = getExtension(filename).toLowerCase();
        return Arrays.asList(imageTypes).contains(extension);
    }

    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }


    public static String getMD5Checksum(InputStream inputStream) throws NoSuchAlgorithmException {

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
    public static double getFileSizeInKB(MultipartFile file) {
        return (double) file.getSize() / 1024;
    }

    public static boolean checkImageFileIsAvailable(MultipartFile file){
        return isImageFile(file.getOriginalFilename()) && !calculateFileSize(file,imageSize);
    }

    public static boolean checkModelFileIsAvailable(MultipartFile[] file){
        if(file.length!=2){
            throw new BaseException(ResultCodeEnum.MODEL_FILE_ILLEGAL);
        }
        String firstFileExtension = file[0].getOriginalFilename();
        String secondFileExtension = file[1].getOriginalFilename();
        String extension1 = getExtension(firstFileExtension);
        String extension2 = getExtension(secondFileExtension);
        return "pth".equalsIgnoreCase(extension1) && "index".equalsIgnoreCase(extension2) && !calculateFileSize(file[0],modelSize)||
                "index".equalsIgnoreCase(extension1) && "pth".equalsIgnoreCase(extension2) && !calculateFileSize(file[0],modelSize);
    }

    private static boolean calculateFileSize(MultipartFile file,String fileSize){
        double fileSizeInKB = getFileSizeInKB(file);
        double defaultModelFileSize = Double.parseDouble(fileSize);
        return fileSizeInKB>defaultModelFileSize;
    }

    public static boolean checkAudioFileIsAvailable(MultipartFile file){
        return isAudioFile(file.getOriginalFilename())&&!calculateFileSize(file,audioSize);
    }

    public static boolean isAudioFile(String filename){
        String fileType = getExtension(filename);
        return Arrays.asList(audioTypes).contains(fileType);
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\18243\\Desktop\\butian\\chaotian\\chaotian.pth";
        System.out.println(getMD5One(path));

    }

    private final static String[] strHex = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String getMD5One(String path) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(FileUtils.readFileToByteArray(new File(path)));
            for (int i = 0; i < b.length; i++) {
                int d = b[i];
                if (d < 0) {
                    d += 256;
                }
                int d1 = d / 16;
                int d2 = d % 16;
                sb.append(strHex[d1] + strHex[d2]);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
