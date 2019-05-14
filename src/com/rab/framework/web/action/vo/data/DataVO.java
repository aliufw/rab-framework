package com.rab.framework.web.action.vo.data;

import com.rab.framework.comm.dto.vo.BaseValueObject;
import com.rab.framework.web.action.vo.ComponentType;

public class DataVO extends BaseValueObject{
	private static final long serialVersionUID = -4838777030458700365L;

	private ComponentType type;

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

}
