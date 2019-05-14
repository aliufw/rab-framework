package com.rab.sys.security.login.blh;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.security.TicketImpl;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.security.LoginAuthorizationManager;
import com.rab.sys.security.login.event.ChangeModuleInfoRequestEvent;
import com.rab.sys.security.login.event.ChangeModuleInfoResponseEvent;
import com.rab.sys.security.login.event.LoginRequestEvent;
import com.rab.sys.security.login.event.LoginResponseEvent;

public class LoginBLH extends BaseDomainBLH{

	public BaseResponseEvent login(BaseRequestEvent reqEvent) throws BaseCheckedException {
//		if(true) throw new VHBaseCheckedException("0000001");

		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		LoginRequestEvent req = (LoginRequestEvent)reqEvent;
		LoginResponseEvent resp = new LoginResponseEvent();
		
		String usercode = req.getUsercode();
		String passwd = req.getPasswd();
		
		LoginAuthorizationManager slm = new LoginAuthorizationManager();
		slm.setDao(dao);
		
		Ticket ticket = slm.accredit(usercode, passwd);
		
		resp.setTicket((TicketImpl)ticket);
		
//		//------------------------------
//		//测试代码
//		String compCode = "100101";
//		String copyCode = "001";
//		String tableId = "sys_store_dict";
//		String bizTable = "T_LFW";
//		User user = ticket.getUser();
//		
//		VHSecurityManager sm = VHSecurityManagerFactory.getSecurityManager();
//		String s = ((VHSecurityManagerImpl)sm).createDataRightFilter(compCode, copyCode, user, tableId, bizTable, dao);
//		System.out.println("*********** " + s);
//		//------------------------------
		
		return resp;
	}

	public BaseResponseEvent updateLogonEnvironment(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		ChangeModuleInfoResponseEvent resp = new ChangeModuleInfoResponseEvent();
		if(this.domainSession != null){
			LogonEnvironment logonEnvironment = this.domainSession.getLogonEnvironment();
			if(logonEnvironment != null){
				ChangeModuleInfoRequestEvent req = (ChangeModuleInfoRequestEvent)reqEvent;
				logonEnvironment.setOrgId(req.getLogonEnvironment().getOrgId());
				logonEnvironment.setSOB(req.getLogonEnvironment().getSOB());
				logonEnvironment.setModule(req.getLogonEnvironment().getModule());	
				logonEnvironment.setAcctYear((req.getLogonEnvironment().getAcctYear()));	
				logonEnvironment.setCurrDate((req.getLogonEnvironment().getCurrDate()));	
				
				logger.debug("登录后设置当前环境变量：logonEnvironment = " + logonEnvironment);
			}			
			
			resp.setSuccess(true);			
		}
		if(this.domainSession == null || this.domainSession.getLogonEnvironment() == null){
			resp.setSuccess(false);
		}
		
		return resp;
	}
	
//	public BaseResponseEvent test02(BaseRequestEvent reqEvent) throws VHBaseCheckedException {
//		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
//		
//		LoginRequestEvent req = (LoginRequestEvent)reqEvent;
//		LoginResponseEvent resp = new LoginResponseEvent();
//		
//		EmployeeBO e = new EmployeeBO();
//		e.setName("张三");
//		e.setTel("123456");
//		e.setZipcode("100033");
////		dao.insertSingleRow(e);
//		
//		Session session = ((PersistenceDAOImpl)dao).getSession();
//		session.save(e);
//		System.out.println("dao = " + dao);
//		System.out.println("domainsession = " + domainSession);
//		
//		return resp;
//	}

}