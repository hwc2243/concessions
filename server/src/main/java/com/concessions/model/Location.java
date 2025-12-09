package com.concessions.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.concessions.model.Location.Builder;
import com.concessions.model.base.BaseLocation;

@Entity
@Table(name = "location")
public class Location extends BaseLocation<Location> implements Comparable, Serializable {
	public Location() {
		super();
	}

	public String toString() {
		return this.getName();
	}

// Private constructor to force the use of the Builder
	private Location(Builder builder) {
		this.name = builder.name;
		this.organizationId = builder.organizationId;
	}

	public static class Builder {

		private String name = null;
		private Long organizationId = null;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder organizationId(Long organizationId) {
			this.organizationId = organizationId;
			return this;
		}

		/**
		 * The build method creates and returns the immutable Entity object.
		 */
		public Location build() {
			return new Location(this);
		}
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Location) {
			Location loc = (Location) o;
			return this.getName().compareTo(loc.getName());
		}
		return 0;
	}
}