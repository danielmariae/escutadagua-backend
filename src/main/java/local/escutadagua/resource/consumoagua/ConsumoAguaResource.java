package local.escutadagua.resource.consumoagua;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.escutadagua.dto.consumoagua.ConsumoAguaResponseDTO;
import local.escutadagua.service.consumoagua.ConsumoAguaService;

@Path("/consumo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("User") 
public class ConsumoAguaResource {

    @Inject
    ConsumoAguaService consumoAguaService;

    // GET /consumo/{idUsuario}/hoje
    // Retorna o consumo agregado do dia atual para o dashboard
    @GET
    @Path("/{idUsuario}/hoje")
    public Response buscarConsumoHoje(@PathParam("idUsuario") Long idUsuario) {
        LocalDate hoje = LocalDate.now();
        ConsumoAguaResponseDTO consumo = consumoAguaService.buscarConsumoDiario(idUsuario, hoje);
        return Response.ok(consumo).build();
    }
    
    // GET /consumo/{idUsuario}/historico?dias=7
    // Retorna o histórico de consumo dos últimos N dias
    @GET
    @Path("/{idUsuario}/historico")
    public Response buscarHistorico(
        @PathParam("idUsuario") Long idUsuario, 
        @QueryParam("dias") int dias
    ) {
        // Se 'dias' não for fornecido, usa 7 por padrão
        if (dias <= 0) {
            dias = 7; 
        }
        
        List<ConsumoAguaResponseDTO> historico = consumoAguaService.buscarConsumoIntervalo(idUsuario, dias);
        return Response.ok(historico).build();
    }
}