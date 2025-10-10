package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalOrganizationRestImpl;
import com.concessions.model.Organization;
import com.concessions.model.User;
import com.concessions.spring.SessionContext;

@RestController
@RequestMapping("/api/external/organization")
public class ExternalOrganizationRestImpl
   extends BaseExternalOrganizationRestImpl
   implements ExternalOrganizationRest
{

	@Override
	@GetMapping("/mine")
	public ResponseEntity<List<Organization>> mine() {
		Set<Organization> orgs = new HashSet<>();
		
		if (SessionContext.getCurrentUser() != null) {
			User user = SessionContext.getCurrentUser();
			if (user.getOrganization() != null) {
				orgs.add(user.getOrganization());
			}
			if (user.getOrganizations() != null && !user.getOrganizations().isEmpty()) {
				orgs.addAll(user.getOrganizations());
			}
		}
		
		return ResponseEntity.ok(new ArrayList<>(orgs));
	}
}