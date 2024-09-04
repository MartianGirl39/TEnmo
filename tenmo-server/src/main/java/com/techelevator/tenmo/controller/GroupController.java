package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.GroupDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.dto.response.*;
import com.techelevator.tenmo.service.AccountValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="user/account")
@PreAuthorize("isAuthorized()")
public class GroupController {

    @Autowired
    private AccountValidationService validator;
    @Autowired
    private GroupDao groupDao;

    @RequestMapping(path="group", method= RequestMethod.GET)
    public List<ClientGroupDto> getGroups(Principal principal){
        List<ClientGroupDto> groups = new ArrayList<>();
        return groups;
    }

    @RequestMapping(path="group/{id}", method=RequestMethod.GET)
    public ClientGroupDto getGroupById(@PathVariable int id, Principal principal){
        ClientGroupDto group = null;
        return group;
    }

    @RequestMapping(path="group/{id}/member", method=RequestMethod.GET)
    public List<GroupMemberDto> getMembers(@PathVariable int id, Principal principal){
        List<GroupMemberDto> members = new ArrayList<>();
        return members;
    }

    // do I want to do this or by username?
    @RequestMapping(path="group/{id}/member/{accountId}", method=RequestMethod.GET)
    public GroupMemberDto getMemberById(@PathVariable int id, @PathVariable int accountId, Principal principal){
        GroupMemberDto member = null;
        return member;
    }

    @RequestMapping(path="group/{id}/member/{accountId}", method=RequestMethod.GET)
    public AccountDto getMemberAccountById(@PathVariable int id, @PathVariable int accountId, Principal principal){
        AccountDto member = null;
        return member;
    }

    @RequestMapping(path="group/{id}/expense", method=RequestMethod.GET)
    public List<ClientGroupExpenseDto> getExpenses(@PathVariable int id, Principal principal){
        List<ClientGroupExpenseDto> expenses = new ArrayList<>();
        return expenses;
    }

    @RequestMapping(path="group/{groupId}/expense{expenseId}", method=RequestMethod.GET)
    public ClientGroupExpenseDto getExpenseById(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        ClientGroupExpenseDto expense = null;
        return expense;
    }

    @RequestMapping(path="group/{groupId}/expense{expenseId}/contribution", method=RequestMethod.GET)
    public List<ContributionDto> getExpenseContributions(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        List<ContributionDto> contributions = new ArrayList<>();
        return contributions;
    }

    @RequestMapping(path="group/{groupId}/expense{expenseId}/contribution/{contributionId}", method=RequestMethod.GET)
    public ContributionDto getExpenseContributionById(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        ContributionDto contribution = null;
        return contribution;
    }
}
