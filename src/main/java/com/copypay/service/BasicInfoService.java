package com.copypay.service;

import com.copypay.dto.Pagination;
import com.copypay.dto.request.*;
import com.copypay.dto.response.*;
import com.copypay.exception.*;
import com.copypay.repository.BasicInfoRepository;
import com.copypay.repository.SalesManagementRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicInfoService {
    private final BasicInfoRepository basicInfoRepository;
    private final SalesManagementService salesManagementService;
    private final SalesManagementRepository salesManagementRepository;

    //기본정보 등록/변경의 조회
    public List<BasicInfoResponse> getBasicInfoList(BasicInfoRequest basicInfoRequest) {
        List<BasicInfoResponse> basicInfoList = basicInfoRepository.getBasicInfoList(basicInfoRequest);
        return salesManagementService.validateListNotEmpty(
                basicInfoList,
                String.format("총 %d개의 기본 정보 항목을 성공적으로 가져왔습니다", basicInfoList.size()),
                String.format("MID %s에 대한 기본 정보가 없습니다.", basicInfoRequest.getMid())
        );
    }

    //담당자 ID 가져오기
    public List<String> getManagerId(){
        List<String> managerIds = basicInfoRepository.getManagerId();
        if(managerIds.isEmpty()){
            log.error("담당자 ID 불러오기에 실패했습니다.");
            throw new LoadFailedException();
        }
        log.info("총 {}개의 담당자 ID를 성공적으로 가져왔습니다.", managerIds.size());
        return managerIds;
    }

    //사업자번호로 기본정보(일반정보, 정산정보, 계약수단) 가져오기
    public BasicInfosResponse getBasicInfo(String businessRegNumber){
        ContractResponse contract = basicInfoRepository.getContractByBusinessRegNumber(businessRegNumber);
        if (contract == null) {
            log.error("사업자번호 {}에 대한 기본 정보가 없습니다.", businessRegNumber);
            throw new BusinessRegNumberNotFoundException();
        }
        SettlementInfoResponse settlementInfo = basicInfoRepository.getSettlementInfoByNo(contract.getNo());
        PaymentMethodResponse paymentMethod = basicInfoRepository.getPaymentMethodByNo(contract.getNo());

        if(settlementInfo == null){
            log.info("사업자번호 {}에 대한 정산 정보가 없습니다.", businessRegNumber);
        } else if(paymentMethod == null){
            log.info("사업자번호 {}에 대한 결제수단 정보가 없습니다.", businessRegNumber);
        }

        log.info("사업자번호 {}에 대한 기본 정보를 성공적으로 가져왔습니다.", businessRegNumber);
        return new BasicInfosResponse(contract, settlementInfo, paymentMethod);
    }

    //MID로 메모 내역 가져오기
    public List<MemoResponse> getMemoList(String inputMid){
        List<MemoResponse> memoList = basicInfoRepository.getMemoList(inputMid);
        return salesManagementService.validateListNotEmpty(
                memoList,
                String.format("총 %d개의 기본 정보 항목을 성공적으로 가져왔습니다", memoList.size()),
                String.format("MID %s에 대한 메모내역이 없습니다.", inputMid)
        );
    }

    //일반정보(계약) & 메모 저장
    @Transactional
    public void saveContract(BasicInfosRequest basicInfosRequest) {
        int rowsAffected = basicInfoRepository.saveContract(basicInfosRequest.getContractRequest());
        rowsAffected += basicInfoRepository.saveMemo(basicInfosRequest.getMemoRequest());
        if(rowsAffected < 2) {
            logSaveError("사업자번호", basicInfosRequest.getContractRequest().getBusinessRegNumber(),"일반정보(계약) 저장 실패");
        }else{
            log.info("사업자번호 : {} 계약 정보가 성공적으로 저장되었습니다.", basicInfosRequest.getContractRequest().getBusinessRegNumber());
        }
    }

    //사업자번호로 NO 가져오기
    public String getNoByBusinessRegNumber(String businessRegNumber){
        return basicInfoRepository.getNoByBusinessRegNumber(businessRegNumber);
    }

    //정산정보 & 메모 저장
    @Transactional
    public void saveSettlementInfo(BasicInfosRequest basicInfosRequest){
        int rowsAffected = basicInfoRepository.saveSettlementInfo(basicInfosRequest.getSettlementInfoRequest());
        rowsAffected += basicInfoRepository.saveMemo(basicInfosRequest.getMemoRequest());
        if(rowsAffected < 2){
            logSaveError("NO", String.valueOf(basicInfosRequest.getSettlementInfoRequest().getNo()),"정산정보 저장 실패");
        }else{
            log.info("NO : {} 정산정보 정보가 성공적으로 저장되었습니다.", basicInfosRequest.getSettlementInfoRequest().getNo());
        }
    }

    //결제수단 & 메모 저장
    @Transactional
    public void savePaymentMethod(BasicInfosRequest basicInfosRequest){
        int rowsAffected = basicInfoRepository.savePaymentMethod(basicInfosRequest.getPaymentMethodRequest());
        rowsAffected += basicInfoRepository.saveMemo(basicInfosRequest.getMemoRequest());
        if(rowsAffected < 2){
            logSaveError("NO", String.valueOf(basicInfosRequest.getPaymentMethodRequest().getNo()),"결제수단 저장 실패");
        }else{
            log.info("NO : {} 결제수단이 성공적으로 저장되었습니다.", basicInfosRequest.getPaymentMethodRequest().getNo());
        }
    }

    //메모 저장
    public void saveMemo(MemoRequest memoRequest){
        int rowsAffected = basicInfoRepository.saveMemo(memoRequest);
        if(rowsAffected == 0){
            logSaveError("MID", memoRequest.getMid(),"메모 저장 실패");
        }else{
            log.info("MID : {} 메모가 성공적으로 저장되었습니다.", memoRequest.getMid());
        }
    }

    //기본정보(일반정보, 정산정보, 계약수단 한꺼번에) 저장
    @Transactional
    public void saveBasicInfo(BasicInfosRequest basicInfosRequest){
        int rowsAffected = basicInfoRepository.saveContract(basicInfosRequest.getContractRequest());
        if(rowsAffected == 0){
            logSaveError("사업자번호", basicInfosRequest.getContractRequest().getBusinessRegNumber(),"계약 저장 실패");
        }

        rowsAffected = basicInfoRepository.saveSettlementInfo(basicInfosRequest.getSettlementInfoRequest());
        if(rowsAffected == 0){
            logSaveError("NO", String.valueOf(basicInfosRequest.getSettlementInfoRequest().getNo()),"정산정보 저장 실패");
        }

        rowsAffected = basicInfoRepository.savePaymentMethod(basicInfosRequest.getPaymentMethodRequest());
        if(rowsAffected == 0){
            logSaveError("NO", String.valueOf(basicInfosRequest.getPaymentMethodRequest().getNo()),"결제수단 저장 실패");
        }

        rowsAffected = basicInfoRepository.saveMemo(basicInfosRequest.getMemoRequest());
        if(rowsAffected == 0){
            logSaveError("MID", String.valueOf(basicInfosRequest.getMemoRequest().getMid()),"메모 저장 실패");
        }

        log.info("사업자번호 : {} 기본정보가 성공적으로 저장되었습니다.", basicInfosRequest.getContractRequest().getBusinessRegNumber());
    }

    //기본정보 조회의 조회
    public List<BasicInfoViewResponse> getBasicInfoViewList(BasicInfoViewRequest basicInfoViewRequest){
        List<BasicInfoViewResponse> list = basicInfoRepository.getBasicInfoViewList(basicInfoViewRequest);
        return salesManagementService.validateListNotEmpty(
                list,
                String.format("총 %d개의 기본 정보 항목을 성공적으로 가져왔습니다", list.size()),
                "조건에 맞는 기본정보가 없습니다."
        );
    }

    //기본정보 등록/변경의 조회에서 리스트 총 개수 가져오기
    public int getBasicInfoViewListTotalCount(BasicInfoViewRequest basicInfoViewRequest){
        return basicInfoRepository.countBasicInfoViewList(basicInfoViewRequest);
    }

    //기본정보 조회의 조회에서 리스트 총 개수 가져오기
    public int getBasicInfoListTotalCount(BasicInfoRequest basicInfoRequest){
        return basicInfoRepository.countBasicInfoList(basicInfoRequest);
    }

    // 영업관리의 계약 진행현황의 조회에서 리스트 총 개수 가져오기
    public int getContractProgressListTotalCount(ContractProgressRequest contractProgressRequest){
        return salesManagementRepository.countContractProgressList(contractProgressRequest);
    }

    // 영업관리의 계약 완료현황의 조회에서 리스트 총 개수 가져오기
    public int getContractDoneListTotalCount(ContractDoneRequest contractDoneRequest){
        return salesManagementRepository.countContractDoneList(contractDoneRequest);
    }

    // 영업관리의 가맹점 ID 관리의 조회에서 리스트 총 개수 가져오기
    public int getManageIdListTotalCount(ManageIdRequest manageIdRequest){
        return salesManagementRepository.countManageIdList(manageIdRequest);
    }

    private void setupPaginationRequest(PaginationRequest request, Pagination pagination) {
        request.setFirstIndex(pagination.getFirstRecordIndex());
        request.setPageSize(pagination.getPageSize());
    }

    //페이징 설정
    public <T> Pagination createPagination(T request, int currentPage) {
        Pagination pagination = new Pagination();
        pagination.setCurrentPageNo(currentPage);
        if (request instanceof PaginationRequest) {
            setupPaginationRequest((PaginationRequest) request, pagination);
        }
        int totalCount = 0;
        if (request instanceof BasicInfoRequest) {
            totalCount = getBasicInfoListTotalCount((BasicInfoRequest) request);
        } else if (request instanceof BasicInfoViewRequest) {
            totalCount = getBasicInfoViewListTotalCount((BasicInfoViewRequest) request);
        } else if (request instanceof ContractProgressRequest) {
            totalCount = getContractProgressListTotalCount((ContractProgressRequest) request);
        } else if (request instanceof ContractDoneRequest) {
            totalCount = getContractDoneListTotalCount((ContractDoneRequest) request);
        } else if (request instanceof ManageIdRequest) {
            totalCount = getManageIdListTotalCount((ManageIdRequest) request);
        }
        pagination.setTotalCount(totalCount);
        return pagination;
    }

    //세션에서 사용자 id 가져와서 설정
    public void setUsernameFromSession(MemoRequest memoRequest, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        String username = (String)session.getAttribute("username");
        if (username == null) {
            log.error("사용자 id가 세션에 존재하지 않습니다.");
            throw new IllegalArgumentException("사용자 id가 세션에 존재하지 않습니다.");
        }else{
            memoRequest.setId(username);
            log.info("세션에서 사용자 id를 성공적으로 조회했습니다: {}", username);
        }
    }

    //저장 실패 시 에러 로그 출력
    private void logSaveError(String identifierType, String identifierValue, String message) {
        log.error("{} : {} {}", identifierType, identifierValue, message);
        throw new SaveFailedException();
    }
}
