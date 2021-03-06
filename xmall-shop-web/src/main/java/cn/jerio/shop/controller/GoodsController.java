package cn.jerio.shop.controller;

import cn.jerio.entity.PageResult;
import cn.jerio.entity.Result;
import cn.jerio.pojo.TbGoods;
import cn.jerio.pojogroup.Goods;
import cn.jerio.sellergoods.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Jerio on 2018/09/20
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);
    
    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll(){
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows){
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods){

        //获取登录名
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.getGoods().setSellerId(sellerId);//设置商家ID
        try {
            goodsService.add(goods);
            return Result.success("增加成功");
        } catch (Exception e) {
            logger.error("商品添加失败",e);
            return Result.fail( "增加失败");
        }
    }

    /**
     * 修改
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods){
        //校验是否是当前商家的id
        Goods goods2 = goodsService.findOne(goods.getGoods().getId());
        //获取当前登录的商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果传递过来的商家ID并不是当前登录的用户的ID,则属于非法操作
        if(!goods2.getGoods().getSellerId().equals(sellerId) ||  !goods.getGoods().getSellerId().equals(sellerId) ){
            return Result.fail("操作非法");
        }
        try {
            goodsService.update(goods);
            return Result.success("修改成功");
        } catch (Exception e) {
            logger.error("商品修改失败",e);
            return Result.fail( "修改失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id){
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long [] ids){
        try {
            goodsService.delete(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("商品删除失败",e);
            return Result.fail( "删除失败");
        }
    }

    /**
     * 查询+分页
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }
}
