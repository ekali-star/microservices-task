package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.metric.UserMetrics;
import com.example.gymcrm.model.User;
import com.example.gymcrm.model.UserComparable;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Optional;

public abstract class UserService<T extends UserComparable> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected abstract JpaRepository<T, Long> getRepository();

    protected abstract Collection<T> findAllUsers();

    protected abstract Long getId(T user);

    protected abstract Optional<T> findByUsernameOptional(String username);

    protected UserMetrics userMetrics;
    protected PasswordEncoder passwordEncoder;

    protected UserService(UserMetrics userMetrics, PasswordEncoder passwordEncoder) {
        this.userMetrics = userMetrics;
        this.passwordEncoder = passwordEncoder;
    }

    protected void afterCreate(T entity) {
    }

    @Transactional
    public AuthCredentials create(T entity) {
        User user = entity.getUser();
        String username = CredentialsGenerator.generateUsername(
                user.getFirstName(),
                user.getLastName(),
                findAllUsers().stream().map(UserComparable::getUser).toList()
        );
        String rawPassword = CredentialsGenerator.generatePassword();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);

        T saved = getRepository().save(entity);
        log.info("{} created - ID: {}, Username: {}",
                getClass().getSimpleName().replace("Service", ""), getId(saved), username);
        afterCreate(saved);

        return new AuthCredentials(username, rawPassword);
    }

    public T findById(Long id) {
        return getRepository().findById(id).orElse(null);
    }

    public Collection<T> findAll() {
        return getRepository().findAll();
    }

    public boolean authenticate(String username, String password) {
        return findByUsernameOptional(username)
                .map(t -> passwordEncoder.matches(password, t.getUser().getPassword()))
                .orElse(false);
    }

    public boolean authenticate(Auth auth) {
        return authenticate(auth.getUsername(), auth.getPassword());
    }

    public void changePassword(Auth auth, String newPassword) {
        if (!authenticate(auth)) throw new IllegalArgumentException("Authentication failed");
        T userEntity = findByUsernameOptional(auth.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userEntity.getUser().setPassword(passwordEncoder.encode(newPassword));
        getRepository().save(userEntity);
    }

    public void setActiveStatus(Auth auth, boolean active) {
        if (!authenticate(auth)) throw new IllegalArgumentException("Authentication failed");
        setActiveStatus(auth.getUsername(), active);
    }

    public void setActiveStatus(String username, boolean active) {
        T userEntity = findByUsernameOptional(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userEntity.getUser().setIsActive(active);
        getRepository().save(userEntity);
    }

    public T findByUsername(String username) {
        return findByUsernameOptional(username).orElse(null);
    }
}