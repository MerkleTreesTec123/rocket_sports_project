package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FVirtualFinances;

import java.util.List;
import java.util.Map;

/**
 * 后台虚拟币接口
 *
 * @author ZKF
 */
public interface IAdminSystemCoinTypeService {

    /**
     * 获取虚拟币列表
     *
     * @param page 分页实体对象
     * @param type 虚拟币实体对象
     * @return 分页实体对象
     */
    Pagination<SystemCoinType> selectVirtualCoinList(Pagination<SystemCoinType> page, SystemCoinType type);

    /**
     * 查询虚拟币基本信息
     *
     * @param id 虚拟币ID
     * @return 虚拟币实体对象
     */
    SystemCoinType selectVirtualCoinById(int id);

    /**
     * 新增虚拟币
     *
     * @param coin 虚拟币实体对象
     * @return true：成功，false：失败
     */
    boolean insert(SystemCoinType coin);

    /**
     * 修改虚拟币基本信息
     *
     * @param coin 虚拟币实体对象
     * @return true：成功，false：失败
     */
    boolean updateVirtualCoin(SystemCoinType coin);

    /**
     * 启用虚拟币钱包
     *
     * @param coin 虚拟币实体对象
     * @return true：成功，false：失败
     */
    boolean updateVirtualCoinByEnabled(SystemCoinType coin);

    /**
     * 修改钱包链接
     *
     * @param coin 虚拟币实体对象
     * @return true：成功，false：失败
     */
    boolean updateVirtualCoinWalletLink(SystemCoinType coin);

    /***************虚拟币地址操作****************/
    /**
     * 查询虚拟币地址数量列表
     *
     * @param page 分页实体对象
     * @return 分页实体对象
     */
    Pagination<Map<String, Object>> selectVirtualCoinAddressNumList(Pagination<Map<String, Object>> page);

    /**
     * 生成虚拟币地址(内部执行事物，该方法不走事物)
     *
     * @param virtualCoinType 虚拟币实体对象
     * @param count           生成数量
     * @param password        钱包密码
     * @return 200添加成功, 302钱包连接失败，请检查配置信息，303取地址受限，304钱包连接失败，请检查配置信息，未知错误
     */
    int createVirtualCoinAddress(SystemCoinType virtualCoinType, int count, String password);

    /**
     * 插入存币理财设置
     *
     * @param record 实体对象
     */
    boolean insertVirtualFinances(FVirtualFinances record);

    /**
     * 查询存币理财设置
     *
     * @param fid 主键ID
     */
    FVirtualFinances selectVirtualFinances(Integer fid);

    /**
     * 查询存币理财设置列表
     *
     * @param fcoinid 币种ID
     */
    List<FVirtualFinances> selectVirtualFinancesList(Integer fcoinid, Integer fstate);

    /**
     * 修改存币理财设置
     *
     * @param record 实体对象
     */
    boolean updateVirtualFinances(FVirtualFinances record);

    /**
     * 删除存币理财设置
     *
     * @param fid 主键ID
     */
    boolean deleteVirtualFinances(Integer fid);

    /**
     * 根据币种ID查询币种设置
     */
    List<SystemCoinSetting> selectSystemCoinSettingList(Integer coinId);

    /**
     * 更新币种设置
     */
    boolean updateSystemCoinSetting(SystemCoinSetting record);

    /**
     * 添加币信息
     *
     * @param coinInfo
     * @return
     */
    boolean insertSystemCoinInfo(SystemCoinInfo coinInfo);

    /**
     * 修改币信息
     *
     * @param coinInfo
     * @return
     */
    boolean updateSystemCoinInfo(SystemCoinInfo coinInfo);

    /**
     * 删除币信息
     *
     * @param id
     * @return
     */
    boolean deleteSystemCoinInfo(Integer id);

    /**
     * 查询币种信息
     *
     * @param primaryKey
     * @return
     */
    SystemCoinInfo selectSystemCoinInfo(Integer primaryKey);

    /**
     * 查询币种信息列表
     *
     * @param params
     * @return
     */
    Pagination<SystemCoinInfo> selectSystemCoinInfoList(Map<String,Object> params);






}
