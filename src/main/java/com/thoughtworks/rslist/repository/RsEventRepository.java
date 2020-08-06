package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.RsEventEntitiy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RsEventRepository extends CrudRepository<RsEventEntitiy, Integer> {

}
