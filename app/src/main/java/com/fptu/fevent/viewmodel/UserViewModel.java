package com.fptu.fevent.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        allUsers = repository.getAll();
    }


    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }


    public LiveData<List<User>> getUsersInTeam(int teamId) {
        return repository.getUsersInTeam(teamId);
    }

    // THÊM 2 PHƯƠNG THỨC MỚI NÀY
    public LiveData<List<User>> getUnassignedUsers() {
        return repository.getUnassignedUsers();
    }

    public void updateUser(User user) {
        repository.update(user);
    }
}