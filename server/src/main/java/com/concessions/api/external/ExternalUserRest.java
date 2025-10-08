package com.concessions.api.external;

import org.springframework.http.ResponseEntity;

import com.concessions.api.external.base.BaseExternalUserRest;
import com.concessions.model.User;

public interface ExternalUserRest extends BaseExternalUserRest
{
	public ResponseEntity<User> getCurrentUser();
}