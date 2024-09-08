package com.techelevator.tenmo.model.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class GroupMemberDto {

    @Min(2001)
    private int member_id;
    @NotBlank
    private String member_role;

    public GroupMemberDto(){

    }

    public GroupMemberDto(int member_id, String member_role){
        this.member_id = member_id;
        this.member_role = member_role;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getMember_role() {
        return member_role;
    }

    public void setMember_role(String member_role) {
        this.member_role = member_role;
    }
}
