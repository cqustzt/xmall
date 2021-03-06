package cn.jerio.user.service;

import cn.jerio.entity.PageResult;
import cn.jerio.pojo.TbUser;

import java.util.List;

/**
 * Created by Jerio on 2018/09/27
 */
public interface UserService {

    /**
     * 返回全部列表
     * @return
     */
    public List<TbUser> findAll();


    /**
     * 返回分页列表
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);


    /**
     * 增加
     */
    public void add(TbUser user);


    /**
     * 修改
     */
    public void update(TbUser user);


    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    public TbUser findOne(Long id);


    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long [] ids);

    /**
     * 分页
     * @param pageNum 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbUser user, int pageNum, int pageSize);


    /**
     * 发送短信验证码
     * @param phone
     */
    public void createSmsCode(String phone);

    /**
     * 校验验证码
     * @param phone
     * @param code
     * @return
     */
    public boolean checkSmsCode(String phone,String code);

    /**
     * 根据用户名获取用户
     */
    TbUser findByUsername(String username);

    void cacheUserinfo(Long id, String token);

    Long getCacheUserinfo(String token);
}
