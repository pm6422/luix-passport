package org.infinity.passport.collection.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinity.passport.entity.MenuTree;
import org.infinity.passport.repository.AdminMenuRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MenuTreeTest {

    @Autowired
    private AdminMenuRepository adminMenuRepository;

    @Test
    public void test() throws JsonProcessingException {
        MenuTree tree = new MenuTree();
        adminMenuRepository.findAll().forEach(menu -> tree.insert(menu.asNode()));
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree.getChildren()));
    }
}
