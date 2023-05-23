package dio.me.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dio.me.credit.application.system.dto.CustomerDto
import dio.me.credit.application.system.dto.CustomerUpdateDto
import dio.me.credit.application.system.entity.Customer
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
import java.util.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    // injeção de acesso ao banco de dados
    @Autowired
    private lateinit var customerRepository: ICustomerRepository

    // "mockar" as requisições
    @Autowired
    private lateinit var mockMvc: MockMvc

    // Passar string na requisição
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Vinicius"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Meireles"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("31868676579"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("vinicius@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("123456"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua 1"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not save a customer with same CPF and return 409 status `() {
        //given
        customerRepository.save(builderCustomerDTO().toEntity())
        val customerDto: CustomerDto = builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not save a customer with empty firstName and return 400 status `() {
        //given
        val customerDto: CustomerDto = builderCustomerDTO(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString))
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

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Vinicius"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Meireles"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("31868676579"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("vinicius@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("123456"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua 1"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not find customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = 5
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
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
    fun `should delete customer by id and return 204 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = Random().nextLong()
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
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
    fun `should update customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        val updateCustomer: CustomerUpdateDto = builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(updateCustomer)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("ViniciusUPDATE"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("MeirelesUPDATE"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("654321"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua UPDATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(3000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("31868676579"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("vinicius@email.com"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = Random().nextLong()
        val updateCustomer: CustomerUpdateDto = builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(updateCustomer)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
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


    private fun builderCustomerDTO(
        firstName: String = "Vinicius",
        lastName: String = "Meireles",
        cpf: String = "31868676579",
        email: String = "vinicius@email.com",
        password: String = "12345",
        zipCode: String = "123456",
        street: String = "Rua 1",
        income: BigDecimal = BigDecimal.valueOf(1500.0),
    ): CustomerDto = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        zipCode = zipCode,
        street = street,
        income = income,
    )

    private fun builderCustomerUpdateDTO(
        firstName: String = "ViniciusUPDATE",
        lastName: String = "MeirelesUPDATE",
        zipCode: String = "654321",
        street: String = "Rua UPDATED",
        income: BigDecimal = BigDecimal.valueOf(3000.0),
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        income = income
    )
}