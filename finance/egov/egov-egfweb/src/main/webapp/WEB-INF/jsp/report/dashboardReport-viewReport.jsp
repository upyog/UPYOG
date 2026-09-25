<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<div xmlns:s="http://www.w3.org/1999/XSL/Transform">
    <h3>Dashboard</h3>

    <div style="height: 20px;">

    </div>

    <div style="padding-bottom: 20px; font-family: semibold; font-size: 16px; font-weight: bold;" >Bills and Vouchers From <s:property value="startDate"/> to <s:property value="endDate"/></div>

    <div class="row">
        <!--<div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">DataS</div>
                <div class="panel-body text-center">
                    <h5><s:property value="dataS" /></h5>
                </div>
            </div>
        </div>-->
        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Expense Bills</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalExpenseBills" /></h5>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Contractor Bills</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalContractorBills" /></h5>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Supplier Bill</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalSupplierBills" /></h5>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Journal Vouchers</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalJournalVouchers" /></h5>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Bills Payment</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalBillsPayment" /></h5>
                </div>
            </div>
        </div>

    </div>

    <div style="padding-bottom: 20px; font-family: semibold; font-size: 16px; font-weight: bold;" >Masters</div>

    <div class="row" style="height: 20px;" >

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Work Orders</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalWorkOrders" /></h5>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Purchase Orders</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalPurchaseOrders" /></h5>
                </div>
            </div>
        </div>


        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Funds</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalFunds" /></h5>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Bank Accounts</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalBankAccounts" /></h5>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Contractors</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalContractors" /></h5>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading text-center">Total Suppliers</div>
                <div class="panel-body text-center">
                    <h5><s:property value="totalSuppliers" /></h5>
                </div>
            </div>
        </div>




    </div>



    <!--    <s:iterator value="contractors" var="contractor">-->
    <!--        <p>Contractor Name: <s:property value="name" /><
        },
        {/p>-->
    <!--    </s:iterator>-->


</div>