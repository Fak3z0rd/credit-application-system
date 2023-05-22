package dio.me.credit.application.system.service

import dio.me.credit.application.system.entity.Address
import dio.me.credit.application.system.entity.Credit
import dio.me.credit.application.system.entity.Customer
import dio.me.credit.application.system.enummeration.Status
import dio.me.credit.application.system.exception.BusinessException
import dio.me.credit.application.system.repository.ICreditRepository
import dio.me.credit.application.system.service.impl.CreditService
import dio.me.credit.application.system.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: ICreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService


    @Test
    fun `should create credit`() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit = buildCredit(customer = fakeCustomer)
        every { customerService.findById(any()) } returns fakeCustomer
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual: Credit = creditService.save(fakeCredit)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `should get all credits of a customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        val fakeCredit1 = buildCredit(customer = fakeCustomer)
        val fakeCredit2 = buildCredit(customer = fakeCustomer, creditValue = BigDecimal.valueOf(1000.0))
        val fakeList: List<Credit> = listOf(fakeCredit1, fakeCredit2)
        every { creditRepository.findAllByCustomerId(any()) } returns fakeList
        //when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeId)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeList)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeId) }
    }

    @Test
    fun `should find credit by creditCode`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        val fakeCredit: Credit = buildCredit(creditCode = fakeCreditCode, customer = fakeCustomer)
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit
        //when
        val actual: Credit = creditService.findByCreditCode(fakeId, fakeCreditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(actual).isEqualTo(fakeCredit)
        Assertions.assertThat(actual).isSameAs(fakeCredit)
    }

    @Test
    fun `should not find credit by invalid creditCode`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCreditCode: UUID = UUID.randomUUID()
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns null
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeId,fakeCreditCode) }
            .withMessage("CreditCode $fakeCreditCode not found")
    }

    private fun buildCustomer(
        firstName: String = "Vinicius",
        lastName: String = "Meireles",
        cpf: String = "31868676579",
        email: String = "vinicius@email.com",
        password: String = "12345",
        zipCode: String = "123456",
        street: String = "Rua 1",
        income: BigDecimal = BigDecimal.valueOf(1500.0),
        id: Long = 1L,
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income,
        id = id
    )

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        dayFirstInstallment: LocalDate = LocalDate.now().plusDays(3),
        numberOfInstallments: Int = 12,
        status: Status = Status.IN_PROGRESS,
        customer: Customer,
        id: Long = 1L,
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )
}




