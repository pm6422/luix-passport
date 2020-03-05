package org.infinity.passport.dozer;

import com.github.dozermapper.core.Mapper;
import org.infinity.passport.domain.User;
import org.infinity.passport.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DozerConverterTest {

    @Autowired
    private Mapper dozerMapper;

    @Test
    public void testClassToClass() {
        User from = new User();
        from.setUserName("louis");
        UserDTO to = dozerMapper.map(from, UserDTO.class);
        assertEquals("louis", to.getFirstName());
    }

    @Test
    public void testMapToClass() {
        Map from = new HashMap();
        from.put("firstName", "louis");
        User to = dozerMapper.map(from, User.class, "map-to-class");
        assertEquals("liu", to.getLastName());
    }

    @Test
    public void testClassToMap() {
        User from = new User();
        from.setUserName("userNameValue");
        Map<?, ?> to = dozerMapper.map(from, HashMap.class, "class-to-map");
        assertEquals("userNameValue", to.get("fullName"));
    }
}
