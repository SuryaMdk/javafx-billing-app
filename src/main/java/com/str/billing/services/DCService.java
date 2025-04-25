package com.str.billing.services;

import com.str.billing.dao.DCDAO;

public class DCService {

	public static boolean isValidDCId(int dcId) {
		return DCDAO.dcIdExist(dcId);
	}

}
