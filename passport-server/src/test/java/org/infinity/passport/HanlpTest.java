package org.infinity.passport;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class HanlpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HanlpTest.class);

    @Test
    public void segment() {
        String[] testCase = new String[] { "刘德华多大的" };
        Segment segment = HanLP.newSegment().enableNameRecognize(true);

        // 初始的HanlpData位于resources目录下，需要手动将Hanlp模型文件拷贝到user.home下的HanlpData目录下
        String path = System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar;

        List<String> customDictionaryPath = Arrays.asList("人名词典.txt").stream().map(file -> path + file)
                .collect(Collectors.toList());

        String[] customDictionaryPathArray = new String[customDictionaryPath.size()];
        HanLP.Config.CustomDictionaryPath = customDictionaryPath.toArray(customDictionaryPathArray);

        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            LOGGER.debug("Result: {}", termList);
        }
    }
}
