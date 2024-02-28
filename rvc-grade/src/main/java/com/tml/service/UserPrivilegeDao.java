package com.tml.service;


import java.util.List;

public interface UserPrivilegeDao {
    List<String> getPrivilege(String uid);
}
