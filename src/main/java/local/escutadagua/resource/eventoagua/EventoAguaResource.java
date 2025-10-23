package local.escutadagua.resource.eventoagua;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.escutadagua.dto.eventoagua.EventoAguaDTO;
import local.escutadagua.dto.eventoagua.EventoAguaResponseDTO;
import local.escutadagua.service.eventoagua.EventoAguaService;

@Path("/eventosagua")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Geralmente, endpoints de escrita são protegidos por autenticação
@RolesAllowed("User") 
public class EventoAguaResource {

    @Inject
    EventoAguaService eventoAguaService;

    // POST /eventosagua
    // Endpoint para o Flutter enviar os dados de um evento detectado
    @POST
    public Response criar(EventoAguaDTO eventoDTO) {
        try {
            EventoAguaResponseDTO evento = eventoAguaService.registrar(eventoDTO);
            return Response.status(Response.Status.CREATED).entity(evento).build();
        } catch (IllegalArgumentException e) {
            // Retorna 404/400 se o usuário ou outro dado obrigatório for inválido
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno: " + e.getMessage()).build();
        }
    }

    // GET /eventosagua/usuario/{idUsuario}
    // Endpoint para o Flutter buscar o histórico
    @GET
    @Path("/usuario/{idUsuario}")
    public Response buscarPorUsuario(@PathParam("idUsuario") Long idUsuario) {
        List<EventoAguaResponseDTO> eventos = eventoAguaService.buscarPorUsuario(idUsuario);
        return Response.ok(eventos).build();
    }
}