/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.order;

import example.customer.Customer;
import example.customer.Customer.CustomerId;
import example.order.Order.LineItem.LineItemId;
import example.order.Order.OrderIdentifier;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Association;
import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;

/**
 * @author Oliver Drotbohm
 */
@Getter
@Table(name = "MyOrder")
public class Order implements AggregateRoot<Order, OrderIdentifier> {

	private final OrderIdentifier id = OrderIdentifier.of(UUID.randomUUID());
	private final Association<Customer, CustomerId> customer;
	private Status status;

	private final List<LineItem> lineItems = new ArrayList<>();

	public Order(CustomerId customerId) {

		this.status = Status.OPEN;
		this.customer = Association.forId(customerId);
	}

	Order complete() {

		this.status = Status.COMPLETED;

		return this;
	}

	Order add(LineItem item) {

		this.lineItems.add(item);

		return this;
	}

	@Value(staticConstructor = "of")
	public static class OrderIdentifier implements Identifier {
		UUID orderId;
	}

	enum Status {
		OPEN, COMPLETED, CANCELLED;
	}

	@Getter
	static class LineItem implements Entity<Order, LineItemId> {

		private LineItemId id;
		private String description;
		private long amount;

		LineItem(String description, long amount) {

			this.id = LineItemId.of(UUID.randomUUID().toString());
			this.description = description;
			this.amount = amount;
		}

		@Value(staticConstructor = "of")
		static class LineItemId implements Identifier {
			String lineItemId;
		}
	}
}
