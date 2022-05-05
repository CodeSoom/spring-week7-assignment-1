package com.codesoom.assignment.infra;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FakeUserStorage implements UserRepository {
    private Map<Long, User> userStorage = new ConcurrentHashMap<Long, User>();


    @Override
    public User save(User user) {
        Long userId = createUserId();
        user = user.initUserId(userId);
        userStorage.put(userId, user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        long count = userStorage.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .count();
        return count > 0;
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = userStorage.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByIdAndDeletedIsFalse(Long id) {
        User user = userStorage.get(id);
        boolean isDeleted = user.isDeleted();
        if (isDeleted) {
            return Optional.empty();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }


    private Long createUserId() {
        List<Long> sortedStorageIds = sortedStorageIds();

        if (sortedStorageIds.size() == 0) {
            return 0L;
        }
        return sortedStorageIds.get(Math.decrementExact(sortedStorageIds.size()));
    }

    private List<Long> sortedStorageIds() {
        return userStorage.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
