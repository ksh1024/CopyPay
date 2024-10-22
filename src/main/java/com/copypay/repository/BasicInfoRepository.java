package com.copypay.repository;

import com.copypay.dto.request.*;
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
    int saveContract(ContractRequest contractRequest);
    int saveSettlementInfo(SettlementInfoRequest settlementInfoRequest);
    int savePaymentMethod(PaymentMethodRequest paymentMethodRequest);
    int saveMemo(MemoRequest memoRequest);
    List<BasicInfoViewListResponse> getBasicInfoViewList(BasicInfoViewRequest basicInfoViewRequest);
}
