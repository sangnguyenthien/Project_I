package template.service;

import template.persistence.dto.Group;

import java.util.List;

public interface GroupService {
    void createGroup(Group newGroup, String ownerId , List<String> userId);
    void createTeam(String groupId);
    void deleteTeam(String groupId);
    void addMemberToTeam(String groupId,List<String> memberIds);
}
