package com.ffm.lms.audit;

import java.util.ArrayList;
import java.util.List;

import com.ffm.lms.customer.commons.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResponse<T extends BaseEntity> {

	private List<T> auditObjects = new ArrayList<>();
}
