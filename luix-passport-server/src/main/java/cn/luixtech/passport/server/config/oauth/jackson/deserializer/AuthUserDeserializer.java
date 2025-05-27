package cn.luixtech.passport.server.config.oauth.jackson.deserializer;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Set;

public class AuthUserDeserializer extends JsonDeserializer<AuthUser> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<>() {
    };
    private static final TypeReference<Set<String>>                 STRING_SET                   = new TypeReference<>() {
    };

    /**
     * This method will create {@link AuthUser} object. It will ensure successful object
     * creation even if password key is null in serialized json, because credentials may
     * be removed from the {@link AuthUser} by invoking {@link User#eraseCredentials()}. In
     * that case there won't be any password key in serialized json.
     *
     * @param jp   the JsonParser
     * @param ctxt the DeserializationContext
     * @return the user
     * @throws IOException if a exception during IO occurs
     */
    @Override
    public AuthUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        String username = readJsonNode(jsonNode, "username").asText();
        String email = readJsonNode(jsonNode, "email").asText();
        String mobileNo = readJsonNode(jsonNode, "mobileNo").asText();
        String firstName = readJsonNode(jsonNode, "firstName").asText();
        String lastName = readJsonNode(jsonNode, "lastName").asText();
        String password = passwordNode.asText("");
        String photoUrl = readJsonNode(jsonNode, "photoUrl").asText();
        String locale = readJsonNode(jsonNode, "locale").asText();
        String modifiedTime = readJsonNode(jsonNode, "modifiedTime").asText();

        boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        Set<? extends GrantedAuthority> authorities = mapper.convertValue(jsonNode.get("authorities"), SIMPLE_GRANTED_AUTHORITY_SET);
        Set<String> roles = mapper.convertValue(jsonNode.get("roles"), STRING_SET);
        Set<String> permissions = mapper.convertValue(jsonNode.get("permissions"), STRING_SET);
        Set<String> teamIds = mapper.convertValue(jsonNode.get("teamIds"), STRING_SET);
        AuthUser result = new AuthUser(username, email, mobileNo, firstName, lastName, password, enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, photoUrl, locale, modifiedTime, authorities, roles, permissions, teamIds);
        if (passwordNode.asText(null) == null) {
            result.eraseCredentials();
        }
        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
