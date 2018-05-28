package org.springframework.hateoas

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
// TODO import fix

/**
 * Unit tests for [LinkBuilderDsl].
 *
 * @author Roland Kulcsár
 */
class LinkBuilderDslTest : TestUtils() {

    @Test
    fun `creates link to controller method`() {
        val self = linkTo<CustomerController> { findById("15") } withRel "self"

        assertPointsToMockServer(self)
        assertThat(self.rel).isEqualTo("self")
        assertThat(self.href).endsWith("/customers/15")
    }

    @Test
    fun `creates affordance to controller method`() {
        val delete = afford<CustomerController> { delete("15") }

        assertThat(delete.httpMethod).isEqualTo(HttpMethod.DELETE)
        assertThat(delete.name).isEqualTo("delete")
    }

    @Test
    fun `creates link to controller method with affordances`() {
        val self = linkTo<CustomerController> { findById("15") } withRel "self"
        val update = afford<CustomerController> { update("15", CustomerDto("John")) }
        val delete = afford<CustomerController> { delete("15") }

        val selfWithAffordances = self.andAffordances(listOf(update, delete))

        assertThat(selfWithAffordances.affordances).hasSize(3)
        assertThat(selfWithAffordances.hashCode()).isNotEqualTo(self.hashCode())
        assertThat(selfWithAffordances).isNotEqualTo(self)
    }

    @Test
    fun `adds links to wrapped domain object`() {
        val customer = Resource(Customer("15", "John Doe"))

        customer.add(CustomerController::class) {
            on { findById(it.content.id) } withRel "self"
            on { findProductsById(it.content.id) } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink("self")).isTrue()
        assertThat(customer.hasLink("products")).isTrue()
    }

    @Test
    fun `adds links to resource`() {
        val customer = CustomerResource("15", "John Doe")

        customer.add(CustomerController::class) {
            on { findById(it.id) } withRel "self"
            on { findProductsById(it.id) } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink("self")).isTrue()
        assertThat(customer.hasLink("products")).isTrue()
    }

    data class Customer(val id: String, val name: String)
    data class CustomerDto(val name: String)
    open class CustomerResource(val id: String, val name: String) : ResourceSupport()
    open class ProductResource(val id: String) : ResourceSupport()

    @RequestMapping("/customers")
    interface CustomerController {

        @GetMapping("/{id}")
        fun findById(@PathVariable id: String): ResponseEntity<CustomerResource>

        @GetMapping("/{id}/products")
        fun findProductsById(@PathVariable id: String): PagedResources<ProductResource>

        @PutMapping("/{id}")
        fun update(@PathVariable id: String, @RequestBody customer: CustomerDto): ResponseEntity<CustomerResource>

        @DeleteMapping("/{id}")
        fun delete(@PathVariable id: String): ResponseEntity<Unit>
    }
}