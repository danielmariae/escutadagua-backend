package local.escutadagua.service.consumoagua;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import local.escutadagua.dto.consumoagua.ConsumoAguaResponseDTO;
import local.escutadagua.model.ConsumoAgua;
import local.escutadagua.model.EventoAgua;
import local.escutadagua.model.Usuario;
import local.escutadagua.repository.ConsumoAguaRepository;
import local.escutadagua.service.alerta.AlertaService;
import local.escutadagua.service.usuario.UsuarioService;

@ApplicationScoped
public class ConsumoAguaService {

    @Inject
    ConsumoAguaRepository consumoAguaRepository;

    @Inject
    UsuarioService usuarioService; // Para buscar o Usuario

    @Inject
    AlertaService alertaService; // Para checar alertas (Parte 3)

    /**
     * Agrega o consumo de um novo EventoAgua ao registro diário.
     * Método chamado pelo EventoAguaService.
     * @param evento O evento de água recém-criado.
     */
    @Transactional
    public void registrarConsumo(EventoAgua evento) {
        LocalDate hoje = evento.getDataHoraEvento().toLocalDate();
        Long idUsuario = evento.getUsuario().getId();

        // 1. Tenta buscar o consumo diário existente
        ConsumoAgua consumo = consumoAguaRepository.findByUsuarioAndDia(idUsuario, hoje);

        if (consumo == null) {
            // 2. Se não existir, cria um novo registro de consumo diário
            Usuario usuario = evento.getUsuario(); // Já veio carregado no evento
            
            consumo = new ConsumoAgua();
            consumo.setUsuario(usuario);
            consumo.setDiaConsumo(hoje.atStartOfDay()); // Armazena o início do dia
            consumo.setTotalConsumo(0.0);
            
            // Panache EntityBase não permite List<EventoAgua> diretamente
            // Devemos inicializar a lista de eventos se o modelo de dados permitir
            // Como estamos simplificando, vamos inicializar a lista
            if (consumo.getEventosDia() == null) {
                 consumo.setEventosDia(new java.util.ArrayList<>());
            }
        }

        // 3. Adiciona o evento e soma o consumo
        consumo.getEventosDia().add(evento);
        consumo.setTotalConsumo(consumo.getTotalConsumo() + evento.getGastoAgua());
        
        // 4. Persiste ou atualiza
        consumoAguaRepository.persist(consumo);
        
        // 5. Checa se o novo total ultrapassa a meta (Regra de Negócio)
        verificarMetaDiaria(consumo);
    }
    
    /**
     * Regra de negócio: verifica se o consumo total do dia ultrapassou a meta.
     */
    private void verificarMetaDiaria(ConsumoAgua consumo) {
        // O Usuario.metaDiaria precisa ser carregado do BD
        Usuario usuario = Usuario.findById(consumo.getUsuario().getId());
        double meta = usuario.getMetaDiaria(); // Assumindo que o campo foi adicionado ao model Usuario
        
        if (consumo.getTotalConsumo() > meta) {
             String detalhes = String.format("A meta diária de %.2f L foi ultrapassada! Total: %.2f L.", 
                                            meta, consumo.getTotalConsumo());
            
            // Chama o AlertaService (Stub da Parte 3)
            alertaService.criarAlertaAltoConsumo(usuario, detalhes);
        }
    }

    /**
     * Busca o ConsumoAgua por data.
     */
    public ConsumoAguaResponseDTO buscarConsumoDiario(Long idUsuario, LocalDate dia) {
        ConsumoAgua consumo = consumoAguaRepository.findByUsuarioAndDia(idUsuario, dia);
        
        if (consumo == null) {
             // Retorna um DTO vazio para o dia se não houver consumo
            return null;
        }
        return ConsumoAguaResponseDTO.valueOf(consumo);
    }
    
    /**
     * Busca o histórico de consumo em um intervalo (ex: últimos 7 dias).
     */
    public List<ConsumoAguaResponseDTO> buscarConsumoIntervalo(Long idUsuario, int dias) {
        // Lógica simplificada: Apenas lista os N últimos dias que têm registro.
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(dias);
        
        // Exemplo de query customizada no Panache
        List<ConsumoAgua> consumos = consumoAguaRepository.list(
            "usuario.id = ?1 and diaConsumo between ?2 and ?3 order by diaConsumo desc", 
            idUsuario, startDate, endDate
        );
        
        return consumos.stream()
                       .map(ConsumoAguaResponseDTO::valueOf)
                       .collect(Collectors.toList());
    }
}