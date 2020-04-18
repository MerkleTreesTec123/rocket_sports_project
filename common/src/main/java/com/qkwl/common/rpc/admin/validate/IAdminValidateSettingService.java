package com.qkwl.common.rpc.admin.validate;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.validate.ValidateAccountDTO;
import com.qkwl.common.dto.validate.ValidateTemplateDTO;
import com.qkwl.service.validate.model.ValidatePlatformDTO;

/**
 * 短信、邮件配置后台管理接口
 */
public interface IAdminValidateSettingService {

    /**
     * 查询账号数据
     *
     * @param id 主键ID
     * @return 账号数据实体
     */
    ValidateAccountDTO getValidateAccount(Integer id);

    /**
     * 新增账号数据
     *
     * @param record 账号数据实体
     * @return true 成功，false 失败
     */
    Boolean insertValidateAccount(ValidateAccountDTO record);

    /**
     * 更新账号数据
     *
     * @param record 账号数据实体
     * @return true 成功，false 失败
     */
    Boolean updateValidateAccount(ValidateAccountDTO record);

    /**
     * 分页查询账号数据
     *
     * @param page   分页参数
     * @param record 账号数据实体
     * @return 分页列表
     */
    Pagination<ValidateAccountDTO> listValidateAccount(Pagination<ValidateAccountDTO> page, ValidateAccountDTO record);

    /**
     * 查询平台数据
     *
     * @param id 主键ID
     * @return 平台数据实体
     */
    ValidatePlatformDTO getValidatePlatform(Integer id);

    /**
     * 新增平台数据
     *
     * @param record 平台数据实体
     * @return true 成功，false 失败
     */
    Boolean insertValidatePlatform(ValidatePlatformDTO record);

    /**
     * 更新平台数据
     *
     * @param record 平台数据实体
     * @return true 成功，false 失败
     */
    Boolean updateValidatePlatform(ValidatePlatformDTO record);

    /**
     * 分页查询平台数据
     *
     * @param page   分页参数
     * @param record 平台数据实体
     * @return 分页列表
     */
    Pagination<ValidatePlatformDTO> listValidatePlatform(Pagination<ValidatePlatformDTO> page, ValidatePlatformDTO record);

    /**
     * 查询消息模板数据
     *
     * @param id 主键ID
     * @return 消息模板数据实体
     */
    ValidateTemplateDTO getValidateTemplate(Integer id);

    /**
     * 更新消息模板数据
     *
     * @param record 消息模板数据实体
     * @return true 成功，false 失败
     */
    Boolean updateValidateTemplate(ValidateTemplateDTO record);

    /**
     * 分页查询消息模板数据
     *
     * @param page   分页参数
     * @param record 消息模板数据实体
     * @return 分页列表
     */
    Pagination<ValidateTemplateDTO> listValidateTemplate(Pagination<ValidateTemplateDTO> page, ValidateTemplateDTO record);


}
