package io.solo.customer360.nameservice;

import java.util.Objects;

import org.springframework.data.annotation.Id;

record Name(@Id Integer personId, String first, String middle, String last) {

	boolean hasId() {
		return personId != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Name name)) {
			return false;
		}
		return Objects.equals(first, name.first) && Objects.equals(middle, name.middle) && Objects.equals(last, name.last);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, middle, last);
	}
}
