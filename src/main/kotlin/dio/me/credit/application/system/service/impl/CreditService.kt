package dio.me.credit.application.system.service.impl

import dio.me.credit.application.system.entity.Credit
import dio.me.credit.application.system.service.ICreditService
import java.util.*

class CreditService: ICreditService {
    override fun save(credit: Credit): Credit {
        TODO("Not yet implemented")
    }

    override fun findAllByCustomer(cusomerId: Long): List<Credit> {
        TODO("Not yet implemented")
    }

    override fun findByCreditCode(creditCode: UUID): Credit {
        TODO("Not yet implemented")
    }
}