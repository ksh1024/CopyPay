package com.copypay.repository;

import com.copypay.dto.request.ContractRequest;
import com.copypay.dto.request.SettlementInfoRequest;
import com.copypay.dto.response.*;

import java.util.List;

public interface BasicInfoRepository {
    List<BasicInfoListResponse> getBasicInfoList(String inputMid);
    List<String> getManagerId();
    BasicInfoResponse getBasicInfo(String inputBusinessRegNumber);
    List<MemoResponse> getMemoList(String inputMid);
    ContractResponse getContractByBusinessRegNumber(String inputBusinessRegNumber);
    PaymentMethodResponse getPaymentMethodByNo(int no);
    SettlementInfoResponse getSettlementInfoByNo(int no);
    String getNoByBusinessRegNumber(String businessRegNumber);
    int updateContract(ContractRequest contractRequest);
}
