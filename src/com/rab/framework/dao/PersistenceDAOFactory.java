package com.rab.framework.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PersistenceDAOFactory {

	public static PersistenceDAO createPersistenceDAO(PersistenceManager pm){
		SessionFactory sf = pm.getSessionFactory();
		Session session = sf.openSession();
		PersistenceDAOImpl dao = new PersistenceDAOImpl(pm.getSessionFactoryName());
		dao.setSession(session);
		
		return dao;
	}
}
