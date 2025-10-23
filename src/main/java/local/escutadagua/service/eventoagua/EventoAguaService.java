package local.escutadagua.service.eventoagua;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import local.escutadagua.dto.eventoagua.EventoAguaDTO;
import local.escutadagua.dto.eventoagua.EventoAguaResponseDTO;
import local.escutadagua.model.EventoAgua;
import local.escutadagua.model.Usuario;
import local.escutadagua.model.enums.TipoEvento;
import local.escutadagua.repository.EventoAguaRepository;
import local.escutadagua.service.alerta.AlertaService;
import local.escutadagua.service.consumoagua.ConsumoAguaService;

@ApplicationScoped
public class EventoAguaService {

    @Inject
    EventoAguaRepository eventoAguaRepository;

    @Inject
    AlertaService alertaService; 

    @Inject
    ConsumoAguaService consumoAguaService; 

    // --- Regras de Negócio: Limite de Banho Longo ---
    // Exemplo: 10 Litros/min * 5 minutos = 50 Litros. Consideramos 50L um banho longo para esta demo.
    private static final double LIMITE_CHUVEIRO_LITROS = 50.0; 

    @Transactional
    public EventoAguaResponseDTO registrar(EventoAguaDTO eventoDTO) {
        // 1. Busca e Validação do Usuário (usando o método findById do Panache/JPA)
        Usuario usuario = Usuario.findById(eventoDTO.idUsuario());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // 2. Criação e Persistência da Entidade
        EventoAgua evento = new EventoAgua();
        evento.setUsuario(usuario);
        evento.setDataHoraEvento(LocalDateTime.now());
        evento.setTipoEvento(TipoEvento.valueOf(eventoDTO.tipoEvento()));
        evento.setGastoAgua(eventoDTO.gastoAgua());
        evento.setDuracaoEvento(eventoDTO.duracaoEvento());
        
        eventoAguaRepository.persist(evento);

        // 3. Processamento Adicional: Atualizar Consumo e Checar Alertas
        
        // a) Chamada ao service da Parte 2 (Stub por enquanto)
        consumoAguaService.registrarConsumo(evento);
        
        // b) Chamada à Regra de Negócio
        verificarConsumoExcessivo(evento);

        return EventoAguaResponseDTO.valueOf(evento);
    }
    
    // Método que implementa a regra de negócio para gerar Alertas
    private void verificarConsumoExcessivo(EventoAgua evento) {
        if (evento.getTipoEvento().name().equals("CHUVEIRO")) {
            if (evento.getGastoAgua() > LIMITE_CHUVEIRO_LITROS) {
                String detalhes = String.format("Banho longo: %.2f minutos (%.2f L) ultrapassou o limite de %.2f L.", 
                                                evento.getDuracaoEvento(), 
                                                evento.getGastoAgua(), 
                                                LIMITE_CHUVEIRO_LITROS);
                
                alertaService.criarAlertaConsumoExcessivo(evento.getUsuario(), detalhes);
            }
        }
    }

    public List<EventoAguaResponseDTO> buscarPorUsuario(Long idUsuario) {
        List<EventoAgua> eventos = eventoAguaRepository.list("usuario.id", idUsuario);
        return eventos.stream()
                      .map(EventoAguaResponseDTO::valueOf)
                      .collect(Collectors.toList());
    }
}