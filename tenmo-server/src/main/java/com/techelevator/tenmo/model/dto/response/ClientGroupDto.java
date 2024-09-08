package com.techelevator.tenmo.model.dto.response;

public class ClientGroupDto {
    int group_id;
    String group_name;
    String description;
    ClientGroupMemberDto creator;

    public ClientGroupDto(){

    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public ClientGroupMemberDto getCreator() {
        return creator;
    }

    public void setCreator(ClientGroupMemberDto creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
