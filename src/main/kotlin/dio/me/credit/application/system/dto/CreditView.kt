package dio.me.credit.application.system.dto

import dio.me.credit.application.system.entity.Credit
import dio.me.credit.application.system.enummeration.Status
import java.math.BigDecimal
import java.util.*

data class CreditView(
    var creditCode: UUID,
    var creditValue: BigDecimal,
    var numberOfInstallments: Int,
    var status: Status,
    var emailCustomer: String?,
    var incomeCustomer: BigDecimal?
){
    constructor(credit: Credit): this (
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments,
        status = credit.status,
        emailCustomer = credit.customer?.email,
        incomeCustomer = credit.customer?.income
    )

}
