package dio.me.credit.application.system.service.impl

import dio.me.credit.application.system.entity.Customer
import dio.me.credit.application.system.repository.ICustomerRepository
import dio.me.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Repository
import java.lang.RuntimeException

class CustomerService(
    private val customerRepository: ICustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer {
        return this.customerRepository.save(customer)
    }

    override fun findById(id: Long): Customer {
        return this.customerRepository.findById(id).orElseThrow{
            throw RuntimeException("ID $id not found")
        }
    }

    override fun delete(id: Long) {
        this.customerRepository.deleteById(id)
    }
}