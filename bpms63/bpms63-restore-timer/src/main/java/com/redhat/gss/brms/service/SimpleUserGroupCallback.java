package com.redhat.gss.brms.service;

import java.util.List;

import org.kie.api.task.UserGroupCallback;

public class SimpleUserGroupCallback implements UserGroupCallback {

    public boolean existsUser(String userId) {
        return true;
    }

    public boolean existsGroup(String groupId) {
        return true;
    }

    public List<String> getGroupsForUser(String userId,
            List<String> groupIds, List<String> allExistingGroupIds) {
        return allExistingGroupIds;
    }
}