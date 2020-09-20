package com.ffm.lms.branch;

import java.util.Collections;
import java.util.List;

public interface BranchService {

	default List<Branch> list() {
		return Collections.emptyList();
	}
	
	BranchResponseDTO getBranchById(Long id);
	
	BranchResponseDTO create(BranchCreateDTO branch);
	
	BranchResponseDTO modify(BranchUpdateDTO branch, Long branchId);
}
