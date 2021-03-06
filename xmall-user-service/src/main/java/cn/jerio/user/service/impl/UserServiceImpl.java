package cn.jerio.user.service.impl;

import cn.jerio.entity.PageResult;
import cn.jerio.mapper.TbUserMapper;
import cn.jerio.pojo.TbUser;
import cn.jerio.pojo.TbUserExample;
import cn.jerio.user.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jerio on 2018/09/27
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TbUserMapper userMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbUser> page= (Page<TbUser>) userMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());//用户注册时间
        user.setUpdated(new Date());//修改时间
        user.setSourceType("1");//注册来源
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));//密码加密
        System.out.println("密码长度："+user.getPassword().length());
        System.out.println("密码：" +user.getPassword());
        userMapper.insert(user);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbUser user){
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbUser findOne(Long id){
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            userMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbUser user, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();

        if(user!=null){
            if(user.getUsername()!=null && user.getUsername().length()>0){
                criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if(user.getPassword()!=null && user.getPassword().length()>0){
                criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if(user.getPhone()!=null && user.getPhone().length()>0){
                criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if(user.getEmail()!=null && user.getEmail().length()>0){
                criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if(user.getSourceType()!=null && user.getSourceType().length()>0){
                criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if(user.getNickName()!=null && user.getNickName().length()>0){
                criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if(user.getName()!=null && user.getName().length()>0){
                criteria.andNameLike("%"+user.getName()+"%");
            }
            if(user.getStatus()!=null && user.getStatus().length()>0){
                criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
                criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if(user.getQq()!=null && user.getQq().length()>0){
                criteria.andQqLike("%"+user.getQq()+"%");
            }
            if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
                criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
                criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if(user.getSex()!=null && user.getSex().length()>0){
                criteria.andSexLike("%"+user.getSex()+"%");
            }

        }

        Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void createSmsCode(String phone) {
        //1.生成一个6位随机数（验证码）
        final String smscode=  (long)(Math.random()*1000000)+"";
        System.out.println("验证码："+smscode);

        //2.将验证码放入redis
        String key = "smscode_"+phone;
        redisTemplate.boundValueOps(key).append(smscode);
        redisTemplate.expire("smscode_"+phone,60, TimeUnit.SECONDS);
        //发送短信验证码
        // TODO: 2018/09/27  
    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        String key = "smscode_"+phone;
        String systemcode= (String) redisTemplate.boundValueOps(key).get();
        if(StringUtils.isBlank(systemcode)){
            return false;
        }
        return systemcode.equals(code);
    }


    @Override
    public TbUser findByUsername(String username) {
        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        return tbUsers==null || tbUsers.isEmpty()?null:tbUsers.get(0);
    }

    @Override
    public void cacheUserinfo(Long id, String token) {
        redisTemplate.boundHashOps("userinfo").put(token,String.valueOf(id));
    }

    @Override
    public Long getCacheUserinfo(String token) {
        if(redisTemplate.boundHashOps("userinfo").get(token) != null)
            return Long.valueOf(redisTemplate.boundHashOps("userinfo").get(token).toString());
        return null;
    }
}
