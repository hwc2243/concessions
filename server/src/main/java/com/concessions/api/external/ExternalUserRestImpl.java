package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalUserRestImpl;
import com.concessions.model.User;
import com.concessions.spring.SessionContext;

@RestController
@RequestMapping("/api/external/user")
public class ExternalUserRestImpl
   extends BaseExternalUserRestImpl
   implements ExternalUserRest
{

	@GetMapping("/me")
	@Override
	public ResponseEntity<User> getCurrentUser() {
		if (SessionContext.getCurrentUser() != null) {
			return ResponseEntity.ok(SessionContext.getCurrentUser());
		}
		else {
			return ResponseEntity.noContent().build();
		}
	}
}