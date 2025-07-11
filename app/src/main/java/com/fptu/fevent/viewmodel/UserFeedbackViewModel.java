// File mới: com/fptu/fevent/viewmodel/UserFeedbackViewModel.java
package com.fptu.fevent.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.fptu.fevent.model.UserFeedback;
import com.fptu.fevent.repository.UserFeedbacksRepository;
import java.util.List;

public class UserFeedbackViewModel extends AndroidViewModel {
    private final UserFeedbacksRepository repository;
    private final LiveData<List<UserFeedback>> allFeedbacks;

    public UserFeedbackViewModel(@NonNull Application application) {
        super(application);
        repository = new UserFeedbacksRepository(application);
        allFeedbacks = repository.getAll();
    }

    public void insert(UserFeedback feedback) {
        repository.insert(feedback);
    }

    public LiveData<List<UserFeedback>> getAllFeedbacks() {
        return allFeedbacks;
    }
}