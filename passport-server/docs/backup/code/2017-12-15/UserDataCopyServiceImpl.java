package org.infinity.passport.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.User;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.UserDataCopyService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserDataCopyServiceImpl implements UserDataCopyService, InitializingBean {

    @Autowired
    private MongoTemplate   embeddedMongoTemplate;

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<User> results = embeddedMongoTemplate.findAll(User.class);
        Assert.isTrue(CollectionUtils.isEmpty(results), "User data copy must be empty.");

        userRepository.findAll().stream().forEach(user -> {
            embeddedMongoTemplate.save(user);
        });

        results = embeddedMongoTemplate.findAll(User.class);
    }

    @Override
    public User insert(String userName, String rawPassword, String firstName, String lastName, String email,
            String mobileNo, String activationKey, Boolean activated, String avatarImageUrl, Boolean enabled,
            String resetKey, Instant resetTime, Set<String> authorityNames) {
        User newUser = new User();
        newUser.setUserName(userName.toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setMobileNo(mobileNo);
        newUser.setEmail(email.toLowerCase());
        newUser.setActivated(activated);
        newUser.setActivationKey(activationKey);
        newUser.setAvatarImageUrl(avatarImageUrl);
        newUser.setResetKey(resetKey);
        newUser.setResetTime(resetTime);
        newUser.setEnabled(enabled);

        embeddedMongoTemplate.save(newUser);
        return newUser;
    }

    @Override
    public void update(String userName, String firstName, String lastName, String email, String mobileNo,
            String modifiedBy, Boolean activated, String avatarImageUrl, Boolean enabled, Set<String> authorityNames) {
        // TODO implement later

    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        Assert.hasText(login, "it must not be null, empty, or blank");
        Query query = new Query(new Criteria().orOperator(Criteria.where(User.FIELD_USER_NAME).is(login),
                Criteria.where(User.FIELD_EMAIL).is(login), Criteria.where(User.FIELD_MOBILE_NO).is(login)));
        User result = embeddedMongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(result);
    }
}
