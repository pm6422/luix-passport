package org.infinity.passport.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @version 1.0
 * @author liuyao@xforceplus.com
 * @date 2021/4/21 15:16
 */
public class AesUtilsTests {
    @Test
    public void decryptByPassword() {
        String raw = "KaWooicWPLJRbJa89BgyzpbfekF3j7";
        String password = "message-center-service";
        String encrypted1 = AesUtils.encrypt(raw, password);
        String encrypted2 = AesUtils.encrypt(raw, password);
        System.out.println(encrypted1);
        System.out.println(encrypted2);
        String decrypted = AesUtils.decrypt(encrypted1, password);
        Assert.assertEquals(raw, decrypted);
    }
}
