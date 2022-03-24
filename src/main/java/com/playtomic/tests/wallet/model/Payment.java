package com.playtomic.tests.wallet.model;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class Payment {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(length = 36, columnDefinition = "varchar", updatable = false, unique = true)
	private UUID uuid;

	@Column(nullable = false, columnDefinition = "DECIMAL(7,2)")
	private BigDecimal amount = BigDecimal.ZERO;

	@Builder.Default
	private Boolean refund = false;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
	private String paymentId;

	@ManyToOne
	@JoinColumn(name = "wallet", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Wallet wallet;
}
