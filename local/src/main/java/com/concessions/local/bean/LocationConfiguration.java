package com.concessions.local.bean;

import lombok.Getter;
import lombok.Setter;

public class LocationConfiguration {
	@Getter
	@Setter
	protected long organizationId;

	@Getter
	@Setter
	protected String organizationName;

	@Getter
	@Setter
	protected long locationId;

	@Getter
	@Setter
	protected String locationName;

	@Getter
	@Setter
	protected long menuId;

	@Getter
	@Setter
	protected String menuName;
}
