package com.ffm.lms.dashboard;

import java.io.Serializable;
import java.util.List;

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
public class DashBoardContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<DashBoard> totalCompletedLoans;
	private List<DashBoard> totalDefaultedLaons;
	private List<DashBoard> totalActiveLoans;
	private List<DashBoard> totalInflows;
	private List<DashBoard> totalPaid;
	private List<DashBoard> totalOverflow;
	private List<DashBoard> totalUnderflow;
	private List<DashBoard> totalDefaulted;
	
}
