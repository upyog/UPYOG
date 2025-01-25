/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.22.595 on 2025-01-24 08:48:57.

export namespace Digit {

    interface RequestInfoWrapper {
        Transaction: Transaction;
        RequestInfo: RequestInfo;
    }

    interface UserResponse {
        access_token: string;
        ResponseInfo: ResponseInfo;
        UserRequest: User;
    }

    interface Transaction {
        tenantId: string;
        module: string;
        consumerCode: string;
        txnId: string;
        pdfUrl: string;
        fileStoreId: string;
        redirectUrl: string;
        signedFilestoreId: string;
        createdBy: string;
        createdTime: number;
        lastModifiedBy: string;
        lastModifiedTime: number;
    }

}
