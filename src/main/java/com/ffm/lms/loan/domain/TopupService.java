package com.ffm.lms.loan.domain;

import com.ffm.lms.loan.domain.dto.LoanResponseDTO;
import com.ffm.lms.loan.domain.dto.TopUpDTO;

public interface TopupService {

	LoanResponseDTO topUp(TopUpDTO loanDTO);
}
