package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.ExpenseDao;
import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.dto.request.ContribDto;
import com.techelevator.tenmo.model.dto.request.GroupDto;
import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.request.GroupMemberDto;
import com.techelevator.tenmo.model.dto.response.*;
import com.techelevator.tenmo.service.AccountValidationService;
import com.techelevator.tenmo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api")
@PreAuthorize("isAuthenticated()")
public class GroupController {
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private ExpenseDao expenseDao;
    @Autowired
    private AccountValidationService validator;
    @Autowired
    private TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="/group", method=RequestMethod.POST)
    public ClientGroupDto createGroup(@RequestBody GroupDto newGroup, Principal principal){
        ClientGroupDto group = null;
        int accountId = validator.getAndValidateUser(principal.getName()).getAccount_id();
        group = groupDao.createGroup(accountId, newGroup);
        if(group == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating group");
        }
        return group;
    }

    @RequestMapping(path="/group", method= RequestMethod.GET)
    public List<ClientGroupDto> getGroups(Principal principal){
        List<ClientGroupDto> groups = new ArrayList<>();
        groups = groupDao.getGroupByUser(validator.getAndValidateUser(principal.getName()).getAccount_id());
        return groups;
    }

    @RequestMapping(path="/group/{id}", method=RequestMethod.GET)
    public ClientGroupDto getGroupById(@PathVariable int id, Principal principal){
        ClientGroupDto group = null;
        validator.getAndValidateGroupMember(id, principal);
        group = groupDao.getGroupById(id);
        return group;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path="/group/{id}", method=RequestMethod.POST)
    public void deleteGroup(@PathVariable int id, Principal principal){
        validator.getAndValidateGroupMember(id, principal);
        groupDao.deleteGroup(id);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="/group/{id}", method=RequestMethod.PUT)
    public ClientGroupDto updateGroup(@PathVariable int id, @RequestBody GroupDto group, Principal principal){
        validator.getAndValidateGroupMember(id, principal);
        ClientGroupDto updated = groupDao.updateGroup(id, group);
        if(updated != null){
            return updated;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong when updating group");
        }
    }

    @RequestMapping(path="/group/{id}/member", method=RequestMethod.GET)
    public List<ClientGroupMemberDto> getMembers(@PathVariable int id, Principal principal){
        List<ClientGroupMemberDto> members = new ArrayList<>();
        validator.getAndValidateGroupMember(id, principal);
        members = groupDao.getGroupMembers(id);
        if(members.size() == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group was not found");
        }
        return members;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="/group/{id}/member", method=RequestMethod.POST)
    public ClientGroupMemberDto addMember(@PathVariable int id, @RequestBody @Valid GroupMemberDto newMember, Principal principal){
        validator.getAndValidateGroupMember(id, principal);
        ClientGroupMemberDto memberCheck = groupDao.getGroupMember(id, newMember.getMember_id());
        if(memberCheck != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "member already exists");
        }
        ClientGroupMemberDto member = groupDao.addMemberToGroup(id, newMember);
        if(member == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adding member failed, did you format your request body correctly?");
        }
        return member;
    }

    @RequestMapping(path="/group/{id}/member/{accountId}", method=RequestMethod.GET)
    public AccountDto getMemberAccountById(@PathVariable int id, @PathVariable int accountId, Principal principal){
        AccountDto memberAccount = null;
        validator.getAndValidateGroupMember(id, principal);
        ClientGroupMemberDto member = groupDao.getGroupMember(id, accountId);
        if(member == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not locate member, is this account a member?");
        }
        memberAccount = member.getAccount();
        if(memberAccount == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "member account cannot be extracted, does this account exist?");
        }
        return memberAccount;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path="/group/{id}/member/{accountId}", method=RequestMethod.GET)
    public void deleteMember(@PathVariable int id, @PathVariable int accountId, Principal principal){
        AccountDto memberAccount = null;
        ClientGroupMemberDto member = validator.getAndValidateGroupMember(id, principal);
        groupDao.removeMemberFromGroup(id, accountId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="/group/{id}/expense", method=RequestMethod.POST)
    public ClientGroupExpenseDto addExpense(@PathVariable int id, @RequestBody @Valid GroupExpenseDto newExpense, Principal principal){
        validator.getAndValidateGroupMember(id, principal);
        ClientGroupExpenseDto expense = expenseDao.addExpenseToGroup(id, newExpense);
        return expense;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="/group/{groupId}/expense/{expenseId}", method=RequestMethod.PUT)
    public ClientGroupExpenseDto updateExpense(@PathVariable int groupId, @PathVariable int expenseId, @RequestBody @Valid GroupExpenseDto newExpense, Principal principal){
        validator.getAndValidateGroupMember(groupId, principal);
        ClientGroupExpenseDto expense = expenseDao.updateGroupExpense(expenseId, newExpense);
        return expense;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="/group/{groupId}/expense/{expenseId}/settle", method=RequestMethod.PUT)
    public ClientGroupExpenseDto settleExpense(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        ClientGroupMemberDto member = validator.getAndValidateGroupMember(groupId, principal);
        transactionService.settleExpense(expenseId, member.getAccount().getAccount_id());
        GroupExpenseDto expense = expenseDao.getGroupExpenseAsGroupExpenseDto(expenseId);
        if(expense.isRepeating() == true){
            expenseDao.addExpenseToGroup(groupId, expense);
        }
        return expenseDao.getGroupExpense(expenseId);
    }

    @RequestMapping(path="/group/{id}/expense", method=RequestMethod.GET)
    public List<ClientGroupExpenseDto> getExpenses(@PathVariable int id, Principal principal){
        List<ClientGroupExpenseDto> expenses = new ArrayList<>();
        validator.getAndValidateGroupMember(id, principal);
        expenses = expenseDao.getGroupExpenses(id);
        return expenses;
    }

    @RequestMapping(path="group/{groupId}/expense/{expenseId}", method=RequestMethod.GET)
    public ClientGroupExpenseDto getExpenseById(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        ClientGroupExpenseDto expense = null;
        validator.getAndValidateGroupMember(groupId, principal);
        expense = expenseDao.getGroupExpense(expenseId);
        if (expense == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find expense");
        }
        return expense;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="group/{groupId}/expense/{expenseId}", method=RequestMethod.PUT)
    public void deleteExpenseById(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        ClientGroupExpenseDto expense = null;
        validator.getAndValidateGroupMember(groupId, principal);
        expense = expenseDao.getGroupExpense(expenseId);
        if (expense == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find expense");
        }
        expenseDao.deleteGroupExpense(expenseId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="group/{groupId}/expense/{expenseId}/contribution", method=RequestMethod.POST)
    public ContributionDto addContributionToExpense(@PathVariable int groupId, @PathVariable int expenseId, @RequestBody ContribDto amount, Principal principal){
        ClientGroupMemberDto member = validator.getAndValidateGroupMember(groupId, principal);
        if(expenseDao.getExpenseContribution(expenseId, member.getAccount().getAccount_id()) != null){
            return expenseDao.addToExistingContribution(expenseId, member.getAccount().getAccount_id(), amount.getAmount());
        }
        ContributionDto contributionDto = expenseDao.addNewContributionToExpense(expenseId, member.getAccount().getAccount_id(), amount.getAmount());
        return contributionDto;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="group/{groupId}/expense/{expenseId}/contribution", method=RequestMethod.PUT)
    public ContributionDto addExpenseContribution(@PathVariable int groupId, @PathVariable int expenseId, @RequestBody ContribDto amount, Principal principal){
        ClientGroupMemberDto member = validator.getAndValidateGroupMember(groupId, principal);
        ContributionDto contribution = expenseDao.addToExistingContribution(expenseId, member.getAccount().getAccount_id(), amount.getAmount());
        return contribution;
    }

    @RequestMapping(path="group/{groupId}/expense/{expenseId}/contribution", method=RequestMethod.GET)
    public List<ContributionDto> getExpenseContributions(@PathVariable int groupId, @PathVariable int expenseId, Principal principal){
        List<ContributionDto> contributions = new ArrayList<>();
        validator.getAndValidateGroupMember(groupId, principal);
        contributions = expenseDao.getExpenseContributions(expenseId);
        return contributions;
    }

    @RequestMapping(path="group/{groupId}/expense{expenseId}/contribution/{memberId}", method=RequestMethod.GET)
    public ContributionDto getExpenseContributionById(@PathVariable int groupId, @PathVariable int expenseId, @PathVariable int memberId, Principal principal){
        ContributionDto contribution = null;
        validator.getAndValidateGroupMember(groupId, principal);
        contribution = expenseDao.getExpenseContribution(expenseId, memberId);
        if(contribution == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "contribution not found");
        }
        return contribution;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path="group/{groupId}/expense{expenseId}/contribution/{memberId}", method=RequestMethod.POST)
    public void deleteExpenseContributionById(@PathVariable int groupId, @PathVariable int expenseId, @PathVariable int memberId, Principal principal){
        ContributionDto contribution = null;
        validator.getAndValidateGroupMember(groupId, principal);
        contribution = expenseDao.getExpenseContribution(expenseId, memberId);
        if(contribution == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "contribution not found");
        }
        expenseDao.deleteContribution(expenseId, memberId);
    }
}
