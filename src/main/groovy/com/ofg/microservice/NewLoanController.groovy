package com.ofg.microservice
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.POST

@TypeChecked
@RestController
@RequestMapping('/application')
class NewLoanController {
    private final ServiceRestClient serviceRestClient

    @Autowired
    NewLoanController(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient
    }

    @RequestMapping(method = POST)
    String apply(@RequestBody ApplicationForm applicationForNewLoan) {
        String loanId =  applicationForNewLoan.name + "/" + applicationForNewLoan.surname + "/" + UUID.randomUUID()
        Client client = new Client(applicationForNewLoan, loanId)
        HttpStatus clientServiceResponseStatus = serviceRestClient.forService("client-service").
                post().
                onUrl("/api/client").
                body(client).
                withHeaders().
                    contentType(MediaType.APPLICATION_JSON).
                andExecuteFor().aResponseEntity().ofType(Object).statusCode

        if(clientServiceResponseStatus.'2xxSuccessful') {
            throw new RuntimeException("fuckit")
        }

        HttpStatus applicationServiceResponseStatus = serviceRestClient.forService("loan-application-service").
                post().
                onUrl("api/loanApplication").
                body(new LoanApplication(applicationForNewLoan, loanId)).
                withHeaders().
                contentType(MediaType.APPLICATION_JSON).
                andExecuteFor().aResponseEntity().ofType(Object).statusCode

        if(applicationServiceResponseStatus.'2xxSuccessful') {
            throw new RuntimeException("fuckit2")
        }

        return loanId
    }
}

@TypeChecked
//TODO: validation
class ApplicationForm {
    String name
    String surname
    Integer age
    String jobPosition
    BigDecimal amount
}

@TypeChecked
class LoanApplication {
    BigDecimal amount
    String loanId

    LoanApplication(ApplicationForm applicationForNewLoan, String loanId) {
        this.loanId = loanId
        amount = applicationForNewLoan.amount
    }
}

@TypeChecked
class Client {
    String firstName
    String lastName
    Integer age
    String loanId

    Client(ApplicationForm applicationForNewLoan, String loanId) {
        firstName = applicationForNewLoan.name
        lastName = applicationForNewLoan.surname
        age = applicationForNewLoan.age
        this.loanId = loanId
    }
}