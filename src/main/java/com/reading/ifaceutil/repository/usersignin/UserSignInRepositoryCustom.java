package com.reading.ifaceutil.repository.usersignin;

import com.reading.ifaceutil.model.UserSignIn;

import java.util.List;

public interface UserSignInRepositoryCustom {
    public List<UserSignIn> findFrequentUsers();
}
