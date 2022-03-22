package com.playtomic.tests.wallet.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "wallet")
@EntityListeners(AuditingEntityListener.class)
public class Wallet {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(length = 36, columnDefinition = "varchar", updatable = false, unique = true)
	private UUID uuid;

	@Column(nullable = false, columnDefinition = "DECIMAL(7,2)")
	private BigDecimal balance = BigDecimal.ZERO;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date lastModifiedDate;

	@Builder.Default
	private Boolean enable = true;

}
