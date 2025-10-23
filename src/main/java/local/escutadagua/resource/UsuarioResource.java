package local.escutadagua.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.escutadagua.dto.usuario.LoginDTO;
import local.escutadagua.dto.usuario.UsuarioDTO;
import local.escutadagua.dto.usuario.UsuarioResponseDTO;
import local.escutadagua.service.usuario.JwtService;
import local.escutadagua.service.usuario.UsuarioService;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    JwtService jwt;

    @Inject
    UsuarioService usuarioService;

    @POST
    @PermitAll
    @Path("/auth")
    public Response autenticar(LoginDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.autenticar(dto.email(), dto.senha());
        if (usuario != null) {
            String token = jwt.generateJwt(usuario);
            return Response.ok(usuario)
                           .header("Authorization", "Bearer "+ token)
                           .build();
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    public Response criar(UsuarioDTO usuarioDTO) {
        UsuarioResponseDTO usuario = usuarioService.cadastrar(usuarioDTO);
        return Response.status(Response.Status.CREATED).entity(usuario).build();
    }

    @GET
    @Path("email/{email}")
    public Response buscarPorMatricula(@PathParam("email") String email) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        return usuario != null 
            ? Response.ok(usuario).build() 
            : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return usuario != null 
            ? Response.ok(usuario).build() 
            : Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @GET
    public Response listarTodosUsuarios() {
        return Response.ok(usuarioService.listarTodos()).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, UsuarioDTO usuarioDTO) {
        UsuarioResponseDTO usuario = usuarioService.atualizar(id, usuarioDTO);
        return Response.ok(usuario).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        usuarioService.deletar(id);
        return Response.noContent().build();
    }
}