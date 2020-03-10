package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import com.github.dozermapper.core.Mapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertEquals;

@RestController
@Api(tags = "Dozer数据转换")
public class DozerController {

    @Autowired
    private Mapper dozerMapper;

    @ApiOperation("class to class数据转换")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功转换")})
    @GetMapping("/api/dozer/class-to-class")
    @Secured({Authority.USER})
    @Timed
    public ResponseEntity<UserDTO> classToClass() {
        User from = new User();
        from.setUserName("louis");
        UserDTO to = dozerMapper.map(from, UserDTO.class);
        assertEquals("louis", to.getFirstName());
        return ResponseEntity.ok(to);
    }

    @ApiOperation("map to class数据转换")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功转换")})
    @GetMapping("/api/dozer/map-to-class")
    @Secured({Authority.USER})
    @Timed
    public ResponseEntity<User> mapToClass() {
        Map<String, String> from = new HashMap<String, String>();
        from.put("firstName", "louis");
        User to = dozerMapper.map(from, User.class, "map-to-class");
        assertEquals("liu", to.getLastName());
        return ResponseEntity.ok(to);
    }

    @ApiOperation("class to map数据转换")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功转换")})
    @GetMapping("/api/dozer/class-to-map")
    @Secured({Authority.USER})
    @Timed
    public ResponseEntity<Map<String, String>> classToMap() {
        User from = new User();
        from.setUserName("userNameValue");
        @SuppressWarnings("unchecked")
        Map<String, String> to = dozerMapper.map(from, HashMap.class, "class-to-map");
        assertEquals("userNameValue", to.get("fullName"));
        return ResponseEntity.ok(to);
    }
}
