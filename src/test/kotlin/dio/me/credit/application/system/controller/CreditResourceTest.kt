package dio.me.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dio.me.credit.application.system.dto.CreditDto
import dio.me.credit.application.system.entity.Address
import dio.me.credit.application.system.entity.Credit
import dio.me.credit.application.system.entity.Customer
import dio.me.credit.application.system.repository.ICreditRepository
import dio.me.credit.application.system.repository.ICustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: ICreditRepository

    @Autowired
    private lateinit var customerRepository: ICustomerRepository

    // "mockar" as requisições
    @Autowired
    private lateinit var mockMvc: MockMvc

    // Passar string na requisição
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    /**
     * POST REQUESTS
     */
    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val fakeCustomer: Customer = customerRepository.save(buildCustomer())
        val creditDto: CreditDto = buildCreditDTO(customerId = fakeCustomer.id!!)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(BigDecimal.valueOf(1000.0)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("vinicius@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(1500.0))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not create a credit with invalid customerId and return 400 status`() {
        //given
        val fakeId: Long = Random().nextLong()
        val creditDto: CreditDto = buildCreditDTO(customerId = fakeId)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! Consult the documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class dio.me.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not create a credit with creditValue = 0 and return 400 status`() {
        //given
        val fakeCustomer: Customer = customerRepository.save(buildCustomer())
        val creditDto: CreditDto = buildCreditDTO(customerId = fakeCustomer.id!!, creditValue = BigDecimal.ZERO)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! Consult the documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    /**
     * GET REQUESTS
     */
    @Test
    fun `should get all credits by customerId and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDto1: CreditDto = buildCreditDTO(customerId = customer.id!!)
        val creditDto2: CreditDto = buildCreditDTO(customerId = customer.id!!, creditValue = BigDecimal.valueOf(1500.0))
        val valueAsString: String = objectMapper.writeValueAsString(listOf(creditDto1, creditDto2))
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should get credit by creditCode and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDto1: CreditDto = buildCreditDTO(customerId = customer.id!!)
        val credit: Credit = creditRepository.save(creditDto1.toEntity())
        val valueAsString: String = objectMapper.writeValueAsString(credit)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not get any credit with invalid creditCode and return 400 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDto1: CreditDto = buildCreditDTO(customerId = customer.id!!)
        val credit: Credit = creditRepository.save(creditDto1.toEntity())
        val valueAsString: String = objectMapper.writeValueAsString(credit)
        val invalidCreditCode: UUID = UUID.randomUUID()
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${invalidCreditCode}?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! Consult the documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class dio.me.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    private fun buildCreditDTO(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2023, 6, 10),
        numberOfInstallments: Int = 5,
        customerId: Long = 1L,
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun buildCustomer(
        firstName: String = "Vinicius",
        lastName: String = "Meireles",
        cpf: String = "31868676579",
        email: String = "vinicius@email.com",
        password: String = "12345",
        zipCode: String = "123456",
        street: String = "Rua 1",
        income: BigDecimal = BigDecimal.valueOf(1500.0),
    ): Customer = Customer(
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
    )
}