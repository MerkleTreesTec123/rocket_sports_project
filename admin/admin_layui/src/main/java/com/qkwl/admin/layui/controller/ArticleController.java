package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.admin.layui.utils.MQSend;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.oss.OSSConstant;
import com.qkwl.common.oss.OssHelper;
import com.qkwl.common.rpc.admin.IAdminArticleService;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 文章管理
 *
 * @author ZKF
 */
@Controller
public class ArticleController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IAdminArticleService adminArticleService;
    @Autowired
    private IAdminSettingService adminSettingService;
    @Autowired
    private MQSend mqSend;
    @Autowired
    private OssHelper ossHelper;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 文章列表
     */
    @RequestMapping(value = "/article/articleList")
    public ModelAndView articleList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatedate") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "ftype", defaultValue = "0") int ftype,
            @RequestParam(value = "apptype", defaultValue = "-1") int apptype) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("article/articleList");

        Pagination<FArticle> page = new Pagination<FArticle>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }

        FArticle article = new FArticle();
        if (ftype != 0) {
            article.setFarticletype(ftype);
        }
        if (apptype != -1) {
            article.setFtype(apptype);
        }
        article.setFagentid(WebConstant.BCAgentId);
        page = adminArticleService.selectArticlePageList(page, article);

        Map<Integer, Object> typeMap = new HashMap<Integer, Object>();
        typeMap.put(0, "全部");
        List<FArticleType> all = redisHelper.getArticleTypeList();
        for (FArticleType farticletype : all) {
            typeMap.put(farticletype.getFid(), farticletype.getFname());
        }

        Map<Integer, Object> appMap = new HashMap<Integer, Object>();
        appMap.put(-1, "全部");
        appMap.put(0, "所有端");
        appMap.put(1, "手机端");
        appMap.put(2, "电脑端");
        appMap.put(3, "禁用");


        modelAndView.addObject("typeMap", typeMap);
        modelAndView.addObject("appMap", appMap);
        modelAndView.addObject("ftype", ftype);
        modelAndView.addObject("apptype", apptype);
        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 查询跳转
     */
    @RequestMapping("/article/goArticle")
    public ModelAndView goArticle(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "uid", required = false) Integer uid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (uid != null) {
            FArticle article = adminArticleService.selectArticleById(uid);
            modelAndView.addObject("farticle", article);
        }
        Map<Integer, Object> appMap = new HashMap<Integer, Object>();
        appMap.put(0, "所有端");
        appMap.put(1, "手机端");
        appMap.put(2, "电脑端");
        appMap.put(3, "禁用");
        modelAndView.addObject("appMap", appMap);
        return modelAndView;
    }

    /**
     * 新闻回查
     */
    @RequestMapping("/article/articleTypeLookup")
    public ModelAndView forLookUp(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("article/articleTypeLookup");

        Pagination<FArticleType> page = new Pagination<FArticleType>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }
        page = adminArticleService.selectArticleTypePageList(page, null);
        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);
        return modelAndView;
    }

    /**
     * 删除新闻
     */
    @ResponseBody
    @RequestMapping("/article/deleteArticle")
    public ReturnResult deleteArticle(
            @RequestParam(value = "uid", required = false) Integer uid) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        if (adminArticleService.deleteArticle(uid)) {
            String ip = Utils.getIpAddr(request);
            FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_ADD, ip);
            return ReturnResult.SUCCESS("删除成功");
        } else {
            return ReturnResult.FAILUER("删除失败");
        }
    }

    /**
     * 上传
     */
    @RequestMapping(value = "/article/upload")
    @ResponseBody
    public String upload(
            @RequestParam(value = "file", required = false) MultipartFile filedata,
            @RequestParam(value = "type",required = false,defaultValue = "1") Integer type) throws Exception {
        String realName = filedata.getOriginalFilename();
        if (realName != null && realName.trim().toLowerCase().endsWith("jsp")) {
            return "";
        }
        JSONObject resultJson = new JSONObject();
        String filePath = "";
        if (type == 1) {
            filePath = OSSConstant.ARTICLE;
        } else if (type == 2) {
            filePath = OSSConstant.ABOUT;
        } else if (type == 3) {
            filePath = OSSConstant.ARGS;
        } else if (type == 4) {
            filePath = OSSConstant.COIN;
        } else if (type == 5) {
            filePath = OSSConstant.ASSETS;
        } else if (type == 6) {
            filePath = OSSConstant.ICO;
        } else {
            filePath = OSSConstant.ARTICLE;
        }
        String result = ossHelper.uploadFile(filedata, filePath);

        if (!TextUtils.isEmpty(result)) {
            resultJson.put("state", "SUCCESS");
            resultJson.put("url", result);
        } else {
            resultJson.put("state", "上传失败");
        }
        return resultJson.toString();
    }

    /**
     * 保存新闻
     */
    @ResponseBody
    @RequestMapping("/article/saveArticle")
    public ReturnResult saveArticle(
            @RequestParam(value = "findexImg", required = false) String findexImg,
            @RequestParam(value = "ftitle", required = true) String ftitle,
            @RequestParam(value = "fKeyword", required = true) String fKeyword,
            @RequestParam(value = "fcontent", required = false) String fcontent,
            @RequestParam(value = "fisTop", required = false) String fisTop,
            @RequestParam(value = "articleTypeId", required = true) Integer articleTypeId,
            @RequestParam(value = "ftype", required = true) Integer ftype) throws Exception {
        try {
            HttpServletRequest request = sessionContextUtils.getContextRequest();
            FArticleType articletype = adminArticleService.selectArticleTypeById(articleTypeId);

            FArticle article = new FArticle();
            article.setFarticletype(articletype.getFid());
            article.setFtitle(ftitle);
            article.setFkeyword(fKeyword);
            article.setFcontent(fcontent);
            article.setFupdatetime(Utils.getTimestamp());
            article.setFcreatedate(Utils.getTimestamp());
            article.setVersion(0);
            article.setFlookcount(0);
            article.setFagentid(WebConstant.BCAgentId);
            article.setFtype(ftype);
            if (fisTop != null && !fisTop.isEmpty()) {
                article.setFistop(true);
            } else {
                article.setFistop(false);
            }
            FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
            article.setFcreateadmin(admin.getFid());
            article.setFmodifyadmin(admin.getFid());
            article.setFindeximg(findexImg);
            if (adminArticleService.insertArticle(article)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_ADD, ip);
            }
            return ReturnResult.SUCCESS("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 修改新闻
     */
    @ResponseBody
    @RequestMapping("/article/updateArticle")
    public ReturnResult updateArticle(
            @RequestParam(value = "fid", required = true) int fid,
            @RequestParam(value = "findexImg", required = false) String findexImg,
            @RequestParam(value = "ftitle", required = true) String ftitle,
            @RequestParam(value = "fKeyword", required = true) String fKeyword,
            @RequestParam(value = "fcontent", required = false) String fcontent,
            @RequestParam(value = "fisTop", required = false) String fisTop,
            @RequestParam(value = "articleTypeId", required = true) Integer articleTypeId,
            @RequestParam(value = "ftype", required = true) Integer ftype) throws Exception {
        try {
            HttpServletRequest request = sessionContextUtils.getContextRequest();
            FArticleType articletype = adminArticleService.selectArticleTypeById(articleTypeId);
            FArticle article = adminArticleService.selectArticleById(fid);
            article.setFarticletype(articletype.getFid());
            article.setFtitle(ftitle);
            article.setFkeyword(fKeyword);
            article.setFcontent(fcontent);
            article.setFupdatetime(new Date());
            article.setFtype(ftype);
            if (fisTop != null && !fisTop.isEmpty()) {
                article.setFistop(true);
            } else {
                article.setFistop(false);
            }

            FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
            article.setFmodifyadmin(admin.getFid());
            article.setFindeximg(findexImg);
            if (adminArticleService.updateArticle(article)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_ADD, ip);
            }
            return ReturnResult.SUCCESS("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 新闻类型
     */
    @RequestMapping("/admin/articleTypeList")
    public ModelAndView articleTypeList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("article/articleTypeList");

        Pagination<FArticleType> page = new Pagination<FArticleType>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }

        page = adminArticleService.selectArticleTypePageList(page, null);

        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("articleTypeList", page);
        return modelAndView;
    }

    /**
     * 删除新闻类型
     */
    @RequestMapping("admin/deleteArticleType")
    @ResponseBody
    public ReturnResult deleteArticleType(@RequestParam(value = "uid", required = false) Integer uid) throws Exception {
        if (adminArticleService.deleteArticleType(uid)) {
            String ip = Utils.getIpAddr(super.sessionContextUtils.getContextRequest());
            FAdmin sessionAdmin = (FAdmin) super.sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
            mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_TYPE_ADD, ip);
            return ReturnResult.SUCCESS("删除成功！");
        } else {
            return ReturnResult.FAILUER("删除失败！");
        }
    }

    /**
     * 新闻类型页面跳转
     */
    @RequestMapping("admin/goArticleTypeJSP")
    public ModelAndView goArticleTypeJSP(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "uid", required = false) Integer uid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (uid != null) {
            FArticleType articletype = adminArticleService.selectArticleTypeById(uid);
            modelAndView.addObject("farticleType", articletype);
        }
        return modelAndView;
    }

    /**
     * 保存类型
     */
    @RequestMapping("admin/saveArticleType")
    @ResponseBody
    public ReturnResult saveArticleType(
            @RequestParam(value = "fname", required = true) String fname,
            @RequestParam(value = "ftypeid", required = true) int ftypeid,
            @RequestParam(value = "fdescription", required = false) String fdescription,
            @RequestParam(value = "fkeywords", required = false) String fkeywords,
            @RequestParam(value = "languageTypeLookup.id", required = true) Integer languageTypeId) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {
            FArticleType articleType = new FArticleType();

            articleType.setFcreatetime(new Date());
            articleType.setFupdatetime(new Date());
            articleType.setFlanguageid(languageTypeId);
            articleType.setFname(fname);
            articleType.setFtypeid(ftypeid);
            articleType.setFdescription(fdescription);
            articleType.setFkeywords(fkeywords);

            if (adminArticleService.insertArticleType(articleType)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_TYPE_ADD, ip);

                return ReturnResult.SUCCESS("新增成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ReturnResult.FAILUER("新增失败！");
    }

    /**
     * 修改新闻类型
     */
    @RequestMapping("admin/updateArticleType")
    @ResponseBody
    public ReturnResult updateArticleType(
            @RequestParam(value = "fid", required = true) int fid,
            @RequestParam(value = "fname", required = true) String fname,
            @RequestParam(value = "ftypeid", required = true) int ftypeid,
            @RequestParam(value = "fdescription", required = false) String fdescription,
            @RequestParam(value = "fkeywords", required = false) String fkeywords,
            @RequestParam(value = "languageTypeLookup.id", required = true) Integer languageTypeId) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {
            FArticleType articleType = adminArticleService.selectArticleTypeById(fid);

            articleType.setFupdatetime(new Date());
            articleType.setFlanguageid(languageTypeId);
            articleType.setFname(fname);
            articleType.setFtypeid(ftypeid);
            articleType.setFdescription(fdescription);
            articleType.setFkeywords(fkeywords);

            if (adminArticleService.updateArticleType(articleType)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARTICLE_TYPE_ADD, ip);

                return ReturnResult.SUCCESS("修改成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResult.FAILUER("修改失败！");
    }

    /**
     * 语言查找带回
     */
    @RequestMapping("/admin/lookLanguageTypeList")
    public ModelAndView languageTypeLookup(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "keywords", required = false) String keywords) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("lang/lookLanguageTypeList");

        Pagination<FSystemLan> page = new Pagination<FSystemLan>(currentPage, Constant.adminPageSize);
        page.setKeyword(keywords);

        page = adminSettingService.selectLanguagePageList(page);
        Map<Integer, String> typeMap = new HashMap<Integer, String>();
        typeMap.put(0, "全部");
        List<FSystemLan> all = adminSettingService.selectLanguageList();
        for (FSystemLan item : all) {
            typeMap.put(item.getFid(), item.getFname());
        }
        if (!StringUtils.isEmpty(keywords)) {
            modelAndView.addObject("keywords", keywords);
        }
        modelAndView.addObject("typeMap", typeMap);
        modelAndView.addObject("languageTypeList", page);
        return modelAndView;
    }

}
