package org.infinity.passport.utils;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @version 1.0
 * @author: liuyao@xforceplus.com
 * @date: 2021/4/21 15:16
 */
public class AesUtilsTests {

    @Test
    public void decryptByGenerateKey() {
        String source = "louis";
        SecretKey secretKey = AesUtils.generateKey();
        byte[] bytes = AesUtils.encrypt(secretKey, source.getBytes(StandardCharsets.UTF_8));
        byte[] decrypt = AesUtils.decrypt(secretKey, bytes);
        Assert.assertEquals(source, new String(decrypt));
    }

    @Test
    public void decryptByPassword() {
        String source = "louis";
        String password = "pass";
        SecretKey secretKey = AesUtils.createKey(password);
        byte[] bytes = AesUtils.encrypt(secretKey, source.getBytes(StandardCharsets.UTF_8));
        byte[] decrypt = AesUtils.decrypt(password, bytes);
        Assert.assertEquals(source, new String(decrypt));
    }
}
