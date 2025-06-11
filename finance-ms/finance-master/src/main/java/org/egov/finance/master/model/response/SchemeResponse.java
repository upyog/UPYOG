package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.SchemeModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeResponse {

	private ResponseInfo responseInfo;
	private List<SchemeModel> schemes;

}
