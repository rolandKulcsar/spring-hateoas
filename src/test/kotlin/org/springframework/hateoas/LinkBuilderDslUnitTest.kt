package org.springframework.hateoas

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Unit tests for [LinkBuilderDsl].
 *
 * @author Roland Kulcsár
 */
class LinkBuilderDslUnitTest : TestUtils() {

    @Test
    fun `creates link to controller method`() {
        val self = linkTo<CustomerController> { findById("15") } withRel Link.REL_SELF

        assertPointsToMockServer(self)
        assertThat(self.rel).isEqualTo(Link.REL_SELF)
        assertThat(self.href).endsWith("/customers/15")
    }

    @Test
    fun `creates affordance to controller method`() {
        val delete = afford<CustomerController> { delete("15") }

        assertThat(delete.httpMethod).isEqualTo(HttpMethod.DELETE)
        assertThat(delete.name).isEqualTo("delete")
    }

    @Test
    fun `creates link to controller method with an affordance`() {
        val self = linkTo<CustomerController> { findById("15") } withRel Link.REL_SELF
        val selfWithAffordance = self.andAffordance<CustomerController> { update("15", CustomerDto("John Doe")) }

        assertThat(selfWithAffordance.affordances).hasSize(2)
        assertThat(selfWithAffordance.hashCode()).isNotEqualTo(self.hashCode())
        assertThat(selfWithAffordance).isNotEqualTo(self)
    }

    @Test
    fun `creates link to controller method with affordances`() {
        val self = linkTo<CustomerController> { findById("15") } withRel Link.REL_SELF
        val selfWithAffordances = self andAffordances {
            afford<CustomerController> { update("15", CustomerDto("John Doe")) }
            afford<CustomerController> { delete("15") }
        }

        assertThat(selfWithAffordances.affordances).hasSize(3)
        assertThat(selfWithAffordances.hashCode()).isNotEqualTo(self.hashCode())
        assertThat(selfWithAffordances).isNotEqualTo(self)
    }

    @Test
    fun `adds links to wrapped domain object`() {
        val customer = Resource(Customer("15", "John Doe"))

        customer.add(CustomerController::class) {
            linkTo { findById(it.content.id) } withRel Link.REL_SELF
            linkTo { findProductsById(it.content.id) } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink(Link.REL_SELF)).isTrue()
        assertThat(customer.hasLink("products")).isTrue()
    }

    @Test
    fun `adds links to resource`() {
        val customer = CustomerResource("15", "John Doe")

        customer.add(CustomerController::class) {
            linkTo { findById(it.id) } withRel Link.REL_SELF
            linkTo { findProductsById(it.id) } withRel "products"
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink(Link.REL_SELF)).isTrue()
        assertThat(customer.hasLink("products")).isTrue()
    }

    @Test
    fun `adds links to resource with an affordance`() {
        val customer = CustomerResource("15", "John Doe")

        customer.add(CustomerController::class) {
            linkTo { findById(it.id) } withRel Link.REL_SELF andAffordance {
                update(it.id, CustomerDto("John Doe"))
            }
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink(Link.REL_SELF)).isTrue()

        val self = customer.getLink(Link.REL_SELF).get()
        assertThat(self.affordances).hasSize(2)
    }

    @Test
    fun `adds links to resource with affordances`() {
        val customer = CustomerResource("15", "John Doe")

        customer.add(CustomerController::class) {
            linkTo { findById(it.id) } withRel Link.REL_SELF andAffordances {
                afford { update(it.id, CustomerDto("John Doe")) }
                afford { delete(it.id) }
            }
        }

        customer.links.forEach { assertPointsToMockServer(it) }
        assertThat(customer.hasLink(Link.REL_SELF)).isTrue()

        val self = customer.getLink(Link.REL_SELF).get()
        assertThat(self.affordances).hasSize(3)
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