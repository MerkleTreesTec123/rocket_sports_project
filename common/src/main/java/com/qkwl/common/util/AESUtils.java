package com.qkwl.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by wangchen on 2017-08-06.
 */
public class AESUtils {

    private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);

    /**
      * 加密
      * 1.构造密钥生成器
      * 2.根据ecnodeRules规则初始化密钥生成器
      * 3.产生密钥
      * 4.创建和初始化密码器
      * 5.内容加密
      * 6.返回字符串
      */
    public static String AESEncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
            //11.将字符串返回
            return AES_encode;
        } catch (NoSuchAlgorithmException e) {
            logger.error("AESEncode NoSuchAlgorithmException {}",e);
        } catch (NoSuchPaddingException e) {
            logger.error("AESEncode NoSuchPaddingException {}",e);
        } catch (InvalidKeyException e) {
            logger.error("AESEncode InvalidKeyException {}",e);
        } catch (IllegalBlockSizeException e) {
            logger.error("AESEncode IllegalBlockSizeException {}",e);
        } catch (BadPaddingException e) {
            logger.error("AESEncode BadPaddingException {}",e);
        } catch (UnsupportedEncodingException e) {
            logger.error("AESEncode UnsupportedEncodingException {}",e);
        }

        //如果有错就返加nulll
        return null;
    }
    /**
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串反纺成byte[]数组
     * 3.将加密内容解密
     */
    public static String AESDncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //8.将加密并编码后的内容解码成字节数组
            byte [] byte_content= new BASE64Decoder().decodeBuffer(content);
            /*
             * 解密
             */
            byte [] byte_decode=cipher.doFinal(byte_content);
            String AES_decode=new String(byte_decode,"utf-8");
            return AES_decode;
        } catch (NoSuchAlgorithmException e) {
            logger.error("AESDncode NoSuchAlgorithmException {}",e);
        } catch (NoSuchPaddingException e) {
            logger.error("AESDncode NoSuchPaddingException {}",e);
        } catch (InvalidKeyException e) {
            logger.error("AESDncode InvalidKeyException {}",e);
        } catch (IOException e) {
            logger.error("AESDncode IOException {}",e);
        } catch (IllegalBlockSizeException e) {
            logger.error("AESDncode IllegalBlockSizeException {}",e);
        } catch (BadPaddingException e) {
            logger.error("AESDncode BadPaddingException {}",e);
        }

        //如果有错就返加nulll
        return null;
    }

    public static void main(String[] args) {
        while (true) {
            AESUtils se = new AESUtils();
            Scanner scanner = new Scanner(System.in);

         //* 加密

        System.out.println("使用AES对称加密，请输入加密的规则");
        String encodeRules=scanner.next();
        System.out.println("请输入要加密的内容:");
        String content = scanner.next();
        System.out.println("根据输入的规则"+encodeRules+"加密后的密文是:"+se.AESEncode(encodeRules, content));

//        /*
//         * 解密
//         */
//            System.out.println("使用AES对称解密，请输入加密的规则：(须与加密相同)");
//            String encodeRules = scanner.next();
//            System.out.println("请输入要解密的内容（密文）:");
//            String content = scanner.next();
//            System.out.println("根据输入的规则" + encodeRules + "解密后的明文是:" + se.AESDncode(encodeRules, content));
        }
    }
}
