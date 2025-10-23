package local.escutadagua.errors;


import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    
    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);
    
    @Context
    UriInfo uriInfo;
    
    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Erro na API: ", exception);
        
        if (exception instanceof ApiException apiException) {
            ErrorResponse errorResponse = new ErrorResponse(
                apiException.getStatusCode(),
                Response.Status.fromStatusCode(apiException.getStatusCode()).getReasonPhrase(),
                apiException.getMessage(),
                uriInfo.getPath()
            );
            return Response.status(apiException.getStatusCode()).entity(errorResponse).build();
        }
        
        // Tratamento para outras exceções comuns
        ErrorResponse errorResponse = new ErrorResponse(
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
            Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Ocorreu um erro interno no servidor",
            uriInfo.getPath()
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
