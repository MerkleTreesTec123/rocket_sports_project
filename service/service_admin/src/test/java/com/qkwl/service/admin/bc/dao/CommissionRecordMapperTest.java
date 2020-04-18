package com.qkwl.service.admin.bc.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommissionRecordMapperTest {

    @Autowired
    CommissionRecordMapper commissionRecordMapper;

    @Test
    @Ignore
    public void getAll() { }

    @Test
    @Ignore
    public void update() {
    }

    @Test
    @Ignore
    public void select() {

    }

    @Test
    public void getCount() {
        Map<String,String> params = new HashMap<>();
        int count = commissionRecordMapper.getCount(params);
        Assert.assertEquals(1,count);
    }

    @Test
    @Ignore
    public void getPageRecord() {

    }
}