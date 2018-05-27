package org.springframework.hateoas

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

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
    fun `adds links to wrapped domain object`() {
        val customer = Resource(Customer("15"))

        customer.add(CustomerController::class) {
            on { findById(it.content.id) } withRel "self"
            on { findProducts(it.content.id, "5") } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.getLink("self").isPresent).isTrue()
        assertThat(customer.getLink("products").isPresent).isTrue()
    }
    
    @Test
    fun `adds links to resource`() {
        val customer = CustomerResource("15")

        customer.add(CustomerController::class) {
            on { findById(it.id) } withRel "self"
            on { findProducts(it.id, "5") } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.getLink("self").isPresent).isTrue()
        assertThat(customer.getLink("products").isPresent).isTrue()
    }

    data class Customer(val id: String)

    open class CustomerResource(val id: String) : ResourceSupport()
    open class ProductResource(val id: String) : ResourceSupport()

    @RequestMapping("/customers")
    interface CustomerController {

        @GetMapping("/{id}")
        fun findById(@PathVariable id: String): CustomerResource

        @GetMapping("/{id}/products/{productId}")
        fun findProducts(@PathVariable id: String, @PathVariable productId: String): List<ProductResource>
    }
}