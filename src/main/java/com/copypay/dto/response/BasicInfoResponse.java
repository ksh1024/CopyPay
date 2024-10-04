package com.copypay.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoResponse {
    /* 계약 */
    private String businessRegNumber;  //사업자번호
    private String mid;  //MID
    private String businessName;  //상호
    private String effectiveDate;  //적용일
    private String completionDate;  //완료일
    private String contractDate;  //계약일자
    private String ceoName;  //대표자명
    private String ceoBirth;  //대표자 생년월일
    private String ceoHp;  //대표자 HP
    private String ceoTel;  //대표 TEL
    private String ceoEmail;  //대표 E-Mail
    private String ceoFax;  //대표 FAX
    private String businessPostcode;  //사업장 우편번호
    private String businessAddressLine1;  //사업장 주소1
    private String businessAddressLine2;  //사업장 주소2
    private String businessType;  //사업체구분
    private String franchiseContractManagerName;  //가맹점 계약담당 성명
    private String franchiseContractManagerTel;//가맹점 계약담당 TEL
    private String franchiseContractManagerHp;//가맹점 계약담당 HP
    private String franchiseContractManagerEmail;//가맹점 계약담당 EMail
    private String applicationPath;  //접수경로
    private String contractManager;  //계약담당자
    private String salesManager;  //영업담당자
    /* 정산정보 */
    private String paymentMethod;  //지급방법
    private String feePaymentMethod;  //수수료 지급방법
    private String feeSettlementDate;  //수수료 정산일자
    private String depositAccountFormat;  //정산 입금 통장 인자 표기 방법
    private String accountParameterName;  //통장 인자명
    private String taxInvoiceCriteria;  //세금계산서 발행 기준
    private String taxInvoiceVat;  //세금계산서 부가세 계산 방식
    private String taxInvoiceEmail;  //세금계산서 이메일
    private String partialCancellationAllowed;  //부분취소 가능 여부
}
