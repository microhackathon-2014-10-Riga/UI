package com.ofg.microservice

import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.ToString
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.POST

@Slf4j
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
        log.debug("We got new application: $applicationForNewLoan")
        String loanId = generateUniqueId(applicationForNewLoan)
        Client client = new Client(applicationForNewLoan, loanId)
        HttpStatus clientServiceResponseStatus = send(client, "client-service", "/api/client")
        validate(clientServiceResponseStatus)
        LoanApplication loanApplication = new LoanApplication(applicationForNewLoan, loanId)
        HttpStatus applicationServiceResponseStatus = send(loanApplication, "loan-application-service", "/api/loanApplication")
        validate(applicationServiceResponseStatus)
        return loanId
    }

    private String generateUniqueId(ApplicationForm applicationForNewLoan) {
        applicationForNewLoan.name + "_" + applicationForNewLoan.surname + "_" + UUID.randomUUID()
    }

    private void validate(HttpStatus clientServiceResponseStatus) {
        if (clientServiceResponseStatus.'2xxSuccessful') {
            throw new RuntimeException("fuckit")
        }
    }

    private HttpStatus send(Object stuffToSend, String serviceAlias, String url) {
        HttpStatus clientServiceResponseStatus = serviceRestClient.forService(serviceAlias).
                post().
                onUrl(url).
                body(stuffToSend).
                withHeaders().
                contentType(MediaType.APPLICATION_JSON).
                andExecuteFor().aResponseEntity().ofType(Object).statusCode
        return clientServiceResponseStatus
    }
}

@TypeChecked
@ToString
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