package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.AdminStatusEnum;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.admin.FRole;
import com.qkwl.common.dto.admin.FRoleSecurity;
import com.qkwl.common.dto.admin.FSecurity;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminManageService;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.MQSend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ManagerController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private IAdminManageService adminManageService;
    @Autowired
    private MQSend mqSend;

    /**
     * 权限列表
     */
    @RequestMapping("/admin/goSecurityJSP")
    public ModelAndView goSecurityJSP(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "treeId", required = false) Integer treeId,
            @RequestParam(value = "treeId1", required = false) Integer treeId1,
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fid") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "uid", required = false) Integer uid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (treeId != null) {
            Pagination<FSecurity> page = new Pagination<FSecurity>(currentPage, Constant.adminPageSize);
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
            page = adminManageService.selectSecurityPageList(page, treeId);
            // tree
            List<FSecurity> securityList = adminManageService.selectSecurityAllList();
            StringBuilder tree = new StringBuilder();
            for (FSecurity security : securityList) {
                tree.append("{id:" + security.getFid() + ", pId:" + security.getFparentid() + ", name:\"" + security.getFname() + "\", url:\"admin/goSecurityJSP.html?url=security/securityList&treeId=" + security.getFid() + "\"},");
            }
            modelAndView.addObject("securityTree", tree.substring(0, tree.length() - 1));
            modelAndView.addObject("treeId", treeId);
            modelAndView.addObject("keywords", keywords);
            modelAndView.addObject("page", page);
        } else if (treeId1 != null) {
            FSecurity security = adminManageService.selectSecurityById(treeId1);
            modelAndView.addObject("security", security);
            if (status.equals("update")) {
                FSecurity oldSecurity = adminManageService.selectSecurityById(uid);
                modelAndView.addObject("oldSecurity", oldSecurity);
            }
            modelAndView.addObject("treeId", treeId1);
        } else {

        }
        return modelAndView;
    }

    /**
     * 新增权限
     */
    @ResponseBody
    @RequestMapping(value = "admin/saveSecurity")
    public ReturnResult saveSecurity(
            @RequestParam(value = "fparentid") int fparentid,
            @RequestParam(value = "fpriority") int fpriority,
            @RequestParam(value = "fname") String fname,
            @RequestParam(value = "furl") String furl,
            @RequestParam(value = "fdescription") String fdescription) {
        try {
            FSecurity security = new FSecurity();
            security.setFname(fname);
            security.setFurl(furl);
            security.setFdescription(fdescription);
            security.setFpriority(fpriority);
            security.setFparentid(fparentid);

            boolean i = adminManageService.insertSecurity(security);
            if (i) {
                return ReturnResult.SUCCESS("新增成功");
            } else {
                return ReturnResult.FAILUER("新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 修改权限
     */
    @ResponseBody
    @RequestMapping(value = "admin/updateSecurity")
    public ReturnResult updateSecurity(
            @RequestParam(value = "fid") int fid,
            @RequestParam(value = "fname") String fname,
            @RequestParam(value = "furl") String furl,
            @RequestParam(value = "fdescription") String fdescription) {
        try {
            FSecurity security = adminManageService.selectSecurityById(fid);
            security.setFname(fname);
            security.setFurl(furl);
            security.setFdescription(fdescription);

            boolean i = adminManageService.updateSecurity(security);
            if (i) {
                return ReturnResult.SUCCESS("修改成功");
            } else {
                return ReturnResult.FAILUER("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 删除权限
     */
    @ResponseBody
    @RequestMapping(value = "admin/deleteSecurity")
    public ReturnResult deleteSecurity(
            @RequestParam(value = "uid", required = true) int uid) {
        boolean i = adminManageService.deleteSecurity(uid);
        if (i) {
            return ReturnResult.SUCCESS("删除成功");
        } else {
            return ReturnResult.FAILUER("删除失败");
        }
    }


    /**
     * 角色列表
     */
    @RequestMapping("/admin/roleList")
    public ModelAndView roleList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("security/roleList");
        Pagination<FRole> page = new Pagination<FRole>(currentPage, Constant.adminPageSize);
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }
        page.setOrderField(orderField);
        page.setOrderDirection(orderDirection);
        page = adminManageService.selectRolePageList(page, null);
        modelAndView.addObject("page", page);
        return modelAndView;
    }

    /**
     * 角色列表跳转
     */
    @RequestMapping("admin/goRoleJSP")
    public ModelAndView goRoleJSP(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "uid", required = false) Integer uid,
            @RequestParam(value = "roleId", required = false) Integer roleId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        List<FSecurity> securityList = adminManageService.selectSecurityAllList();
        if (roleId != null) {
            FRole role = adminManageService.selectRoleById(roleId);
            modelAndView.addObject("cPermissonTree", roleSecurityListTree(securityList, role, 0));
            modelAndView.addObject("role", role);
        } else {
            modelAndView.addObject("cPermissonTree", roleSecurityListTree(securityList, null, 0));
        }
        return modelAndView;
    }

    /**
     * 角色权限树生成（layui）
     */
    private JSONArray roleSecurityListTree(List<FSecurity> securityList, FRole role, Integer parentId) {
        JSONArray tree = new JSONArray();
        List<FSecurity> securityListClone= new ArrayList<>(securityList);
        for (FSecurity security : securityListClone) {
            if (!security.getFparentid().equals(parentId)) {
                continue;
            }
            JSONObject treeNode = new JSONObject();
            treeNode.put("name", security.getFname());
            treeNode.put("id", security.getFid());
            treeNode.put("rel", "role[" + security.getFid() + "]");
            treeNode.put("spread", true);
            if (role != null) {
                for (FRoleSecurity roleSecurities : role.getFroleSecurities()) {
                    if (roleSecurities.getFsecurityid().equals(security.getFid())) {
                        treeNode.put("checked", true);
                        break;
                    }
                }
            }
            treeNode.put("children", roleSecurityListTree(securityList, role, security.getFid()));
            tree.add(treeNode);
        }
        return tree;
    }

    /**
     * 保存角色
     */
    @ResponseBody
    @RequestMapping("admin/saveRole")
    public ReturnResult saveRole(
            @RequestParam(value = "fname", required = true) String fname,
            @RequestParam(value = "fdescription", required = true) String fdescription) {
        try {
            FRole role = new FRole();
            role.setFname(fname);
            role.setFdescription(fdescription);
            Integer roleid = adminManageService.insertRole(role);
            List<FSecurity> all = adminManageService.selectSecurityAllList();
            List<FRoleSecurity> roleSecurityList = new ArrayList<FRoleSecurity>();
            if (roleid != null) {
                for (FSecurity fsecurity : all) {
                    int fid = fsecurity.getFid();
                    String key = "role[" + fid + "]";
                    String value = sessionContextUtils.getContextRequest().getParameter(key);
                    if (value != null) {
                        FRoleSecurity roleSecurity = new FRoleSecurity();
                        roleSecurity.setFroleid(roleid);
                        roleSecurity.setFsecurityid(fsecurity.getFid());
                        roleSecurityList.add(roleSecurity);
                    }
                }
            }
            if (roleSecurityList.size() > 0) {
                boolean result = adminManageService.insertRoleSecurity(roleSecurityList, roleid);
                if (result) {
                    return ReturnResult.SUCCESS("新增成功");
                }
            }
            return ReturnResult.FAILUER("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 修改角色
     */
    @RequestMapping("admin/updateRole")
    @ResponseBody
    public ReturnResult updateRole(
            @RequestParam(value = "roleId") int roleId,
            @RequestParam(value = "fname") String fname,
            @RequestParam(value = "fdescription") String fdescription) {
        try {
            FRole role = adminManageService.selectRoleById(roleId);
            if (!role.getFname().equals(fname) || !role.getFdescription().equals(fdescription)) {
                role.setFname(fname);
                role.setFdescription(fdescription);
                adminManageService.updateRole(role);
            }
            List<FSecurity> all = adminManageService.selectSecurityAllList();
            List<FRoleSecurity> roleSecurityList = new ArrayList<FRoleSecurity>();
            for (FSecurity fsecurity : all) {
                int fid = fsecurity.getFid();
                String value = sessionContextUtils.getContextRequest().getParameter("role[" + fid + "]");
                if (value != null) {
                    FRoleSecurity roleSecurity = new FRoleSecurity();
                    roleSecurity.setFroleid(roleId);
                    roleSecurity.setFsecurityid(fsecurity.getFid());
                    roleSecurityList.add(roleSecurity);
                }
            }
            if (roleSecurityList.size() > 0) {
                boolean result = adminManageService.insertRoleSecurity(roleSecurityList, roleId);
                if (result) {
                    return ReturnResult.SUCCESS("修改成功");
                }
            }
            return ReturnResult.FAILUER("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }


    /**
     * 管理员列表
     */
    @RequestMapping("/admin/adminList")
    public ModelAndView adminList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("security/adminList");
        Pagination<FAdmin> page = new Pagination<FAdmin>(currentPage, Constant.adminPageSize);
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }
        page.setOrderField(orderField);
        page.setOrderDirection(orderDirection);
        page = adminManageService.selectAdminPageList(page, null);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 管理员列表跳转
     */
    @RequestMapping("admin/goAdmin")
    public ModelAndView goAdmin(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "uid", required = false) Integer uid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        Map<Integer, Object> roleMap = new HashMap<Integer, Object>();
        List<FRole> all = adminManageService.selectRoleList();
        for (FRole frole : all) {
            roleMap.put(frole.getFid(), frole.getFname());
        }
        if (uid != null) {
            FAdmin fadmin = adminManageService.selectAdminById(uid);
            modelAndView.addObject("fadmin", fadmin);
        }
        modelAndView.addObject("roleMap", roleMap);
        return modelAndView;
    }


    /**
     * 保存管理员
     */
    @ResponseBody
    @RequestMapping("admin/saveAdmin")
    public ReturnResult saveAdmin(
            @RequestParam(value = "fname", required = true) String fname,
            @RequestParam(value = "fpassword", required = true) String fpassword,
            @RequestParam(value = "froleid", required = true) Integer froleid,
            @RequestParam(value = "fagentid", required = false, defaultValue = "0") Integer fagentid) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {
            FAdmin admin = new FAdmin();
            admin.setFname(fname);
            admin.setFpassword(Utils.MD5(fpassword));
            admin.setFroleid(froleid);
            admin.setFstatus(AdminStatusEnum.NORMAL_VALUE);
            admin.setVersion(0);
            admin.setFcreatetime(new Date());
            admin.setFgooglebind(false);
            admin.setFopengooglevalidate(false);
            admin.setFgooglevalidate(false);
            admin.setFagentid(fagentid);
            if (adminManageService.insertAdmin(admin)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.SYSTEM_ADMIN_ADD, ip);
            }
            return ReturnResult.SUCCESS("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 更新管理员
     */
    @ResponseBody
    @RequestMapping("admin/updateAdmin")
    public ReturnResult updateAdmin(
            @RequestParam(value = "fid", required = true) int fid,
            @RequestParam(value = "fpassword", required = true) String fpassword) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {
            FAdmin admin = adminManageService.selectAdminById(fid);
            admin.setFpassword(Utils.MD5(fpassword));
            if (adminManageService.updateAdmin(admin)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.SYSTEM_ADMIN_MODIFY, ip);
            }
            return ReturnResult.SUCCESS("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 禁用管理员
     */
    @ResponseBody
    @RequestMapping("admin/forbbinAdmin")
    public ReturnResult updateAdmin(
            @RequestParam(value = "uid", required = true) int uid,
            @RequestParam(value = "status", required = true) int status) {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {
            FAdmin admin = adminManageService.selectAdminById(uid);
            admin.setFstatus(status);
            boolean flag = adminManageService.updateAdmin(admin);
            if (flag) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                if (status == 1) {
                    mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.SYSTEM_ADMIN_ENABLED, ip);
                } else if (status == 2) {
                    mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.SYSTEM_ADMIN_DISABLE, ip);
                }
            }
            return ReturnResult.SUCCESS("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 更改管理员角色
     */
    @ResponseBody
    @RequestMapping("admin/updateAdminRole")
    public ReturnResult updateAdminRole(
            @RequestParam(value = "fid", required = true) int fid,
            @RequestParam(value = "froleid", required = true) int froleid) {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try {

            FAdmin admin = adminManageService.selectAdminById(fid);
            admin.setFroleid(froleid);
            if (adminManageService.updateAdmin(admin)) {
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.SYSTEM_ADMIN_MODIFY_ROLE, ip);
            }
            return ReturnResult.SUCCESS("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }

    /**
     * 修改密码
     */
    @RequestMapping("admin/updatePassword")
    public ModelAndView updatePassword(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("comm/updatePassword");
        FAdmin fadmin = (FAdmin)(request.getSession().getAttribute("login_admin"));
        modelAndView.addObject("login_admin", fadmin);
        return modelAndView;
    }

    /**
     * 修改密码-提交
     */
    @ResponseBody
    @RequestMapping("admin/updateAdminPassword")
    public ReturnResult updateAdminPassword() throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FAdmin fadmin = (FAdmin)(request.getSession().getAttribute("login_admin"));
        fadmin = adminManageService.selectAdminById(fadmin.getFid());
        String truePassword = fadmin.getFpassword();
        String newPassWord = request.getParameter("fpassword");
        String oldPassword = request.getParameter("oldPassword");

        if (!truePassword.equals(Utils.MD5(oldPassword))) {
            return ReturnResult.FAILUER("原密码输入有误，请重新输入");
        }
        fadmin.setFpassword(Utils.MD5(newPassWord));
        this.adminManageService.updateAdmin(fadmin);
        return ReturnResult.SUCCESS("修改密码成功");
    }
}
