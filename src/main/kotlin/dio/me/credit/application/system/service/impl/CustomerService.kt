package dio.me.credit.application.system.service.impl

import dio.me.credit.application.system.entity.Customer
import dio.me.credit.application.system.exception.BusinessException
import dio.me.credit.application.system.repository.ICustomerRepository
import dio.me.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class CustomerService(
    private val customerRepository: ICustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer {
        return this.customerRepository.save(customer)
    }

    override fun findById(id: Long): Customer {
        return this.customerRepository.findById(id).orElseThrow{
            throw BusinessException("ID $id not found")
        }
    }

    override fun delete(id: Long) {
        val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }
}