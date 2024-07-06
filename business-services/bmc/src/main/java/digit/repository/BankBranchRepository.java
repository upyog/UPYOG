package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.BankBranch;
@Repository
public interface BankBranchRepository extends JpaRepository<BankBranch,Long>{

   // BankBranch getBankBranchByBranchCode (String branchcode);

}
