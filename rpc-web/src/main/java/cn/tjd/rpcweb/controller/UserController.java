package cn.tjd.rpcweb.controller;

import cn.tjd.rpc.service.UserService;
import cn.tjd.rpcweb.util.RemoteProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @Auther: TJD
 * @Date: 2020-06-28
 * @DESCRIPTION:
 **/
@RestController
public class UserController {

    private UserService userService;

    @PostConstruct
    public void postConstruct() {
        userService = RemoteProxy.getInstance(UserService.class);
    }

    @GetMapping("/login")
    public String login(String username, String password) {
        String result = userService.login(username, password);
        return result;
    }
}
