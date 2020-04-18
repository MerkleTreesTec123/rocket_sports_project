package com.qkwl.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;


@ConfigurationProperties("spring.datasource.druid")
public class DruidDataSourceProperties {

    private Boolean testWhileIdle = true;
    private Boolean testOnBorrow;
    private String validationQuery = "SELECT 'x'";
    private Boolean useGlobalDataSourceStat;
    private String filters;
    private Long timeBetweenLogStatsMillis;
    private Integer maxSize;
    private Boolean clearFiltersEnable;
    private Boolean resetStatEnable;
    private Integer notFullTimeoutRetryCount;
    private Integer maxWaitThreadCount;
    private Boolean failFast;
    private Boolean phyTimeoutMillis;
    private Long minEvictableIdleTimeMillis = 300000L;
    private Long maxEvictableIdleTimeMillis;
    private Integer initialSize = 5;
    private Integer minIdle = 5;
    private Integer maxActive = 20;
    private Long maxWait = 60000L;
    private Long timeBetweenEvictionRunsMillis = 60000L;
    private Boolean poolPreparedStatements = true;
    private Integer maxPoolPreparedStatementPerConnectionSize = 20;
    private Properties connectionProperties = new Properties() {{
        put("druid.stat.mergeSql", "true");
        put("druid.stat.slowSqlMillis", "5000");
    }};
    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }
    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }
    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
    public String getValidationQuery() {
        return validationQuery;
    }
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }
    public Boolean getUseGlobalDataSourceStat() {
        return useGlobalDataSourceStat;
    }
    public void setUseGlobalDataSourceStat(Boolean useGlobalDataSourceStat) {
        this.useGlobalDataSourceStat = useGlobalDataSourceStat;
    }
    public String getFilters() {
        return filters;
    }
    public void setFilters(String filters) {
        this.filters = filters;
    }
    public Long getTimeBetweenLogStatsMillis() {
        return timeBetweenLogStatsMillis;
    }
    public void setTimeBetweenLogStatsMillis(Long timeBetweenLogStatsMillis) {
        this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
    }
    public Integer getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
    public Boolean getClearFiltersEnable() {
        return clearFiltersEnable;
    }
    public void setClearFiltersEnable(Boolean clearFiltersEnable) {
        this.clearFiltersEnable = clearFiltersEnable;
    }
    public Boolean getResetStatEnable() {
        return resetStatEnable;
    }
    public void setResetStatEnable(Boolean resetStatEnable) {
        this.resetStatEnable = resetStatEnable;
    }
    public Integer getNotFullTimeoutRetryCount() {
        return notFullTimeoutRetryCount;
    }
    public void setNotFullTimeoutRetryCount(Integer notFullTimeoutRetryCount) {
        this.notFullTimeoutRetryCount = notFullTimeoutRetryCount;
    }
    public Integer getMaxWaitThreadCount() {
        return maxWaitThreadCount;
    }
    public void setMaxWaitThreadCount(Integer maxWaitThreadCount) {
        this.maxWaitThreadCount = maxWaitThreadCount;
    }
    public Boolean getFailFast() {
        return failFast;
    }
    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }
    public Boolean getPhyTimeoutMillis() {
        return phyTimeoutMillis;
    }
    public void setPhyTimeoutMillis(Boolean phyTimeoutMillis) {
        this.phyTimeoutMillis = phyTimeoutMillis;
    }
    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }
    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    public Long getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }
    public void setMaxEvictableIdleTimeMillis(Long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }
    public Integer getInitialSize() {
        return initialSize;
    }
    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }
    public Integer getMinIdle() {
        return minIdle;
    }
    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }
    public Integer getMaxActive() {
        return maxActive;
    }
    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }
    public Long getMaxWait() {
        return maxWait;
    }
    public void setMaxWait(Long maxWait) {
        this.maxWait = maxWait;
    }
    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }
    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
    public Boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
    }
    public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }
    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }
    public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }
    public Properties getConnectionProperties() {
        return connectionProperties;
    }
    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
    public Properties toProperties() {
        Properties properties = new Properties();
        notNullAdd(properties, "testWhileIdle", this.testWhileIdle);
        notNullAdd(properties, "testOnBorrow", this.testOnBorrow);
        notNullAdd(properties, "validationQuery", this.validationQuery);
        notNullAdd(properties, "useGlobalDataSourceStat", this.useGlobalDataSourceStat);
        notNullAdd(properties, "filters", this.filters);
        notNullAdd(properties, "timeBetweenLogStatsMillis", this.timeBetweenLogStatsMillis);
        notNullAdd(properties, "stat.sql.MaxSize", this.maxSize);
        notNullAdd(properties, "clearFiltersEnable", this.clearFiltersEnable);
        notNullAdd(properties, "resetStatEnable", this.resetStatEnable);
        notNullAdd(properties, "notFullTimeoutRetryCount", this.notFullTimeoutRetryCount);
        notNullAdd(properties, "maxWaitThreadCount", this.maxWaitThreadCount);
        notNullAdd(properties, "failFast", this.failFast);
        notNullAdd(properties, "phyTimeoutMillis", this.phyTimeoutMillis);
        notNullAdd(properties, "minEvictableIdleTimeMillis", this.minEvictableIdleTimeMillis);
        notNullAdd(properties, "maxEvictableIdleTimeMillis", this.maxEvictableIdleTimeMillis);
        notNullAdd(properties, "initialSize", this.initialSize);
        notNullAdd(properties, "minIdle", this.minIdle);
        notNullAdd(properties, "maxActive", this.maxActive);
        notNullAdd(properties, "maxWait", this.maxWait);
        notNullAdd(properties, "timeBetweenEvictionRunsMillis", this.timeBetweenEvictionRunsMillis);
        notNullAdd(properties, "poolPreparedStatements", this.poolPreparedStatements);
        notNullAdd(properties, "maxPoolPreparedStatementPerConnectionSize", this.maxPoolPreparedStatementPerConnectionSize);
        return properties;
    }
    private void notNullAdd(Properties properties, String key, Object value) {
        if (value != null) {
            properties.setProperty("druid." + key, value.toString());
        }
    }
}
