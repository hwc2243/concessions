package com.concessions.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.concessions.model.Menu.Builder;
import com.concessions.model.base.BaseMenu;

@Entity
@Table(name = "menu")
public class Menu extends BaseMenu<Menu> implements Comparable, Serializable {
	public Menu() {
		super();
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Menu) {
			Menu menu = (Menu) o;
			return this.getName().compareTo(menu.getName());
		}
		return 0;
	}

	public String toString() {
		return this.getName();
	}

// Private constructor to force the use of the Builder
	private Menu(Builder builder) {
		this.name = builder.name;
		this.description = builder.description;
		this.organizationId = builder.organizationId;
	}

	public static class Builder {

		private String name = null;
		private String description = null;
		private Long organizationId = null;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder organizationId(Long organizationId) {
			this.organizationId = organizationId;
			return this;
		}

		/**
		 * The build method creates and returns the immutable Entity object.
		 */
		public Menu build() {
			return new Menu(this);
		}
	}
}