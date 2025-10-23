package local.escutadagua.resource.alerta;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.escutadagua.dto.alerta.AlertaResponseDTO;
import local.escutadagua.service.alerta.AlertaService;

@Path("/alertas")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("User") 
public class AlertaResource {

    @Inject
    AlertaService alertaService;

    // GET /alertas/usuario/{idUsuario}
    // Endpoint para o Flutter buscar todos os alertas do usuário
    @GET
    @Path("/usuario/{idUsuario}")
    public Response buscarPorUsuario(@PathParam("idUsuario") Long idUsuario) {
        List<AlertaResponseDTO> alertas = alertaService.buscarAlertasPorUsuario(idUsuario);
        return Response.ok(alertas).build();
    }

    // PUT /alertas/{id}/lido
    // Endpoint para marcar um alerta como lido
    @PUT
    @Path("/{id}/lido")
    public Response marcarComoLido(@PathParam("id") Long id) {
        try {
            AlertaResponseDTO alerta = alertaService.marcarComoLido(id);
            return Response.ok(alerta).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}