package com.qkwl.service.entrust.impl;

import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.MatchTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.EntrustOrderDTO;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.service.entrust.dao.FEntrustHistoryMapper;
import com.qkwl.service.entrust.dao.FEntrustLogMapper;
import com.qkwl.service.entrust.dao.FEntrustMapper;
import com.qkwl.service.entrust.model.EntrustDO;
import com.qkwl.service.entrust.model.EntrustHistoryDO;
import com.qkwl.service.entrust.model.EntrustLogDO;
import com.qkwl.service.entrust.service.EntrustOrder;
import com.qkwl.service.entrust.tx.EntrustOrderTx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 委单接口实现
 *
 * @author LY
 */
@Service("entrustServer")
public class EntrustServerImpl implements IEntrustServer {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(IEntrustServer.class);

    @Autowired
    private FEntrustMapper entrustMapper;
    @Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;
    @Autowired
    private FEntrustLogMapper entrustLogMapper;
    @Autowired
    private EntrustOrder entrustOrder;

    @Autowired
    private EntrustOrderTx entrustOrderTx;

    /**
     * 买单
     *
     * @param entrust 委单数据
     */
    @Override
    public Result createBuyEntrust(EntrustOrderDTO entrust) {
        try {
            return entrustOrder.createEntrust(entrust, EntrustTypeEnum.BUY);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createBuyEntrust_error_" + entrust.toString());
        }
        return Result.failure("委托失败");
    }

    /**
     * 卖单
     *
     * @param entrust 委单数据
     */
    @Override
    public Result createSellEntrust(EntrustOrderDTO entrust) {
        try {
            return entrustOrder.createEntrust(entrust, EntrustTypeEnum.SELL);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createSellEntrust_error_" + entrust.toString());
        }
        return Result.failure("委托失败");
    }

    /**
     * 取消委单
     *
     * @param userId    用户id
     * @param entrustId 委单id
     */
    @Override
    public Result cancelEntrust(Integer userId, BigInteger entrustId) {
        EntrustDO entrust = entrustMapper.selectById(userId, entrustId);
        Result result =  performCancelEntrust(userId, entrustId);
        if (result.getSuccess()) {
            if (entrust.getFmatchtype().equals(MatchTypeEnum.HUOBI.getCode())) {
                entrustOrder.callGroupCancel(entrust.getFid().longValue());
            }
        }
        return result;
    }

    @Transactional
    private Result performCancelEntrust(Integer userId,BigInteger entrustId) {
        if (userId == null) {
            return Result.param("userId is null");
        }
        if (entrustId == null) {
            return Result.param("entrustId is null");
        }
        EntrustDO entrust = entrustMapper.selectByIdLocal(userId, entrustId);
        if (entrust == null) {
            return Result.failure(1000, "委单记录不存在");
        }
        // 状态判断
        if (entrust.getFstatus().equals(EntrustStateEnum.Cancel.getCode()) ||
                entrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode()) ||
                entrust.getFstatus().equals(EntrustStateEnum.WAITCancel.getCode()) ||
                entrust.getFstatus().equals(EntrustStateEnum.SubmitCancel.getCode())){
            return Result.failure(1001, "委单已取消");
        }
        // 更改订单状态
        entrust.setFstatus(EntrustStateEnum.WAITCancel.getCode());
        entrust.setFlastupdattime(Utils.getTimestamp());
        try {
            if (entrustOrderTx.cancleEntrust(entrust)) {
                return Result.success("取消成功");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("cancelEntrust is err,userId:{}, entrustId:{}", userId, entrustId);
        }
        return Result.failure("取消失败");
    }



    /**
     * 分页查询当前委单
     *
     * @param paginParam 分页参数
     * @param entrust    实体参数
     * @param stateList  状态列表
     */
    @Override
    public Pagination<FEntrust> listEntrust(Pagination<FEntrust> paginParam, FEntrust entrust, List<Integer> stateList) {
        Map<String, Object> map = new HashMap<>();

        map.put("offset", paginParam.getOffset());
        map.put("limit", paginParam.getPageSize());
        map.put("fuid", entrust.getFuid());
        map.put("ftradeid", entrust.getFtradeid());
        map.put("fagentid", entrust.getFagentid());
        map.put("stateList", stateList);
        map.put("begindate", paginParam.getBegindate());
        map.put("enddate", paginParam.getEnddate());

        List<EntrustDO> fEntrusts = entrustMapper.selectPageList(map);
        for (EntrustDO fEntrust : fEntrusts) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFlastamount(MathUtils.toScaleNum(fEntrust.getFlastamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        paginParam.setData(PojoConvertUtil.convert(fEntrusts, FEntrust.class));
        if (!StringUtils.isEmpty(paginParam.getRedirectUrl())) {
            int count = entrustMapper.selectPageCount(map);
            paginParam.setTotalRows(count);
            paginParam.generate();
        }
        return paginParam;
    }

    /**
     * 根据id查询委单
     *
     * @param userId    用户id
     * @param entrustId 委单id
     */
    @Override
    public FEntrust getEntrust(Integer userId, BigInteger entrustId) {
        EntrustDO fEntrust = entrustMapper.selectById(userId, entrustId);
        if (fEntrust != null) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFlastamount(MathUtils.toScaleNum(fEntrust.getFlastamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        return PojoConvertUtil.convert(fEntrust, FEntrust.class);
    }

    /**
     * 查询委单日志
     *
     * @param entrustId 委单id
     */
    @Override
    public List<FEntrustLog> getEntrustLog(BigInteger entrustId) {
        List<EntrustLogDO> entrustLogs = entrustLogMapper.selectByEntrustId(entrustId);
        for (EntrustLogDO fEntrustLog : entrustLogs) {
            fEntrustLog.setFamount(MathUtils.toScaleNum(fEntrustLog.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrustLog.setFcount(MathUtils.toScaleNum(fEntrustLog.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrustLog.setFprize(MathUtils.toScaleNum(fEntrustLog.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        return PojoConvertUtil.convert(entrustLogs, FEntrustLog.class);
    }

   // @Override
    //public Result batchCancelEntrust(Integer fuid,Integer symbol) {
//        if (fuid == null){
//            return Result.param("fuid is null");
//        }
//
//        EntrustDO entrustDO = new EntrustDO();
//        entrustDO.setFuid(fuid);
//        entrustDO.setFlastupdattime(Utils.getTimestamp());
//        entrustDO.setFtradeid(symbol);
//        entrustDO.setFstatus(EntrustStateEnum.WAITCancel.getCode());
//        try {
//            if(entrustMapper.updateBatchByUid(entrustDO) > 0){
//                return Result.success("取消成功");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error("batchCancelEntrust is err,userId:{}",fuid);
//        }
//        return Result.failure("取消失败");
       // return null;
    //}

    /**
     * 分页查询历史当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    历史委单实体
     * @param stateList  委单状态列表
     */
    @Override
    public Pagination<FEntrustHistory> listEntrustHistory(Pagination<FEntrustHistory> paginParam,
                                                          FEntrustHistory entrust, List<Integer> stateList) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", paginParam.getOffset());
        map.put("limit", paginParam.getPageSize());
        map.put("fuid", entrust.getFuid());
        map.put("ftradeid", entrust.getFtradeid());
        map.put("fagentid", entrust.getFagentid());
        map.put("stateList", stateList);
        map.put("begindate", paginParam.getBegindate());
        map.put("enddate", paginParam.getEnddate());
        map.put("orderField",paginParam.getOrderField());

        List<EntrustHistoryDO> fEntrustHistories = entrustHistoryMapper.selectPageList(map);
        for (EntrustHistoryDO fEntrust : fEntrustHistories) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        paginParam.setData(PojoConvertUtil.convert(fEntrustHistories, FEntrustHistory.class));
        if (!StringUtils.isEmpty(paginParam.getRedirectUrl())) {
            int count = entrustHistoryMapper.selectPageCount(map);
            paginParam.setTotalRows(count);
            paginParam.generate();
        }
        return paginParam;
    }

    /**
     * 根据id查询历史委单
     *
     * @param userId    用户id
     * @param entrustId 委单id
     */
    @Override
    public FEntrustHistory getEntrustHistory(Integer userId, BigInteger entrustId) {
        EntrustHistoryDO fEntrust = entrustHistoryMapper.selectById(userId, entrustId);
        if (fEntrust != null) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        return PojoConvertUtil.convert(fEntrust, FEntrustHistory.class);
    }
}