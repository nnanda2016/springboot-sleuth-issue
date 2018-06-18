package com.demo.springboot.sleuth;

/**
 * Model class used in demo app.
 * 
 * @author Niranjan Nanda
 */
public class User {
	private String id;
	private String firstName;
	private String lastName;

	/**
	 * Returns the value of id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the value of firstName.
	 *
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the value of firstName.
	 *
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns the value of lastName.
	 *
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the value of lastName.
	 *
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("User {id=");
		builder.append(id);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append("}");
		return builder.toString();
	}
	
}
