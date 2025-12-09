package com.concessions.client.model;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.client.model.base.BaseUser;

@Entity
@Table(name="user_",
       uniqueConstraints = {
               @UniqueConstraint(columnNames = {
                       "username"
               })
       }
)
public class User extends BaseUser<User>
    implements Serializable
{
	public User ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private User(Builder builder)
    {
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.authProvider = builder.authProvider;
    }

    public static class Builder {

        private String username = null;
        private String firstName = null;
        private String lastName = null;
        private String authProvider = null;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder authProvider(String authProvider) {
            this.authProvider = authProvider;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public User build() {
            return new User(this);
        }
    }
}