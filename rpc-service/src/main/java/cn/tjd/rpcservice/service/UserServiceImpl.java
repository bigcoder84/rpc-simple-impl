package cn.tjd.rpcservice.service;

import cn.tjd.rpc.service.UserService;

/**
 * @Auther: TJD
 * @Date: 2020-06-28
 * @DESCRIPTION:
 **/
public class UserServiceImpl implements UserService {
    @Override
    public String login(String username, String password) {
        if ("root".equals(username)&&"123".equals(password)) {
            return "登录成功";
        }
        return "用户名或密码错误登录失败";
    }
}
