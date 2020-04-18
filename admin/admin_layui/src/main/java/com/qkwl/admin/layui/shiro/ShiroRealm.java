package com.qkwl.admin.layui.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.Enum.AdminStatusEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.admin.FRole;
import com.qkwl.common.dto.admin.FSecurity;
import com.qkwl.common.rpc.admin.IAdminManageService;

@Component("shiroDbRealm")
public class ShiroRealm extends AuthorizingRealm  {

	@Autowired
	private IAdminManageService adminService;

	public ShiroRealm() {
		super.setAuthorizationCachingEnabled(false);
	}
	
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if (SecurityUtils.getSubject().getSession().getAttribute("permissions") != null) {
			info = (SimpleAuthorizationInfo) SecurityUtils.getSubject().getSession().getAttribute("permissions");
		} else {
			// 获取当前登录的用户名
			String name = (String) super.getAvailablePrincipal(principals);
			List<String> permissions = new ArrayList<String>();
			FAdmin filterFAdmin = new FAdmin();
			filterFAdmin.setFname(name);
			FAdmin admin = adminService.selectAdminByProperty(filterFAdmin);
			if (admin != null) {
				if (admin.getFroleid() != null) {
					System.out.println("admin");
					// 查找角色
					FRole fRole = adminService.selectRoleById(admin.getFroleid());
					// 给当前用户设置角色
					info.addRole(fRole.getFname());
					List<FSecurity> set = adminService.selectSecurityList(fRole.getFid());
					for (FSecurity froleSecurity : set) {
						permissions.add(froleSecurity.getFurl());
					}
				}
			} else {
				throw new AuthorizationException();
			}
			// 给当前用户设置权限
			info.addStringPermissions(permissions);
			SecurityUtils.getSubject().getSession().setAttribute("permissions", info);
		}
		return info;
	}

	/**
	 * 认证回调函数,登录时调用.
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		FAdmin filterFAdmin = new FAdmin();
		filterFAdmin.setFname(token.getUsername());
		filterFAdmin.setFpassword(new String(token.getPassword()));
		FAdmin fadmin = null;
		try {
			fadmin = this.adminService.selectAdminByProperty(filterFAdmin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticationException("登录异常！");
		}
		if (fadmin != null) {
			if (fadmin.getFstatus() == AdminStatusEnum.FORBBIN_VALUE) {
				throw new AuthenticationException("管理员已禁用！");
			}
			SecurityUtils.getSubject().getSession().setTimeout(3600000);
			SecurityUtils.getSubject().getSession().setAttribute("login_admin", fadmin);
			return new SimpleAuthenticationInfo(fadmin.getFname(), fadmin.getFpassword(), fadmin.getFname());
		} else {
			throw new AuthenticationException("错误的用户名或密码！");
		}
	}
}