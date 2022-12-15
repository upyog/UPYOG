/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.22.595 on 2022-12-12 10:18:32.

export namespace Digit {

    interface PaymentRequest {
        RequestInfo: RequestInfo;
        Payment: Payment;
    }

    interface PaymentResponse {
        ResponseInfo: ResponseInfo;
        Payments: Payment[];
    }

    interface PaymentSearchCriteria {
        ids: string[];
        billIds: string[];
        tenantId: string;
        receiptNumbers: string[];
        status: string[];
        instrumentStatus: string[];
        paymentModes: string[];
        payerIds: string[];
        consumerCodes: string[];
        transactionNumber: string;
        mobileNumber: string;
        fromDate: number;
        toDate: number;
        offset: number;
        limit: number;
    }

    interface RequestInfoWrapper {
        RequestInfo: RequestInfo;
    }

    interface UserResponse {
        ResponseInfo: ResponseInfo;
        UserRequest: User;
    }

    interface Payment {
        id: string;
        tenantId: string;
        totalDue: number;
        totalAmountPaid: number;
        transactionNumber: string;
        transactionDate: number;
        paymentMode: PaymentModeEnum;
        instrumentDate: number;
        instrumentNumber: string;
        instrumentStatus: InstrumentStatusEnum;
        ifscCode: string;
        auditDetails: AuditDetails;
        additionalDetails: any;
        paymentDetails: PaymentDetail[];
        paidBy: string;
        mobileNumber: string;
        payerName: string;
        payerAddress: string;
        payerEmail: string;
        payerId: string;
        paymentStatus: PaymentStatusEnum;
        fileStoreId: string;
    }

    interface AuditDetails {
        createdBy: string;
        lastModifiedBy: string;
        createdTime: number;
        lastModifiedTime: number;
    }

    interface PaymentDetail {
        id: string;
        tenantId: string;
        totalDue: number;
        totalAmountPaid: number;
        receiptNumber: string;
        receiptDate: number;
        receiptType: string;
        businessService: string;
        billId: string;
        bill: Bill;
        additionalDetails: any;
        auditDetails: AuditDetails;
    }

    interface Bill {
        id: string;
        mobileNumber: string;
        paidBy: string;
        payerName: string;
        payerAddress: string;
        payerEmail: string;
        payerId: string;
        status: BillStatusEnum;
        reasonForCancellation: string;
        isCancelled: boolean;
        additionalDetails: any;
        billDetails: BillDetail[];
        tenantId: string;
        auditDetails: AuditDetails;
        collectionModesNotAllowed: string[];
        partPaymentAllowed: boolean;
        isAdvanceAllowed: boolean;
        minimumAmountToBePaid: number;
        businessService: string;
        totalAmount: number;
        consumerCode: string;
        billNumber: string;
        billDate: number;
        amountPaid: number;
    }

    interface BillDetail {
        billDescription: string;
        displayMessage: string;
        callBackForApportioning: boolean;
        cancellationRemarks: string;
        id: string;
        tenantId: string;
        demandId: string;
        billId: string;
        amount: number;
        amountPaid: number;
        fromPeriod: number;
        toPeriod: number;
        additionalDetails: any;
        channel: string;
        voucherHeader: string;
        boundary: string;
        manualReceiptNumber: string;
        manualReceiptDate: number;
        billAccountDetails: BillAccountDetail[];
        collectionType: CollectionType;
        auditDetails: AuditDetails;
        expiryDate: number;
    }

    interface BillAccountDetail {
        id: string;
        tenantId: string;
        billDetailId: string;
        demandDetailId: string;
        order: number;
        amount: number;
        adjustedAmount: number;
        isActualDemand: boolean;
        taxHeadCode: string;
        additionalDetails: any;
        purpose: Purpose;
        auditDetails: AuditDetails;
    }

    interface CollectionType extends CollectionLikeType {
    }

    interface Purpose {
    }

    interface CollectionLikeType extends TypeBase {
    }

    interface TypeBase extends JavaType, JsonSerializable {
    }

    interface JavaType extends ResolvedType, Serializable, Type {
        interfaces: JavaType[];
        genericSignature: string;
        contentType: JavaType;
        bindings: TypeBindings;
        referencedType: JavaType;
        erasedSignature: string;
        contentValueHandler: any;
        contentTypeHandler: any;
        superClass: JavaType;
        keyType: JavaType;
        valueHandler: any;
        typeHandler: any;
        javaLangObject: boolean;
    }

    interface JsonSerializable {
    }

    interface TypeBindings extends Serializable {
        empty: boolean;
        typeParameters: JavaType[];
    }

    interface Class<T> extends Serializable, GenericDeclaration, Type, AnnotatedElement {
    }

    interface ResolvedType {
        interface: boolean;
        primitive: boolean;
        abstract: boolean;
        final: boolean;
        contentType: ResolvedType;
        enumType: boolean;
        concrete: boolean;
        collectionLikeType: boolean;
        referencedType: ResolvedType;
        parameterSource: Class<any>;
        rawClass: Class<any>;
        containerType: boolean;
        keyType: ResolvedType;
        throwable: boolean;
        mapLikeType: boolean;
        referenceType: boolean;
        arrayType: boolean;
    }

    interface Serializable {
    }

    interface Type {
        typeName: string;
    }

    interface GenericDeclaration extends AnnotatedElement {
        typeParameters: TypeVariable<any>[];
    }

    interface AnnotatedElement {
        annotations: Annotation[];
        declaredAnnotations: Annotation[];
    }

    interface TypeVariable<D> extends Type, AnnotatedElement {
        name: string;
        annotatedBounds: AnnotatedType[];
        bounds: Type[];
        genericDeclaration: D;
    }

    interface Annotation {
    }

    interface AnnotatedType extends AnnotatedElement {
        type: Type;
    }

    type PaymentModeEnum = "CASH" | "CHEQUE" | "DD" | "ONLINE" | "OFFLINE_NEFT" | "OFFLINE_RTGS" | "ONLINE_NEFT" | "ONLINE_RTGS" | "POSTAL_ORDER" | "CARD";

    type InstrumentStatusEnum = "APPROVED" | "APPROVAL_PENDING" | "TO_BE_SUBMITTED" | "REMITTED" | "REJECTED" | "CANCELLED" | "DISHONOURED";

    type PaymentStatusEnum = "NEW" | "DEPOSITED" | "CANCELLED" | "DISHONOURED" | "RECONCILED";

    type BillStatusEnum = "ACTIVE" | "CANCELLED" | "PAID" | "EXPIRED";

}
