package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.RsEventEntitiy;
import com.thoughtworks.rslist.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RsEventRepository extends CrudRepository<RsEventEntitiy, Integer> {

    List<RsEventEntitiy> findAll();

}
