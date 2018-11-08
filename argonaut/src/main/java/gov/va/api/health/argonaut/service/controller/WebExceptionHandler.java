package gov.va.api.health.argonaut.service.controller;

import gov.va.api.health.argonaut.api.Issue;
import gov.va.api.health.argonaut.api.Issue.IssueSeverity;
import gov.va.api.health.argonaut.api.Narrative;
import gov.va.api.health.argonaut.api.Narrative.NarrativeStatus;
import gov.va.api.health.argonaut.api.OperationOutcome;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient.BadRequest;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient.NotFound;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Exceptions that escape the rest controllers will be processed by this handler. It will convert
 * exception into different HTTP status codes and produce an error response payload.
 */
@RestControllerAdvice
@RequestMapping(produces = {"application/json"})
@Slf4j
public class WebExceptionHandler {

  @ExceptionHandler({BadRequest.class, javax.validation.ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public OperationOutcome handleBadRequest(Exception e, HttpServletRequest request) {
    return responseFor("structure", e, request);
  }

  @ExceptionHandler({NotFound.class, HttpClientErrorException.NotFound.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public OperationOutcome handleNotFound(Exception e, HttpServletRequest request) {
    return responseFor("not-found", e, request);
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public OperationOutcome handleSnafu(Exception e, HttpServletRequest request) {
    return responseFor("exception", e, request);
  }

  private OperationOutcome responseFor(String code, Exception e, HttpServletRequest request) {
    OperationOutcome response =
        OperationOutcome.builder()
            .id(UUID.randomUUID().toString())
            .text(
                Narrative.builder()
                    .status(NarrativeStatus.additional)
                    .div("<div>Failure: " + request.getRequestURI() + "</div>")
                    .build())
            .issue(
                Collections.singletonList(
                    Issue.builder()
                        .severity(IssueSeverity.fatal)
                        .code(code)
                        .diagnostics(
                            "Error: "
                                + e.getClass().getSimpleName()
                                + " Timestamp:"
                                + Instant.now())
                        .build()))
            .build();
    log.error("Response {}", response, e);
    return response;
  }
}
