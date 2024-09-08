package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.dto.request.GroupDto;
import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.request.GroupMemberDto;
import com.techelevator.tenmo.model.dto.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GroupDao {

    @Autowired
    private AccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    GroupDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ClientGroupDto getGroupById(int id){
        ClientGroupDto group = null;
        String sql = "SELECT group_id, group_name, description FROM friend_group WHERE group_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                group = mapRowToGroup(results);
            }
        }
        catch(DataAccessException err){
            throw new DaoException();
        }
        return group;
    }

    public List<ClientGroupDto> getGroupByUser(int id){
        List<ClientGroupDto> groups = new ArrayList<>();
        String sql = "SELECT friend_group.group_id, group_name, description FROM friend_group JOIN friend_group_member ON friend_group.group_id = friend_group_member.group_id WHERE friend_group_member.member_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            groups.add(this.mapRowToGroup(results));
        }
        return groups;
    }

    @Transactional
    public ClientGroupDto createGroup(int creatorId, GroupDto groupDto){
        ClientGroupDto newGroup = null;
        int groupId = -1;
        String sql = "INSERT INTO friend_group (group_name, description, creator_id, date_created) VALUES (?, ?, ?, ?) RETURNING group_id";
        groupId = jdbcTemplate.queryForObject(sql, int.class, groupDto.getName(), groupDto.getDescription(), creatorId, LocalDate.now());
        newGroup = this.getGroupById(groupId);
        if (newGroup != null){
            ClientGroupMemberDto creator = new ClientGroupMemberDto();
            creator.setMember_role("CREATOR");
            creator.setAccount(accountDao.getAccountDtoById(creatorId));
            this.addMemberToGroup(groupId, new GroupMemberDto(creator.getAccount().getAccount_id(), creator.getMember_role()));
        }
        return newGroup;
    }

    public ClientGroupDto updateGroup(int groupId, GroupDto groupDto){
        String sql = "UPDATE friend_group SET name = ?, description = ? WHERE group_id = ?";
        jdbcTemplate.update(sql, groupDto.getName(), groupDto.getDescription());
        return this.getGroupById(groupId);
    }

    public void deleteGroup(int groupId){
        String sql = "DELETE FROM friend_group WHERE group_id = ?";
        jdbcTemplate.update(sql, groupId);
    }

    public ClientGroupMemberDto getGroupMemberByRole(int groupId, String role){
        ClientGroupMemberDto member = null;
        String sql = "SELECT friend_group_member.member_role, account_id, username, first_name, last_name FROM friend_group_member JOIN account ON account_id = member_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ? AND member_role = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, groupId, "'" + role + "'");
        if(results.next()){
            member = this.mapRowToMembers(results);
        }
        return member;
    }

    public ClientGroupMemberDto getGroupMember(int groupId, int memberId){
        ClientGroupMemberDto member = null;
        String sql = "SELECT friend_group_member.member_role, account_id, username, first_name, last_name FROM friend_group_member JOIN account ON account_id = member_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ? AND member_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, groupId, memberId);
        if(results.next()){
            member = this.mapRowToMembers(results);
        }
        return member;
    }

    public List<ClientGroupMemberDto> getGroupMembers(int id){
        List<ClientGroupMemberDto> members = new ArrayList<>();
        String sql = "SELECT friend_group_member.member_role, account_id, username, first_name, last_name FROM friend_group_member JOIN account ON account_id = member_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            members.add(this.mapRowToMembers(results));
        }
        return members;
    }

    public ClientGroupMemberDto addMemberToGroup(int id, GroupMemberDto groupMemberDto){
        ClientGroupMemberDto newMember = null;
        String sql = "INSERT INTO friend_group_member (group_id, member_id, member_role) VALUES (?, ?, ?) RETURNING member_id";
        int memberId = jdbcTemplate.queryForObject(sql, int.class, id, groupMemberDto.getMember_id(), groupMemberDto.getMember_role());
        return this.getGroupMember(id, memberId);

    }

    public void removeMemberFromGroup(int id, int memberId){
        String sql = "DELETE FROM friend_group_member WHERE group_id = ? AND member_id = ?";
        jdbcTemplate.update(sql, id, memberId);
    }

    private ClientGroupDto mapRowToGroup(SqlRowSet rs){
        ClientGroupDto group = new ClientGroupDto();
        group.setGroup_id(rs.getInt("group_id"));
        group.setGroup_name(rs.getString("group_name"));
        group.setDescription(rs.getString("description"));
        return group;
    }

    private ClientGroupMemberDto mapRowToMembers(SqlRowSet rs){
        ClientGroupMemberDto member = new ClientGroupMemberDto();
        member.setMember_role(rs.getString("member_role"));
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        member.setAccount(account);
        return member;
    }
}
