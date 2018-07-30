package org.infinity.passport.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.entity.IHanlpDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.hankcs.hanlp.corpus.io.IIOAdapter;
import com.hankcs.hanlp.corpus.io.ResourceIOAdapter;
import com.hankcs.hanlp.utility.Predefine;

@Component
public class MongoDbIOAdapter implements IIOAdapter {

    private ResourceIOAdapter resourceIOAdapter = new ResourceIOAdapter();

    @Autowired
    private MongoTemplate     mongoTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public InputStream open(String entityClassName) throws IOException {
        if (StringUtils.endsWith(entityClassName, Predefine.BIN_EXT)) {
            String className = StringUtils.removeEnd(entityClassName, Predefine.BIN_EXT);

            if (ApplicationConstants.HANLP_DICT_MAP.keySet().contains(className)) {
                String path = System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar
                        + ApplicationConstants.HANLP_DICT_MAP.get(className) + Predefine.BIN_EXT;
                return resourceIOAdapter.open(path);
            }
        }

        if (!ApplicationConstants.HANLP_DICT_MAP.keySet().contains(entityClassName)) {
            return resourceIOAdapter.open(entityClassName);
        }

        Class<IHanlpDictionary> clazz;
        try {
            clazz = (Class<IHanlpDictionary>) Class.forName(entityClassName);
            String resultStr = "";
            List<IHanlpDictionary> results = mongoTemplate.findAll(clazz);
            for (IHanlpDictionary result : results) {
                resultStr = resultStr + result.toDictionaryString() + IOUtils.LINE_SEPARATOR_WINDOWS;
            }
            return new ByteArrayInputStream(resultStr.getBytes());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream("".getBytes());
    }

    @Override
    public OutputStream create(String entityClassName) throws IOException {
        String className = StringUtils.removeEnd(entityClassName, Predefine.BIN_EXT);
        String path = System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar
                + ApplicationConstants.HANLP_DICT_MAP.get(className) + Predefine.BIN_EXT;
        return resourceIOAdapter.create(path);
    }
}
