package com.ofg.microservice

import com.codahale.metrics.Counter
import com.codahale.metrics.MetricRegistry
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.ToString
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.POST

@Slf4j
@TypeChecked
@RestController
@RequestMapping('/application')
class NewLoanController {
    private final ServiceRestClient serviceRestClient
    private final Counter counter

    @Autowired
    NewLoanController(ServiceRestClient serviceRestClient, MetricRegistry metricRegistry) {
        this.serviceRestClient = serviceRestClient
        counter = metricRegistry.counter("application.new")
    }

    @RequestMapping(method = POST)
    String apply(@RequestBody ApplicationForm applicationForNewLoan) {
        counter.inc()
        log.debug("NEW APPLICATION RECEIVED FROM WEB: $applicationForNewLoan")
        String loanId = generateUniqueId(applicationForNewLoan)
        log.debug("LoanID assigned: $loanId")
        Client client = new Client(applicationForNewLoan, loanId)
        log.debug("SENDING NEW CLIENT to ClientService: $client")
        HttpStatus clientServiceResponseStatus = send(client, "client-service", "/api/client")
        log.debug("Received $clientServiceResponseStatus from ClientService")
        LoanApplication loanApplication = new LoanApplication(applicationForNewLoan, loanId)
        log.debug("SENDING NEW Application to applciation service: $applicationForNewLoan")
        HttpStatus applicationServiceResponseStatus = send(loanApplication, "loan-application-service", "/api/loanApplication")
        log.debug("Received $applicationServiceResponseStatus from ApplicationService")
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
        try {
            return serviceRestClient.forService(serviceAlias).
                    post().
                    onUrl(url).
                    body(stuffToSend).
                    withHeaders().
                    contentType(MediaType.APPLICATION_JSON).
                    andExecuteFor().aResponseEntity().ofType(Object).statusCode
        } catch(Exception e) {
            log.error("Failed", e)
            throw new CollaboratorError(e)
        }
    }
}

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class CollaboratorError extends RuntimeException {
    CollaboratorError(Throwable throwable) {
        super("Collaborator error: " + throwable.message, throwable)
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
@ToString
class LoanApplication {
    String firstName
    String lastName
    Integer age
    String jobPosition
    BigDecimal amount
    String loanId

    LoanApplication(ApplicationForm applicationForm, String loanId) {
        this.firstName = applicationForm.name
        this.lastName = applicationForm.surname
        this.age = applicationForm.age
        this.jobPosition = applicationForm.jobPosition
        this.amount = applicationForm.amount
        this.loanId = loanId
    }
}

@TypeChecked
@ToString
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