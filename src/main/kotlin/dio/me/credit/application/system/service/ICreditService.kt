package dio.me.credit.application.system.service

import dio.me.credit.application.system.entity.Credit
import java.util.*

interface ICreditService {

    fun save(credit: Credit): Credit

    fun findAllByCustomer(cusomerId: Long): List<Credit>

    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
}