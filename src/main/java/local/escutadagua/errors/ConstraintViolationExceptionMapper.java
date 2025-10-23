package local.escutadagua.errors;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    
    @Context
    UriInfo uriInfo;
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(violation -> String.format(violation.getMessage())).collect(Collectors.toList());
        
        ErrorResponse errorResponse = new ErrorResponse(
            Response.Status.BAD_REQUEST.getStatusCode(),
            Response.Status.BAD_REQUEST.getReasonPhrase(),
            "Erros de validação",
            uriInfo.getPath()
        );
        
        // Adiciona a lista detalhada de erros
        errorResponse.setDetails(errors);
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(errorResponse)
                      .build();
    }
}