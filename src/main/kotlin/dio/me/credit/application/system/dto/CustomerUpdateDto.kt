package dio.me.credit.application.system.dto

import dio.me.credit.application.system.entity.Address
import dio.me.credit.application.system.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto (
    @field:NotEmpty(message="Invalid Input") val firstName: String,
    @field:NotEmpty(message="Invalid Input") val lastName: String,
    @field:NotNull val income: BigDecimal,
    @field:NotEmpty(message="Invalid Input") val zipCode: String,
    @field:NotEmpty(message="Invalid Input") val street: String,
) {
    fun toEntitiy(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
