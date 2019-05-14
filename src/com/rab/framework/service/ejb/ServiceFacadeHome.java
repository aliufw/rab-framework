package com.rab.framework.service.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

public interface ServiceFacadeHome extends javax.ejb.EJBHome {

	public ServiceFacade create() throws CreateException, RemoteException;
}