package com.qkwl.service.admin.validate.impl;

import com.qkwl.service.admin.bc.comm.SystemRedisInit;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.validate.ValidateAccountDTO;
import com.qkwl.common.dto.validate.ValidateTemplateDTO;
import com.qkwl.common.rpc.admin.validate.IAdminValidateSettingService;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.service.validate.model.ValidatePlatformDTO;
import com.qkwl.service.admin.validate.dao.ValidateAccountMapper;
import com.qkwl.service.admin.validate.dao.ValidatePlatformMapper;
import com.qkwl.service.admin.validate.dao.ValidateTemplateMapper;
import com.qkwl.service.admin.validate.model.ValidateAccountDO;
import com.qkwl.service.admin.validate.model.ValidatePlatformDO;
import com.qkwl.service.admin.validate.model.ValidateTemplateDO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信、邮件配置后台管理接口实现
 */
@Service("adminValidateService")
public class AdminValidateSettingServiceImpl implements IAdminValidateSettingService {

    @Autowired
    private ValidateAccountMapper validateAccountMapper;
    @Autowired
    private ValidatePlatformMapper validatePlatformMapper;
    @Autowired
    private ValidateTemplateMapper validateTemplateMapper;
    @Autowired
    private SystemRedisInit systemRedisInit;

    @Override
    public ValidateAccountDTO getValidateAccount(Integer id) {
        return PojoConvertUtil.convert(validateAccountMapper.select(id), ValidateAccountDTO.class);
    }

    @Override
    public Boolean insertValidateAccount(ValidateAccountDTO record) {
        ValidateAccountDO validateAccount = PojoConvertUtil.convert(record, ValidateAccountDO.class);
        validateAccount.setGmtCreate(new Date());
        validateAccount.setGmtModified(new Date());
        validateAccount.setVersion(0);

        if(validateAccountMapper.insert(validateAccount) > 0){
            systemRedisInit.initValidateAccount();
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateValidateAccount(ValidateAccountDTO record) {
        ValidateAccountDO validateAccount = PojoConvertUtil.convert(record, ValidateAccountDO.class);
        validateAccount.setGmtModified(new Date());

        if(validateAccountMapper.update(validateAccount) > 0){
            systemRedisInit.initValidateAccount();
            return true;
        }
        return false;
    }

    @Override
    public Pagination<ValidateAccountDTO> listValidateAccount(Pagination<ValidateAccountDTO> page, ValidateAccountDTO record) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("keyword", page.getKeyword());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());

        int count = validateAccountMapper.countPage(map);
        if (count > 0) {
            List<ValidateAccountDO> validateAccountList = validateAccountMapper.listPage(map);
            page.setData(PojoConvertUtil.convert(validateAccountList, ValidateAccountDTO.class));
        }
        page.setTotalRows(count);
        return page;
    }

    @Override
    public ValidatePlatformDTO getValidatePlatform(Integer id) {
        return PojoConvertUtil.convert(validatePlatformMapper.select(id), ValidatePlatformDTO.class);
    }

    @Override
    public Boolean insertValidatePlatform(ValidatePlatformDTO record) {
        ValidatePlatformDO validatePlatform = PojoConvertUtil.convert(record, ValidatePlatformDO.class);
        validatePlatform.setGmtCreate(new Date());
        validatePlatform.setGmtModified(new Date());
        validatePlatform.setVersion(0);

        if(validatePlatformMapper.insert(validatePlatform) > 0){
            systemRedisInit.initValidatePlatform();
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateValidatePlatform(ValidatePlatformDTO record) {
        ValidatePlatformDO validatePlatform = PojoConvertUtil.convert(record, ValidatePlatformDO.class);
        validatePlatform.setGmtModified(new Date());

        if(validatePlatformMapper.update(validatePlatform) > 0){
            systemRedisInit.initValidatePlatform();
            return true;
        }
        return false;
    }

    @Override
    public Pagination<ValidatePlatformDTO> listValidatePlatform(Pagination<ValidatePlatformDTO> page, ValidatePlatformDTO record) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("keyword", page.getKeyword());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());

        int count = validatePlatformMapper.countPage(map);
        if (count > 0) {
            List<ValidatePlatformDO> validatePlatformList = validatePlatformMapper.listPage(map);
            page.setData(PojoConvertUtil.convert(validatePlatformList, ValidatePlatformDTO.class));
        }
        page.setTotalRows(count);
        return page;
    }

    @Override
    public ValidateTemplateDTO getValidateTemplate(Integer id) {
        return PojoConvertUtil.convert(validateTemplateMapper.select(id), ValidateTemplateDTO.class);
    }

    @Override
    public Boolean updateValidateTemplate(ValidateTemplateDTO record) {
        ValidateTemplateDO validateTemplate = PojoConvertUtil.convert(record, ValidateTemplateDO.class);
        validateTemplate.setGmtModified(new Date());
        if(validateTemplateMapper.update(validateTemplate) > 0){
            systemRedisInit.initValidateTemplate();
            return true;
        }
        return false;
    }

    @Override
    public Pagination<ValidateTemplateDTO> listValidateTemplate(Pagination<ValidateTemplateDTO> page, ValidateTemplateDTO record) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("keyword", page.getKeyword());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());

        int count = validateTemplateMapper.countPage(map);
        if (count > 0) {
            List<ValidateTemplateDO> validateTemplateList = validateTemplateMapper.listPage(map);
            page.setData(PojoConvertUtil.convert(validateTemplateList, ValidateTemplateDTO.class));
        }
        page.setTotalRows(count);
        return page;
    }
}
