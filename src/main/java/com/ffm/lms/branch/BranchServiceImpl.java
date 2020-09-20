package com.ffm.lms.branch;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.service.BaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BranchServiceImpl extends BaseService implements BranchService {

	private final BranchValidator validator;
	private final BranchRepository repository;

	@Override
	public List<Branch> list() {
		List<Branch> branches = repository.findAll();
		if (Objects.isNull(branches) || branches.isEmpty())
			throw new ApplicationException("Empty list");

		return branches;
	}

	@Override
	public BranchResponseDTO getBranchById(Long id) {
		Optional<Branch> branch = repository.findById(id);
		if (branch.isPresent())
			return mapper.map(branch.get(), BranchResponseDTO.class);
		throw new ApplicationException(String.format("Branch with this id(%s) does not exist", id));
	}

	@Override
	public BranchResponseDTO create(BranchCreateDTO branch) {
		validator.validate(branch);

		return mapper.map(repository.save(mapper.map(branch, Branch.class)), BranchResponseDTO.class);

	}

	@Override
	public BranchResponseDTO modify(BranchUpdateDTO dto, Long branchId) {
		Branch branch = repository.findById(branchId).orElseThrow(
				() -> new ApplicationException(String.format("Branch with this id(%s) does not exist", branchId)));
		branch.setName(dto.getName());
		branch.setOldCode(dto.getOldCode());
		return mapper.map(repository.save(branch), BranchResponseDTO.class);
	}

}
