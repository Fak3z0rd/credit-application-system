package dio.me.credit.application.system.repository

import dio.me.credit.application.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ICustomerRepository: JpaRepository<Customer, Long> {

}