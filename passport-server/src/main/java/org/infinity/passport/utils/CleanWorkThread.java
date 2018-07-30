package org.infinity.passport.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可以在以下几种场景被调用
 * 1）程序正常退出
 * 2）使用System.exit()
 * 3）终端使用Ctrl+C触发的中断
 * 4）系统关闭
 * 5）使用Kill pid命令干掉进程
 * 注意：在使用kill -9 pid是不会JVM注册的钩子不会被调用
 */
public class CleanWorkThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanWorkThread.class);

    @Override
    public void run() {
        LOGGER.info("Clean up the system");
    }
}
