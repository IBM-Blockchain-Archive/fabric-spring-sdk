package org.springframework.data.chaincode.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;;

@NoRepositoryBean
public interface ChaincodeRepository extends Repository<Object, Object> {
	boolean instantiate();
}
