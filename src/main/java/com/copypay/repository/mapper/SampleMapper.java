package com.copypay.repository.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SampleMapper {

    List<Map<String, Object>> getList();

}
