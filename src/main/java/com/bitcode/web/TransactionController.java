package com.bitcode.web;

import com.bitcode.agent.account.AccountAmount;
import com.bitcode.agent.account.AccountDB;
import com.bitcode.agent.AgentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(path = "/transaction")
public class TransactionController {

    @Autowired
    AccountDB accountDB;

    private static AgentManager agentManager = new AgentManager();

    @RequestMapping(method = GET)
    public ModelAndView home(@RequestParam("address") String address) {

        ModelAndView mv = new ModelAndView();

        if(accountDB.has(address)) {

            AccountAmount aa = accountDB.get(address);
            mv.addObject("account", aa);
        }

        mv.setViewName("transaction");

        return mv;
    }

    @RequestMapping(path = "buy", method = GET)
    public ModelAndView buy(@RequestParam("address") String address) {
        ModelAndView mv = new ModelAndView();
        if(accountDB.has(address)) {

            AccountAmount aa = accountDB.get(address);
            mv.addObject("account", aa);
        }
        mv.setViewName("transaction_buy");
        return mv;
    }

}