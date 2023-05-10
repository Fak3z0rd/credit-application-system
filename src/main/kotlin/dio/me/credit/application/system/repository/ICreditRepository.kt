package dio.me.credit.application.system.repository

import dio.me.credit.application.system.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ICreditRepository: JpaRepository<Credit, Long> {
}