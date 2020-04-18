package com.qkwl.common.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.GUIDUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * OSS公共接口
 * @author ZKF
 * PS: 新调用在spring中增加以下配置
 * <bean id="ossHelper" class="com.qkwl.common.oss.OssHelper"></bean>
 */
public class OssHelper {

	private RedisHelper redisHelper;
	
	public void setRedisHelper(RedisHelper redisHelper) {
		this.redisHelper = redisHelper;
	}

	/**
	 * 上传文件
	 * @param file
	 * @param filePath
	 * @return
	 */
	public String uploadFile(MultipartFile file, String filePath) {
		String result;
		try {
			result = uploadOSS(OSSConstant.BUCKET_BASE, file.getContentType(), new ByteArrayInputStream(file.getBytes()), file.getBytes().length, file.getOriginalFilename(), filePath);
			if (!TextUtils.isEmpty(result)) {
				return redisHelper.getSystemArgs("imgUploadUrl") + result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * OSS上传文件
	 * @param Bucket
	 * @param contentType
	 * @param fileSize
	 * @param input
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	public String uploadOSS(String Bucket, String contentType, InputStream input, long fileSize, String fileName, String filePath) throws Exception {
		// 参数校验
		if (StringUtils.isEmpty(contentType) || StringUtils.isEmpty(filePath) || StringUtils.isEmpty(fileName)) {
			return null;
		}
		String key = filePath + GUIDUtils.getGUIDString().toLowerCase() + fileName;
		OSSClient client = new OSSClient(OSSConstant.OSS_ENDPOINT, OSSConstant.ACCESS_ID, OSSConstant.ACCESS_KEY);
		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(fileSize);

		try {
			// 创建bucket
			client.createBucket(Bucket);
			client.setBucketAcl(Bucket, CannedAccessControlList.PublicRead);
		} catch (ServiceException e) {
			// 如果Bucket已经存在，则忽略
			if (!OSSErrorCode.BUCKET_ALREADY_EXISTS.equals(e.getErrorCode())) {
				throw e;
			}
		}

		objectMeta.setContentType(contentType);
		client.putObject(Bucket, key, input, objectMeta);
		client.shutdown();
		return key;
	}

}
